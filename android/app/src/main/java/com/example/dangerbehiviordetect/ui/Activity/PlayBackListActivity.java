package com.example.dangerbehiviordetect.ui.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dangerbehiviordetect.Adapter.RecordRecycleAdapter;
import com.example.dangerbehiviordetect.Entity.CameraInfo;
import com.example.dangerbehiviordetect.Entity.RecordInfo;
import com.example.dangerbehiviordetect.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayBackListActivity extends AppCompatActivity {

    private ArrayList<RecordInfo> recordInfoArrayList = new ArrayList<>();
    private RecordRecycleAdapter recordRecycleAdapter;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private ImageView calendar_button;
    private Button search_button;

    private ImageButton back_button;

    private EditText et_cID;
    private int year;
    private int month;
    private int dayOfMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back_list);
        initView();
        calendar_button = findViewById(R.id.calendar_img);
        search_button = findViewById(R.id.pblsearch_button);
        et_cID = findViewById(R.id.username_edit_text);
        back_button = findViewById(R.id.register_back);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Bundle bundle = getIntent().getExtras();
        int cID = Integer.parseInt(bundle.getString("cID"));
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/playbacks?cID="+bundle.getString("cID")+
                        "&year="+year+"&month="+month+"&day="+dayOfMonth)
                .addHeader("token",getSharedPreferences("Myprefs", Context.MODE_PRIVATE)
                .getString("token","1"))
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
                int code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                if(code !=0){
                    String msg = element.getAsJsonObject().get("msg").toString();
                    String aList = element.getAsJsonObject().get("data").toString();
                    Gson gson = new Gson();
                    recordInfoArrayList = gson.fromJson(aList,new TypeToken<List<RecordInfo>>(){}.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });
                }
            }
        });
        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建日期选择器对话框
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PlayBackListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth1) {
                                // 这里处理用户选择的日期
                                // year, month, dayOfMonth分别是用户选择的年、月、日
                                Toast.makeText(PlayBackListActivity.this, year+"年"+month+"月"+dayOfMonth+"日", Toast.LENGTH_SHORT).show();
                                year = year1;
                                month = month1;
                                dayOfMonth = dayOfMonth1;
                                recordInfoArrayList.clear();
                                initView();
                            }
                        },
                        year, month, dayOfMonth);

                // 显示日期选择器对话框
                datePickerDialog.show();
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp_month = month + 1;
                String input = et_cID.getText().toString();
                Log.d(TAG, year+"年"+temp_month+"月"+dayOfMonth+"日");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://116.204.11.171:8080/playbacks?cID="+bundle.getString("cID")+
                                "&year="+year+"&month="+temp_month+"&day="+dayOfMonth)
                        .addHeader("token",getSharedPreferences("Myprefs", Context.MODE_PRIVATE)
                                .getString("token","1"))
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
                        int code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                        if(code !=0){
                            String msg = element.getAsJsonObject().get("msg").toString();
                            String aList = element.getAsJsonObject().get("data").toString();
                            Gson gson = new Gson();
                            recordInfoArrayList.clear();
                            recordInfoArrayList = gson.fromJson(aList,new TypeToken<List<RecordInfo>>(){}.getType());
                            for (int i = 0; i < recordInfoArrayList.size() - 1; i++){
                                if (!recordInfoArrayList.get(i).pid.equals(input) && !input.equals("")){
                                    recordInfoArrayList.remove(i);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initView();
                                }
                            });
                        }
                    }
                });
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData(){
        for (int i = 0; i < 5; i++){
            RecordInfo recordInfo = new RecordInfo(""+i,""+i,""+i);
            recordInfoArrayList.add(recordInfo);
        }
    }
    private void initView(){
//        recordInfoArrayList.clear();
        recyclerView = findViewById(R.id.playback_list_recyclerView);
        recordRecycleAdapter = new RecordRecycleAdapter(this, recordInfoArrayList);
        recordRecycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recordRecycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new space_item(10));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recordRecycleAdapter.setOnItemClickListener(new RecordRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, RecordInfo recordInfo) {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://116.204.11.171:8080/getPlayback?pID="+recordInfo.pid)
                        .get()
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        // 处理响应成功的情况，解析 responseBody 等
                        JsonElement element = JsonParser.parseString(responseBody);
                        int code;
                        try {
                            code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            code = 0;
                        }
                        if(code!=0){
//                            Gson gson = new Gson();
//                            PlaybackInfo playbackInfo =
//                                    gson.fromJson(element.getAsJsonObject().get("data").toString(),PlaybackInfo.class);
                            PlaybackInfo playbackInfo = new PlaybackInfo();
                            String url = element.getAsJsonObject().get("data").toString();
                            url = url.substring(1,url.length()-1);
                            playbackInfo.url = url;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(PlayBackListActivity.this, PlaybackActivity2.class);
                                    intent.putExtra("url", playbackInfo.url);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });

            }
        });
    }
    class PlaybackInfo{
//        int fps;
//        String videoUrl;
//        String infoUrl;
        String url;
    }
    class space_item extends RecyclerView.ItemDecoration{


        //设置item的间距
        private int space=20;
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
    @Override
    public void onDestroy() {
        super.onDestroy();
//        binding = null;
    }

}