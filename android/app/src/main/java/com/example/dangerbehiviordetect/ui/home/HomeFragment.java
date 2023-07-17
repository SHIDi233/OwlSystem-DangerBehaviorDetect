package com.example.dangerbehiviordetect.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dangerbehiviordetect.Adapter.CameraRecycleAdapter;
import com.example.dangerbehiviordetect.Entity.CameraInfo;
import com.example.dangerbehiviordetect.R;
import com.example.dangerbehiviordetect.ui.Activity.GmapActivity;
import com.example.dangerbehiviordetect.ui.Activity.MonitorActivity;
import com.example.dangerbehiviordetect.ui.Activity.PlaybackActivity2;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
//import com.example.dangerbehiviordetect.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

//    private FragmentHomeBinding binding;
    private View view;
    private ArrayList<CameraInfo> cameraInfoArrayList;
    private CameraRecycleAdapter cameraRecycleAdapter;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    boolean flag = false;

    private ImageView iv_graph;
    Bundle data;
//    private Fragment_Activity_interface fragment_activity_interface;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
        view = inflater.inflate(R.layout.fragment_home, container, false);

        iv_graph = view.findViewById(R.id.iv_graph);
        iv_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GmapActivity.class);
                startActivity(intent);
            }
        });










        Bundle  bundle = getActivity().getIntent().getExtras();
        OkHttpClient client = new OkHttpClient();
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        data = new Bundle();
        cameraInfoArrayList = new ArrayList<>();
        String token = sharedPreferences.getString("token","1");
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/cameras")
                .header("token",sharedPreferences.getString("token","1"))
                .get()
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        // 处理响应成功的情况，解析 responseBody 等
                        JsonElement element = JsonParser.parseString(responseBody);
                        int code = 0;
                        try {
                            code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                        } catch (Exception e) {
                            code=0;
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"获取摄像头失败！",Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        String temp = element.getAsJsonObject().get("code").toString();
                        data.putString("code",temp);
                        if(code !=0){
                            String msg = element.getAsJsonObject().get("msg").toString();
                            data.putString("msg",msg);
                            String objects = element.getAsJsonObject().get("data").toString();
                            Gson gson = new Gson();
                            cameraInfoArrayList = gson.fromJson(objects, new TypeToken<List<CameraInfo>>(){}.getType());
//                            cameraRecycleAdapter.notifyDataSetChanged();
//                            initData();
                            //加载到界面中
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initView();
                                    Toast.makeText(getActivity(),"成功获取摄像头！",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"获取摄像头失败！",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }
        );

        initView();
        return view;
    }

//    public void initData(){
//        for (int i = 0; i < cameraInfoArrayList.size(); i++){
//            //初始化CamerInfo 这里可以获取请求的信息
//            CameraInfo cameraInfo = new CameraInfo("name:"+i, "addr:"+i,
//                    "msg:" + i);
//            cameraInfoArrayList.add(cameraInfo);
//        }
//    }

    private void initView(){
        recyclerView =(RecyclerView) view.findViewById(R.id.home_recyclerView);
        cameraRecycleAdapter = new CameraRecycleAdapter(getActivity(),cameraInfoArrayList);
//        cameraRecycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cameraRecycleAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new space_item(10));//给recycleView添加item的间距

        cameraRecycleAdapter.setOnItemClickListener(new CameraRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, CameraInfo cameraInfo) {
                double cid = cameraInfo.cid;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://116.204.11.171:8080/record"+"?"+"cID="+String.valueOf((int)cid))
                        .header("token", getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token","1"))
                        .get()
                        .build();
                client.newCall(request).enqueue(
                        new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String responseBody = response.body().string();
                                JsonElement element = JsonParser.parseString(responseBody);
                                int code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                                String temp = element.getAsJsonObject().get("code").toString();
                                data.putString("code",temp);
                                if(code!=0){
                                    String url = element.getAsJsonObject().get("data").toString();
                                    data.putString("msg",element.getAsJsonObject().get("msg").toString());
                                    data.putString("url","rtmp://116.204.11.171/"+String.valueOf((int)cid)+"/test");
                                    flag = true;
                                }else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });
                                }

                            }
                        }
                );
                while (!flag){

                }
                Intent intent = new Intent(getActivity(), PlaybackActivity2.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }

    class space_item extends RecyclerView.ItemDecoration{


        //设置item的间距
        private int space=10;
        public space_item(int space){


            this.space=space;
        }
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
            outRect.left = space;
            outRect.right = space;
            outRect.bottom=space;
            outRect.top=space;
        }
    }
}