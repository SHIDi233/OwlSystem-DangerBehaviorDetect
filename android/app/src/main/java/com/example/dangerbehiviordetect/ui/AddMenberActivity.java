package com.example.dangerbehiviordetect.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
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

public class AddMenberActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menber);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        data = new Bundle();
        Bundle bundle = getIntent().getExtras();
        String cID = bundle.getString("cID");
        String mail = bundle.getString("mail");
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("cID",cID)
                .add("mail",mail)
                .build();
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/addMember")
                .header("token", sharedPreferences.getString("token","1"))
                .post(body)
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
                String temp = element.getAsJsonObject().get("code").toString();
                data.putString("code",temp);
                if(code!=0) {
                    data.putString("msg", element.getAsJsonObject().get("msg").toString());
                }
            }
        });

    }
}