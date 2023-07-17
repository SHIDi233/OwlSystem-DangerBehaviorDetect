package com.example.dangerbehiviordetect.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.dangerbehiviordetect.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Bundle bundle = getIntent().getExtras();
        data = new Bundle();
        //必须携带cID跳转到这个界面
        if(bundle!=null){
            int cID = bundle.getInt("cID");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://116.204.11.171:8080/record"+"?"+"cID="+String.valueOf(cID))
                    .header("token", getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token","1"))
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
                                String url = element.getAsJsonObject().get("url").toString();
                                data.putString("msg",element.getAsJsonObject().get("msg").toString());
                                data.putString("url",url);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }

                        }
                    }
            );
        }
    }
}