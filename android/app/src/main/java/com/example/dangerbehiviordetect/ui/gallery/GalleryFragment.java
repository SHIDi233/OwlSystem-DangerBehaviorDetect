package com.example.dangerbehiviordetect.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dangerbehiviordetect.Adapter.CameraRecycleAdapter;
import com.example.dangerbehiviordetect.Adapter.RecordRecycleAdapter;
import com.example.dangerbehiviordetect.Entity.CameraInfo;
import com.example.dangerbehiviordetect.Entity.RecordInfo;
import com.example.dangerbehiviordetect.R;
import com.example.dangerbehiviordetect.databinding.FragmentGalleryBinding;
import com.example.dangerbehiviordetect.ui.Activity.MonitorActivity;
import com.example.dangerbehiviordetect.ui.Activity.PlayBackListActivity;
import com.example.dangerbehiviordetect.ui.Activity.PlaybackActivity;
import com.example.dangerbehiviordetect.ui.Activity.PlaybackActivity2;
import com.example.dangerbehiviordetect.ui.home.HomeFragment;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

//    private FragmentGalleryBinding binding;
    private View view;
    private ArrayList<CameraInfo> cameraInfoArrayList;
    private CameraRecycleAdapter cameraRecycleAdapter;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    boolean flag = false;
    Bundle data;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

//        binding = FragmentGalleryBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
        view = inflater.inflate(R.layout.fragment_gallery, container, false);


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
                        int code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
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
//        String type[] = {"玩手机", "打电话", "入侵", "抽烟"};
//        for (int i = 0; i < 2; i++){
//            for (int j = 0; j < 4; j++){
//                RecordInfo recordInfo = new RecordInfo();
//                recordInfo.type = type[j];
//                recordInfo.start_time = "2023-07-06T09:23:33";
//                recordInfo.probability = "0.97";
//                recordInfo.number = "" + i*4 + j;
//                recordInfo.url = "https://dbd-video-0.oss-cn-beijing.aliyuncs.com/%E3%80%90%E6%B3%A0%E9%B8%A2%E7%9B%B4%E6%92%AD%E6%AD%8C%E7%B2%BE%E4%BF%AE%E3%80%91%E6%9C%88%E7%90%83%EF%BC%88%E6%98%AF%E9%93%B6%E6%B2%B3%E4%B8%87%E9%A1%B7%E4%B9%8B%E4%B8%AD%E6%9C%80%E6%B8%A9%E6%9F%94%E7%9A%84%E8%B0%9C%E5%BA%95%E5%91%80%EF%BC%89_%E5%93%94%E5%93%A9%E5%93%94%E5%93%A9_bilibili.flv";
//                recordInfoArrayList.add(recordInfo);
//            }
//
//        }
//    }

    private void initView(){
        recyclerView =(RecyclerView) view.findViewById(R.id.gallery_recyclerView);
        cameraRecycleAdapter = new CameraRecycleAdapter(getActivity(),cameraInfoArrayList);
//        cameraRecycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cameraRecycleAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new space_item(10));//给recycleView添加item的间距

        cameraRecycleAdapter.setOnItemClickListener(new CameraRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, CameraInfo cameraInfo) {
                Intent intent = new Intent(getActivity(), PlayBackListActivity.class);
                Bundle data = new Bundle();
                int a = (int)Math.round(cameraInfo.cid);
                data.putString("cID",String.valueOf(a));
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
        view = null;
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