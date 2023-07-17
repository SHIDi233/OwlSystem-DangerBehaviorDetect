package com.example.dangerbehiviordetect.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.dangerbehiviordetect.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckMenberActivity extends AppCompatActivity {
    Bundle data;
    List<Member> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_menber);
        list = new ArrayList<>();
        data = new Bundle();
        Bundle bundle = getIntent().getExtras();
        String cID = bundle.getString("cID");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/menbers?cID="+cID)
                .header("token",getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token","1"))
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
                            list = gson.fromJson(objects, list.getClass());
                            //加载到界面中

                        }else {

                        }
                    }
                }
        );
    }
}

class Member{
    public int uID;
    public String mail;
    public String uName;
    public boolean isOwner;
}