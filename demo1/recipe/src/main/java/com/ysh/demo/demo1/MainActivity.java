package com.ysh.demo.demo1;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ysh.demo.demo1.adapter.MainPagePagerAdapter;
import com.ysh.demo.demo1.fragments.MainListFragment;
import com.ysh.demo.demo1.gsonbean.CategoryBean;
import com.ysh.demo.demo1.retrofit.HttpUtils;
import com.ysh.demo.demo1.utils.NetworkJugde;


import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/*
* Rxjava+retrofit2
* 例子
* */

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ProgressBar mProgressbar;
    private List<MainListFragment> fragmentList;
    private static final String TAG = "MainActivity";
    private MainActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        fragmentList = new ArrayList<>();
        initView();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mProgressbar = (ProgressBar) findViewById(R.id.progressbarActivityMain);
        mProgressbar.setVisibility(View.VISIBLE);
        mTabLayout.setupWithViewPager(mViewPager);
        getCategory();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCollections:
                //进入收藏
                ActivityCollections.actionStartActivity(mContext);
                break;
            case R.id.btnSearchBox:
                //跳转搜索页面
                ActivitySearchMenu.actionStartActivity(mContext, null);
                break;
            case R.id.mainRecyclerViewItem:
                //跳转搜索页面
                TextView textView = (TextView) view.findViewById(R.id.tv_item);
                String cid = (String) textView.getTag();
                ActivitySearchMenu.actionStartActivity(mContext, cid);
//                Log.d(TAG, "onClick: "+cid);
                break;
        }
    }

    private void getCategory() {
        if (!NetworkJugde.getInstance().isNetworkConnected(this)) {
            mProgressbar.setVisibility(View.GONE);
            Snackbar.make(mViewPager, "当前没有网络", Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getCategory();
                }
            }).show();

        } else {
            @SuppressWarnings("unchecked")
            Observable<CategoryBean> call = (Observable<CategoryBean>) HttpUtils.getInstance().getCall(HttpUtils.SEARCH_ALL_LABLE, null, 0);
            if (call != null) {
                call.map(new Func1<CategoryBean, CategoryBean>() {
                    @Override
                    public CategoryBean call(CategoryBean bean) {
                        int retCode = bean.getRetCode();
                        if (retCode != 200) {
                            throw new RuntimeException();
                        }
                        return bean;
                    }
                })
                        .flatMap(new Func1<CategoryBean, Observable<CategoryBean.ResultBean.ChildsBeanX>>() {
                            @Override
                            public Observable<CategoryBean.ResultBean.ChildsBeanX> call(CategoryBean bean) {

                                return Observable.from(bean.getResult().getChilds());
                            }
                        }).map(new Func1<CategoryBean.ResultBean.ChildsBeanX, List<String>>() {
                    @Override
                    public List<String> call(CategoryBean.ResultBean.ChildsBeanX childsBeanX) {
                        List<CategoryBean.ResultBean.ChildsBeanX.ChildsBean> oldList = childsBeanX.getChilds();
                        List<String> list = new ArrayList<>();
                        for (CategoryBean.ResultBean.ChildsBeanX.ChildsBean child : oldList) {
                            list.add(child.getCategoryInfo().getName() + "," + child.getCategoryInfo().getCtgId());
                        }
                        return list;
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<String>>() {
                            @Override
                            public void onCompleted() {
                                mProgressbar.setVisibility(View.GONE);
                                mViewPager.setAdapter(new MainPagePagerAdapter(getSupportFragmentManager(), mContext, fragmentList));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + "未能获取数据");
                            }

                            @Override
                            public void onNext(List<String> strings) {
                                MainListFragment fragment = new MainListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("data", (ArrayList<String>) strings);
                                fragment.setArguments(bundle);
                                fragmentList.add(fragment);
                            }
                        });
            }
        }


    }


}
