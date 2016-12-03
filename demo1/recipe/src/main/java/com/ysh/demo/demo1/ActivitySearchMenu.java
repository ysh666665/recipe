package com.ysh.demo.demo1;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ysh.demo.demo1.gsonbean.SearchResultBean;
import com.ysh.demo.demo1.litepalbean.HistoryBean;
import com.ysh.demo.demo1.retrofit.ApiException;
import com.ysh.demo.demo1.retrofit.HttpUtils;
import com.ysh.demo.demo1.utils.NetworkJugde;
import com.ysh.demo.demo1.utils.Toastutil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ActivitySearchMenu extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView mHistoryRecyclerView;
    private EditText mEdittext;
    private ProgressBar mProgressBar;
    private ImageButton btnBack;
    private ImageButton btnDeletHistory;
    private LinearLayout historyContainer;

    private static final String TAG = "ActivitySearchMenu";
    private Subscription subscription;
    public static final String SEARCH_CONTENT = "con";
    private String searchContent;
    private String currentSearch;
    private int currentSearchPage = 1;
    private List<SearchResultBean.ResultBean.ListBean> mList;
    private NetworkJugde networkJugde;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mHistoryAdapter;
    private List<String> mHistorySearchs;
    private final int notFoundCode = 404;
    private boolean loadAllFinished = false;
    private ActivitySearchMenu mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);
        mContext = this;
        initView();
        networkJugde = NetworkJugde.getInstance();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getIntentData();

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mEdittext.clearFocus();
            String s = bundle.getString(SEARCH_CONTENT);
            if (s != null) {
                searchContent = s;
                subscription = searchMenus(searchContent, 1);
            }
        } else {
//            Log.d(TAG, "onCreate: 111111111111111111111111111");
            mEdittext.setFocusable(true);
            mEdittext.requestFocus();
            //在此显示搜索历史
            getHistoriesData();
            if (mHistorySearchs != null && mHistorySearchs.size() > 0) {
                historyContainer.setVisibility(View.VISIBLE);
                mHistoryAdapter = getHistoryAdapter();
                mHistoryRecyclerView.setAdapter(mHistoryAdapter);
            }

        }
