package com.example.dangerbehiviordetect.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dangerbehiviordetect.Entity.ChartInfo;
import com.example.dangerbehiviordetect.R;

import java.util.ArrayList;

public class ChartRecycleAdapter extends RecyclerView.Adapter<ChartRecycleAdapter.myViewHolder> {

    Context context;
    ArrayList<ChartInfo> chartInfoArrayList = new ArrayList<>();

    public ChartRecycleAdapter(Context context, ArrayList<ChartInfo> chartInfoArrayList){
        this.context = context;
        this.chartInfoArrayList = chartInfoArrayList;
    }

    @NonNull
    @Override
    public ChartRecycleAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.chart_item, null);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        ChartInfo chartInfo = chartInfoArrayList.get(position);
        holder.tv_chart_cid.setText("cID:" + chartInfo.cid);
        holder.tv_chart_autority.setText("权限:" + chartInfo.owner);
        holder.tv_chart_desc.setText("描述:" + chartInfo.content);
//        holder.tv_chart_num.setText("危险行为总次数:" + chartInfo.sum_num);
    }

    @Override
    public int getItemCount() {
        return chartInfoArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_chart_cid;
        private TextView tv_chart_autority;
        private TextView tv_chart_desc;
        private TextView tv_chart_num;

        private CardView cardView;

        public myViewHolder(View itemView) {
            super(itemView);
            tv_chart_cid = itemView.findViewById(R.id.tv_chart_cid);
            tv_chart_autority = itemView.findViewById(R.id.tv_chart_authority);
            tv_chart_desc = itemView.findViewById(R.id.tv_chart_desc);
//            tv_chart_num = itemView.findViewById(R.id.tv_chart_num);
            cardView = itemView.findViewById(R.id.camera_item);
            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, chartInfoArrayList.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param chartInfo 点击的item的数据
         */
        public void OnItemClick(View view, ChartInfo chartInfo);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private ChartRecycleAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(ChartRecycleAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
