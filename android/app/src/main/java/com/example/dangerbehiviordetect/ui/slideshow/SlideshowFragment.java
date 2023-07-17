package com.example.dangerbehiviordetect.ui.slideshow;

import android.content.Context;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dangerbehiviordetect.Adapter.ChartRecycleAdapter;
import com.example.dangerbehiviordetect.Entity.CameraInfo;
import com.example.dangerbehiviordetect.Entity.ChartInfo;
import com.example.dangerbehiviordetect.R;
import com.example.dangerbehiviordetect.ui.slideshow.SlideshowViewModel;
import com.example.dangerbehiviordetect.databinding.FragmentSlideshowBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;
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

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private Boolean flag = false;

    private  View view;
    private ArrayList<ChartInfo> chartInfoArrayList = new ArrayList<>();
    private List<CameraInfo> cameraInfos = new ArrayList<>();
    private ChartRecycleAdapter chartRecycleAdapter;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private BarChart barChart;
    private TabLayout tabLayout;
    private String cID = "0";
    private String type = "hour";
//    private String type = "day";
    private List<Danger> dangers = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

//        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
        view = inflater.inflate(R.layout.fragment_slideshow, container, false);
        barChart = view.findViewById(R.id.barChart);
        tabLayout = view.findViewById(R.id.tab_layout);
//        initData();
//        initView();
//        initBarChartUI();
//        initBarChartData();
        initTabLayout();
        //获取所有摄像头编号 的请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/cameras")
                .header("token",getActivity().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE).getString("token","1"))
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                JsonElement element = JsonParser.parseString(responseBody);
                int code;
                try {
                    code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                } catch (Exception e) {
                    code = 0;
                }
                if(code!=0){

                    Gson gson = new Gson();
                    cameraInfos = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<CameraInfo>>(){}.getType());
                    for(CameraInfo each:cameraInfos){
                        ChartInfo chartInfo = new ChartInfo(each);
                        chartInfoArrayList.add(chartInfo);
                    }
                    if (cameraInfos.size()!=0){
                        cID = String.valueOf(cameraInfos.get(0).cid);
                    }else {
                    }
                    flag = true;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
        });
        while (!flag){

        }
        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder()
                .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+String.format("&type=%s", type))
                .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                .get()
                .build();
        client1.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                JsonElement element = JsonParser.parseString(responseBody);
                int code;
                try {
                    code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                } catch (Exception e) {
                    code = 0;
                }
                if(code!=0){

                    Gson gson = new Gson();
                    dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initBarChartUI();
                        initBarChartData();
                    }
                });
            }
        });
//        final TextView textView = binding.textSlideshow;
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return view;
    }

    private void initView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_slideshow);
        chartRecycleAdapter = new ChartRecycleAdapter(getActivity(), chartInfoArrayList);
        recyclerView.setAdapter(chartRecycleAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new space_item(150));

        chartRecycleAdapter.setOnItemClickListener(new ChartRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, ChartInfo chartInfo) {
                //赋值当前摄像头编号
                cID = chartInfo.cid;
                updateBarChartData();
            }
        });
    }

    private void initBarChartData(){
        barChart.setNoDataText("不存在数据");
        if (dangers.size() == 0){
            List<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(0,0));
            barEntries.add(new BarEntry(1,0));
            barEntries.add(new BarEntry(2,0));
//        barEntries.add(new BarEntry(3,dangers.get(3).cnt));

            BarDataSet barDataSet = new BarDataSet(barEntries,"危险行为次数");
            BarData ba = new BarData(barDataSet);
            barDataSet.setValueTextSize(12f);
            barChart.setData(ba);
            barChart.invalidate();
            barChart.clearValues();
            barChart.setNoDataText("不存在数据");
            return;
        }
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0,dangers.get(0).cnt));
        barEntries.add(new BarEntry(1,dangers.get(1).cnt));
        barEntries.add(new BarEntry(2,dangers.get(2).cnt));
