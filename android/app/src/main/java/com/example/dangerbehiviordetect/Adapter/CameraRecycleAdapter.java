package com.example.dangerbehiviordetect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContentInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dangerbehiviordetect.Entity.CameraInfo;
import com.example.dangerbehiviordetect.R;
import com.example.dangerbehiviordetect.ui.Activity.MonitorActivity;

import java.util.ArrayList;

public class CameraRecycleAdapter extends RecyclerView.Adapter<CameraRecycleAdapter.myViewHodler>{

    Context context;
    ArrayList<CameraInfo> cameraInfoArrayList;
    public CameraRecycleAdapter(Context context, ArrayList<CameraInfo> cameraInfoArrayList){
        this.context = context;
        this.cameraInfoArrayList = cameraInfoArrayList;
    }



    @NonNull
    @Override
    public CameraRecycleAdapter.myViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.camera_item, null);
        return new myViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CameraRecycleAdapter.myViewHodler holder, int position) {
        CameraInfo cameraInfo = cameraInfoArrayList.get(position);
        holder.nameText.setText("cID" + cameraInfo.cid.toString());
        if(cameraInfo.owner){
            holder.addrText.setText("权限：所有权");
        }else {
            holder.addrText.setText("权限：查看权");
        }

        holder.msgText.setText("描述信息：" + cameraInfo.content);
    }

    @Override
    public int getItemCount() {
        return cameraInfoArrayList.size();
    }




    //自定义viewhodler
    class myViewHodler extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView addrText;
        private TextView msgText;

        private CardView cardView;

        public myViewHodler(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.camera_name);
            addrText = itemView.findViewById(R.id.camera_addr);
            msgText = itemView.findViewById(R.id.camera_msg);
            cardView = itemView.findViewById(R.id.camera_item);
            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
//                    Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show();

                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, cameraInfoArrayList.get(getLayoutPosition()));
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
         * @param cameraInfo 点击的item的数据
         */
        public void OnItemClick(View view, CameraInfo cameraInfo);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