//        Log.d(TAG, "onCreate: " + searchContent);
    }

    public static void actionStartActivity(Activity activity, @Nullable String s) {
        Intent intent = new Intent(activity, ActivitySearchMenu.class);
        if (s != null) {
            intent.putExtra(SEARCH_CONTENT, s);
        }
        activity.startActivity(intent);
    }

    public void onClickAtActivitySearchMenu(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                //手动查询
                String s = String.valueOf(mEdittext.getText());
                Log.d(TAG, "onClickAtActivitySearchMenu: " + s);
                if (s != null && !s.isEmpty()) {
                    if (mList != null) {
                        mList.clear();
                    }
                    currentSearch = null;
                    currentSearchPage = 1;
                    subscription = searchMenus(s, 1);
                }

                break;
//            case R.id.menuItem:
            //进入细节menuItem:
//                String menuId = (String) v.getTag();
//                Log.d(TAG, "onClickAtActivitySearchMenu: " + menuId);
//                if (menuId != null) {
//                    ActivityMenuDetail.actionStartActivity(this, menuId);
//                }
//                break;
        }
    }

    private void initView() {
        mEdittext = (EditText) findViewById(R.id.editText);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvResult);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbarActivitySearchMenu);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        historyContainer = (LinearLayout) findViewById(R.id.ll_history_container);
        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.rv_history);
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnDeletHistory = (ImageButton) findViewById(R.id.iv_delete);
        btnDeletHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.deleteAll(HistoryBean.class);
                historyContainer.setVisibility(View.GONE);
                mHistorySearchs.clear();
            }
        });

    }

    private Subscription searchMenus(@NonNull final String s, final int page) {
        //绑定RecylerView
        //先判断网络
        Subscription sub;
        if (!networkJugde.isNetworkConnected(this)) {
            Snackbar.make(mRecyclerView, "当前没有网络", Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    searchMenus(s, currentSearchPage);
                }
            }).show();
            return null;
        } else if (!loadAllFinished) {
            mProgressBar.setVisibility(View.VISIBLE);
            if (subscription != null && subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            Observable<SearchResultBean> call;
            if (s == searchContent) {
//                Log.d(TAG, "searchMenus1: " + searchContent);
                call = (Observable<SearchResultBean>) HttpUtils.getInstance().getCall(HttpUtils.SEARCH_BY_LABLE_ID, s, page);
            } else {
//                Log.d(TAG, "searchMenus2: " + s);
                //只有手动输入的才能储存为搜索记录
                call = (Observable<SearchResultBean>) HttpUtils.getInstance().getCall(HttpUtils.SEARCH_FREELY, s, page);
            }
            sub = call.map(new Func1<SearchResultBean, SearchResultBean>() {
                @Override
                public SearchResultBean call(SearchResultBean searchResultBean) {
                    int retCode = searchResultBean.getRetCode();
//                    Log.d(TAG, "call: " + retCode);
                    if (retCode != 200) {
                        throw new ApiException(retCode);
                    } else if (searchResultBean.getResult().getList().size() == 0) {
                        throw new ApiException(notFoundCode);
                    }
                    return searchResultBean;
                }
            })
                    .map(new Func1<SearchResultBean, List<SearchResultBean.ResultBean.ListBean>>() {
                        @Override
                        public List<SearchResultBean.ResultBean.ListBean> call(SearchResultBean searchResultBean) {
                            return searchResultBean.getResult().getList();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<List<SearchResultBean.ResultBean.ListBean>>() {
                        @Override
                        public void onCompleted() {
//                            Log.d(TAG, "onCompleted: 当前页码" + currentSearchPage);
                            historyContainer.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            if (!s.contains("0")) {
                                if (mHistorySearchs == null || mHistorySearchs.size() == 0) {
                                    saveHistory(s);
                                } else if (!mHistorySearchs.contains(s)) {
                                    saveHistory(s);
                                }
                            }
                            currentSearch = s;
                            currentSearchPage = page;
                            if (mList != null && mList.size() != 0) {
//                                mRecyclerView.setAdapter(new RecyclerViewMenusAdapter(mContext, mList));
                                if (mAdapter == null) {
                                    mAdapter = getSearchResultAdapter();
                                    mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            mProgressBar.setVisibility(View.GONE);
//                            Toastutil.showToast(mContext, "查不到相关菜谱");
//                            Log.d(TAG, "onError: ");
                            if (e instanceof ApiException) {
                                ApiException error = (ApiException) e;
                                int retCode = error.getRetCode();
//                                Log.d(TAG, "onError: " + retCode);
                                if (retCode == 20201) {
//                                    Log.d(TAG, "onError: " + "查不到相关菜谱");
                                    Snackbar.make(mRecyclerView, "查不到相关菜谱", Snackbar.LENGTH_LONG).setAction("确定", null).show();
                                } else if (retCode == notFoundCode) {
                                    loadAllFinished = true;
                                }
                            } else {
                                Snackbar.make(mRecyclerView, "获取数据失败", Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        searchMenus(s, currentSearchPage);
                                    }
                                }).show();
                            }
                        }

                        @Override
                        public void onNext(List<SearchResultBean.ResultBean.ListBean> listBeen) {

                            if (mList == null) {
                                mList = listBeen;
                            } else {
                                for (SearchResultBean.ResultBean.ListBean bean : listBeen) {
                                    mList.add(bean);
                                }
                            }
                        }
                    });
        } else {
            return null;
        }
        return sub;
    }

    private void saveHistory(@NonNull String s) {
        HistoryBean historyBean = new HistoryBean();
        historyBean.setHistory(s);
        historyBean.save();
    }

    private CommonAdapter<String> getHistoryAdapter() {
        return new CommonAdapter<String>(mContext, R.layout.history_item, mHistorySearchs) {

            @Override
            protected void convert(ViewHolder holder, final String s, int position) {
                holder.setText(R.id.tv_history_item, s);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscription = searchMenus(s, 1);
//                        历史记录显示消失
                        historyContainer.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

    private void getHistoriesData() {
        /*
        * 数据库数据转化为adapter加载的数据
        * 为view设置adapter
        * */
        mHistorySearchs = new ArrayList<>();
        List<HistoryBean> beans = DataSupport.findAll(HistoryBean.class);
        if (beans != null && beans.size() > 0) {
            for (HistoryBean bean : beans) {
                mHistorySearchs.add(bean.getHistory());
            }
        }
    }


    private RecyclerView.Adapter getSearchResultAdapter() {
        LoadMoreWrapper adapter = new LoadMoreWrapper(new CommonAdapter<SearchResultBean.ResultBean.ListBean>(mContext, R.layout.search_result_item, mList) {
            @Override
            protected void convert(ViewHolder holder, SearchResultBean.ResultBean.ListBean listBean, int position) {
                ImageView iv = holder.getView(R.id.itemIcon);
//                Log.d(TAG, "convert: " + listBean.getName());
                Glide.with(mContext).load(listBean.getThumbnail()).error(R.mipmap.ic_menu_defaut).into(iv);
                holder.setText(R.id.itemTitle, listBean.getName());
                SearchResultBean.ResultBean.ListBean.RecipeBean recipe = listBean.getRecipe();
                if (recipe != null) {
                    String desc = recipe.getSumary();
                    if (desc == null) {
                        desc = recipe.getTitle();
                    }
                    holder.setText(R.id.itemDesc, desc);
                }
                holder.itemView.setTag(listBean.getMenuId());

                holder.setOnClickListener(R.id.menuItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (networkJugde.isNetworkConnected(mContext)) {
                            String menuId = (String) v.getTag();
//                            Log.d(TAG, "onClickAtActivitySearchMenu: " + menuId);
                            if (menuId != null) {
                                ActivityMenuDetail.actionStartActivity(mContext, menuId);
                            }
                        } else {
                            Toastutil.showToast(mContext, "请打开网络");
                        }

                    }
                });
            }
        });
        adapter.setLoadMoreView(R.layout.load_more_view);
        adapter.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!loadAllFinished) {
                    searchMenus(currentSearch, currentSearchPage + 1);
                }
            }
        });


        return adapter;
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
