package com.ysh.demo.demo1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ysh.demo.demo1.constant.Contant;
import com.ysh.demo.demo1.litepalbean.RecipeItemBean;
import com.ysh.demo.demo1.utils.NetworkJugde;
import com.ysh.demo.demo1.utils.Toastutil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ActivityCollections extends AppCompatActivity {

    private static final String TAG = "ActivityCollections";
    private List<RecipeItemBean> mData;
    private boolean isEdit;
    private boolean isResultTrue;
    private int positionResult;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private ImageButton mImageButton;
    private TextView mTextView;
    private RelativeLayout rlNoCollection;
    private TextView mTextViewTitle;
    private ActivityCollections myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        myContext = this;
        getDataBeans();//获得数据
        Log.d(TAG, "onCreate: dataSize:" + mData.size());
        mAdapter = getAdapter();//根据数据建立Adapter
        initView();//初始化所有view以及给RecyclerView设置Adapter

    }

    @Override
    protected void onStart() {
        super.onStart();
        isEdit = false;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        rlNoCollection.setVisibility(View.GONE);
        setEmptyShow();
    }

    private void setEmptyShow() {
        if (mData.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            rlNoCollection.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_collection);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mEditText = (EditText) findViewById(R.id.editText);
        mEditText.setVisibility(View.GONE);
        mTextViewTitle = (TextView) findViewById(R.id.titleText);
        mTextViewTitle.setVisibility(View.VISIBLE);
        mImageButton = (ImageButton) findViewById(R.id.btnBack);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rlNoCollection = (RelativeLayout) findViewById(R.id.rl_no_collection);
        mTextView = (TextView) findViewById(R.id.btnSearch);
        mTextView.setText("编辑");
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    isEdit = true;
                    mTextView.setText("完成");
                    mAdapter.notifyDataSetChanged();
                } else {
                    isEdit = false;
                    mTextView.setText("编辑");
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @NonNull
    private CommonAdapter<RecipeItemBean> getAdapter() {
        return new CommonAdapter<RecipeItemBean>(myContext, R.layout.search_result_item, mData) {

            @Override
            protected void convert(ViewHolder holder, RecipeItemBean recipeItemBean, final int position) {
                ImageView iv = holder.getView(R.id.itemIcon);
                Glide.with(myContext).load(recipeItemBean.getImageUri()).error(R.mipmap.ic_menu_defaut).into(iv);
                holder.setText(R.id.itemTitle, recipeItemBean.getTitle());
                holder.setText(R.id.itemDesc, recipeItemBean.getDescribe());
                Button btnDelete = holder.getView(R.id.btn_delete);
                final String id = recipeItemBean.getMenuId();
                if (!isEdit) {
                    btnDelete.setVisibility(View.GONE);
                } else {
                    btnDelete.setVisibility(View.VISIBLE);
                }
                //设置点击监听器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkJugde.getInstance().isNetworkConnected(myContext)) {
                            positionResult = position;
                            ActivityMenuDetail.actionStartActivityForResult(myContext, id, Contant.REQUEST_CODE_ACTIVITY_MENU_DETAIL);
                        } else {
                            Toastutil.showToast(myContext, "请打开网络后在试");
                        }
                    }
                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataSupport.deleteAll(RecipeItemBean.class, "menuId = ?", id);
                        mData.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemChanged(position);
                        setEmptyShow();
                    }
                });
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Contant.REQUEST_CODE_ACTIVITY_MENU_DETAIL:
                if (resultCode == RESULT_OK) {
                    isResultTrue = data.getBooleanExtra(Contant.KEY_FOR_RESULT, true);
                    if (!isResultTrue) {
                        mData.remove(positionResult);
                        Log.d(TAG, "onActivityResult: " + mData.size());
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                }

        }
    }

    private void getDataBeans() {
        mData = DataSupport.findAll(RecipeItemBean.class);
    }

    public static void actionStartActivity(Context activity) {
        Intent intent = new Intent(activity, ActivityCollections.class);
        activity.startActivity(intent);
    }

}
