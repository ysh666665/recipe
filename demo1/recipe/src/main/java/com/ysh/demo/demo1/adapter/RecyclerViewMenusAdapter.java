package com.ysh.demo.demo1.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ysh.demo.demo1.R;
import com.ysh.demo.demo1.gsonbean.SearchResultBean;

import java.util.List;

/**
 * Created by hasee on 2016/11/21.
 */

public class RecyclerViewMenusAdapter extends RecyclerView.Adapter<RecyclerViewMenusAdapter.MyViewHolder> {

    private Activity mActivity;
    private List<SearchResultBean.ResultBean.ListBean> mList;

    public RecyclerViewMenusAdapter(Activity mActivity, List<SearchResultBean.ResultBean.ListBean> mList) {
        super();
        this.mActivity = mActivity;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.search_result_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.title.setText(mList.get(position).getName());
        holder.itemView.setTag(mList.get(position).getMenuId());
        SearchResultBean.ResultBean.ListBean.RecipeBean recipe = mList.get(position).getRecipe();
        if (recipe != null) {
            String desc = recipe.getSumary();
            if (desc == null) {
                desc = recipe.getTitle();
            }
            holder.description.setText(desc);
        }


        Glide.with(mActivity).load(mList.get(position).getThumbnail()).error(R.mipmap.ic_menu_defaut).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.itemTitle);
            description = (TextView) itemView.findViewById(R.id.itemDesc);
            icon = (ImageView) itemView.findViewById(R.id.itemIcon);
        }
    }
}


