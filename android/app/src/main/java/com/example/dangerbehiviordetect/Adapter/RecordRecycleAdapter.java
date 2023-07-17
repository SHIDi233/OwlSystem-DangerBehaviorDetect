package com.example.dangerbehiviordetect.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dangerbehiviordetect.Entity.CameraInfo;
import com.example.dangerbehiviordetect.Entity.RecordInfo;
import com.example.dangerbehiviordetect.R;

import java.util.ArrayList;

public class RecordRecycleAdapter extends RecyclerView.Adapter<RecordRecycleAdapter.myViewHodler>{

    Context context;
    ArrayList<RecordInfo> recordInfoArrayList =  new ArrayList<>();

    public RecordRecycleAdapter(Context context, ArrayList<RecordInfo> recordInfoArrayList){
        this.context = context;
        this.recordInfoArrayList = recordInfoArrayList;
    }

    @NonNull
    @Override
    public RecordRecycleAdapter.myViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.record_item, null);
        return new myViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordRecycleAdapter.myViewHodler holder, int position) {
        RecordInfo recordInfo = recordInfoArrayList.get(position);
        holder.tv_number.setText("回放编号："+recordInfo.pid);
        holder.tv_stime.setText("开始:"+recordInfo.stime);
        holder.tv_etime.setText("结束:"+recordInfo.etime);
    }

    @Override
    public int getItemCount() {
        return recordInfoArrayList.size();
    }



    class myViewHodler extends RecyclerView.ViewHolder {
        private TextView tv_stime;
        private TextView tv_etime;
        private TextView tv_number;

        private CardView cardView;

        public myViewHodler(View itemView) {
            super(itemView);
            tv_stime = itemView.findViewById(R.id.stime_text);
            tv_etime = itemView.findViewById(R.id.etime_text);
            tv_number = itemView.findViewById(R.id.number_text);
            cardView = itemView.findViewById(R.id.record_item);
            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, recordInfoArrayList.get(getLayoutPosition()));
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
         * @param recordInfo 点击的item的数据
         */
        public void OnItemClick(View view, RecordInfo recordInfo);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private RecordRecycleAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(RecordRecycleAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
