package com.ysh.demo.demo1.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ysh.demo.demo1.ActivitySearchMenu;
import com.ysh.demo.demo1.R;
import com.ysh.demo.demo1.adapter.HomeAdapter;

import java.util.List;

/**
 * Created by hasee on 2016/11/14.
 */
public class MainListFragment extends Fragment {

    private static final String TAG = "MainListFragment";
    private Activity mActivity;
    private List<String> mList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = getActivity();
        }
        mList = getArguments().getStringArrayList("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragmet_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mainRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        HomeAdapter adapter = new HomeAdapter(mActivity, mList);
//        adapter.setOnItemClickLitener(new HomeAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(TextView view, int position) {
//               String s = (String) view.getText();
//                ActivitySearchMenu.actionStartActivity(mActivity,s);
//            }
//        });
        recyclerView.setAdapter(adapter);

        return view;
    }

}
