package com.ysh.demo.demo1.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ysh.demo.demo1.R;
import com.ysh.demo.demo1.fragments.MainListFragment;

import java.util.List;

/**
 * Created by hasee on 2016/11/14.
 */
public class MainPagePagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "MainPagePagerAdapter";
    private String[] tabs;
    private List<MainListFragment> mFragmnetList;

    public MainPagePagerAdapter(FragmentManager fm, Context mContext, List<MainListFragment> fragmentList) {
        super(fm);
        mFragmnetList= fragmentList;
        tabs = new String[]{mContext.getString(R.string.byVariety),
                mContext.getString(R.string.byMethod),
                mContext.getString(R.string.byStyle),
                mContext.getString(R.string.byPeopleGroup),
                mContext.getString(R.string.byFunction)};
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmnetList.get(position);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tabs[position];
    }
}