//        barEntries.add(new BarEntry(3,dangers.get(3).cnt));

        BarDataSet barDataSet = new BarDataSet(barEntries,"危险行为次数");
        BarData ba = new BarData(barDataSet);
        barDataSet.setValueTextSize(12f);
        barChart.setData(ba);
        barChart.invalidate();
    }

    private void updateBarChartData(){
        List<BarEntry> barEntries = new ArrayList<>();

        if (dangers.size() == 0){
            barChart.clearValues();
            barChart.setNoDataText("不存在数据");
            return;
        }
        barEntries.add(new BarEntry(0,dangers.get(0).cnt));
        barEntries.add(new BarEntry(1,dangers.get(1).cnt));
        barEntries.add(new BarEntry(2,dangers.get(2).cnt));
//        barEntries.add(new BarEntry(3,dangers.get(3).cnt));
        barChart.clearValues();
        BarDataSet barDataSet = new BarDataSet(barEntries, "new");
        barDataSet.setValueTextSize(12f);
        barChart.getBarData().addDataSet(barDataSet);
        barChart.getBarData().notifyDataChanged();
        barChart.invalidate();
        barChart.animateY(1000);
    }

    private void initBarChartUI(){

        // 不显示图例
        barChart.getLegend().setEnabled(false);
        // 不显示描述
        barChart.getDescription().setEnabled(false);
        // 左右空出barWidth/2，更美观
        barChart.setFitBars(true);
        // 不绘制网格
        barChart.setDrawGridBackground(false);


        XAxis xAxis = barChart.getXAxis();
        // 设置x轴显示在下方
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置x轴不画线
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        // 设置自定义的ValueFormatter
        String[] labels = {"calling","smoking","down"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return labels[index];
            }
        });

        // 设置左y轴
        YAxis yAxis = barChart.getAxisLeft();
        // 设置y-label显示在图表外
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // Y轴从0开始，不然会上移一点
        yAxis.setAxisMinimum(0f);
        // 设置y轴不画线
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMaximum(500f);
        // 不显示右y轴
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
//        barChart.setMaxVisibleValueCount(3);
    }
//
//    private void initData(){
//        for (int i = 0; i < 3; i++){
//            ChartInfo chartInfo = new ChartInfo(""+i,""+i,""+i,""+i);
//            chartInfoArrayList.add(chartInfo);
//        }
//    }

    private void initTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    type = "hour";
                    //发送小时的请求
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+String.format("&type=%s", type))
                            .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (Exception e) {
                                code = 0;
                            }
                            if(code!=0){
                                Gson gson = new Gson();
                                dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateBarChartData();
                                }
                            });
                        }
                    });

                    Toast.makeText(getActivity(), "点击 hour", Toast.LENGTH_SHORT).show();
                }else if (tab.getPosition() == 1){
                    //发送天的请求
                    type = "day";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+String.format("&type=%s", type))
                            .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (Exception e) {
                                code = 0;
                            }
                            if(code!=0){
                                Gson gson = new Gson();
                                dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateBarChartData();
                                }
                            });
                        }
                    });


                    Toast.makeText(getActivity(), "点击 day", Toast.LENGTH_SHORT).show();
                } else if (tab.getPosition() == 2) {
                    type = "week";
                    //发送周的请求
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+ String.format("&type=%s", type))
                            .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (Exception e) {
                                code = 0;
                            }
                            if(code!=0){
                                Gson gson = new Gson();
                                dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateBarChartData();
                                }
                            });
                        }
                    });


                    Toast.makeText(getActivity(), "点击 week", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    type = "hour";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+String.format("&type=%s", type))
                            .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (Exception e) {
                                code = 0;
                            }
                            if(code!=0){
                                Gson gson = new Gson();
                                dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateBarChartData();
                                }
                            });
                        }
                    });
                    Toast.makeText(getActivity(), "再次点击 按小时计", Toast.LENGTH_SHORT).show();
                }else if (tab.getPosition() == 1){
                    type = "day";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+String.format("&type=%s", type))
                            .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (Exception e) {
                                code = 0;
                            }
                            if(code!=0){
                                Gson gson = new Gson();
                                dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateBarChartData();
                                }
                            });
                        }
                    });

                    Toast.makeText(getActivity(), "再次点击 按天计", Toast.LENGTH_SHORT).show();
                } else if (tab.getPosition() == 2) {
                    type = "week";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.204.11.171:8080/count_android?cID="+(String.valueOf((int)Double.parseDouble(cID)))+String.format("&type=%s", type))
                            .header("token",getActivity().getSharedPreferences("Myprefs",Context.MODE_PRIVATE).getString("token","1"))
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (Exception e) {
                                code = 0;
                            }
                            if(code!=0){
                                Gson gson = new Gson();
                                dangers = gson.fromJson(element.getAsJsonObject().get("data").toString(),new TypeToken<List<Danger>>(){}.getType());
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateBarChartData();
                                }
                            });
                        }
                    });

                    Toast.makeText(getActivity(), "再次点击 按周计", Toast.LENGTH_SHORT).show();
                }
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
        private int space=15;
        public space_item(int space){
            this.space=space;
        }
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
            outRect.left = space;
            outRect.right = space;
//            outRect.bottom=space;
//            outRect.top=space;
        }
    }
    class Danger{
        String type;
        int cnt;
    }
}