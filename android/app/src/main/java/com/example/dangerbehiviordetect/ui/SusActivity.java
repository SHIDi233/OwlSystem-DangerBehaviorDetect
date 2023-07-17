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

public class SusActivity extends AppCompatActivity {
    Bundle data;
    List<Suspect> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sus);
        data = new Bundle();
        list = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        String cID = bundle.getString("cID");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://116.204.11.171:8080/sus?cID="+cID)
                .header("token",getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token","1"))
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
                        String temp = element.getAsJsonObject().get("code").toString();
                        data.putString("code",temp);
                        int code = Integer.parseInt(temp);
                        if(code!=0){
                            data.putString("msg",element.getAsJsonObject().get("msg").toString());
                            Gson gson = new Gson();
                            list = gson.fromJson(element.getAsJsonObject().get("data").toString(),list.getClass());
                        }
                    }
                }
        );
    }
}
class Suspect{
    public int sID;
    public String stime;
    public float probability;
    public String type;
}