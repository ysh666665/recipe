package com.ysh.demo.demo1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ysh.demo.demo1.R;

import java.util.List;

/**
 * Created by hasee on 2016/11/21.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private OnItemClickLitener mOnItemClickLitener;
    private Context mContext;
    private List<String> mList;

    public HomeAdapter(Context mContext, List<String> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
    }

    public interface OnItemClickLitener {
        void onItemClick(TextView view, int position);

    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String s = mList.get(position);
        String[] strings=s.split(",");
        holder.tv.setText(strings[0]);
        holder.tv.setTag(strings[1]);
//        if (mOnItemClickLitener != null)
//        {
//            holder.tv.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v) {
//                    int pos = holder.getLayoutPosition();
//                    mOnItemClickLitener.onItemClick((TextView) v,pos);
//                }
//            });
//
//        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }
}


