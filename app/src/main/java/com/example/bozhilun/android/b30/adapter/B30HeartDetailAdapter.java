package com.example.bozhilun.android.b30.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */

public class B30HeartDetailAdapter extends RecyclerView.Adapter<B30HeartDetailAdapter.B30ViewHolder>{

    private List<HalfHourRateData> list;
    private Context mContext;

    public B30HeartDetailAdapter(List<HalfHourRateData> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public B30ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_b30_heart_detail_layout,parent,false);
        B30ViewHolder b30ViewHolder = new B30ViewHolder(view);
        return b30ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull B30ViewHolder holder, int position) {
        holder.dateTv.setText(list.get(position).getTime().getColck()+"");
        int rateValue = list.get(position).getRateValue();
        if(rateValue == 0){
            holder.valueTv.setText("--");
        }else{
            holder.valueTv.setText(list.get(position).getRateValue()+"");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class B30ViewHolder extends RecyclerView.ViewHolder{

        TextView dateTv,valueTv;
        ImageView img;

        public B30ViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.itemHeartDetailDateTv);
            valueTv = itemView.findViewById(R.id.itemHeartDetailValueTv);
            img = itemView.findViewById(R.id.itemHeartDetailImg);
        }
    }
}
