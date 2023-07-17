package com.example.dangerbehiviordetect.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dangerbehiviordetect.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById(R.id.register_back).setOnClickListener(this);
        findViewById(R.id.sendCode).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register_back){
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(view.getId()==R.id.sendCode){
            view.setEnabled(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    });
                }
            },60000);
            EditText editText1 = findViewById(R.id.tv_email);
//            EditText editText2 = findViewById(R.id.tv_password);
//            EditText editText3 = findViewById(R.id.tv_newpassword);
//            EditText editText4 = findViewById(R.id.code);

//            Toast.makeText(this,"成功发送验证码", Toast.LENGTH_SHORT).show();
            //发送请求
            FormBody body =new FormBody.Builder()
                    .add("mail",editText1.getText().toString())
                    .build();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://116.204.11.171:8080/code")
                    .addHeader("Authorization", "Bearer token")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(
                    new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            // 处理响应成功的情况，解析 responseBody 等
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            String msg = element.getAsJsonObject().get("msg").toString();
                        }
                    }
            );
        }
        if(view.getId()==R.id.btn_register){
            EditText editText1 = findViewById(R.id.tv_email);
            EditText editText2 = findViewById(R.id.tv_password);
            EditText editText3 = findViewById(R.id.tv_newpassword);
            EditText editText4 = findViewById(R.id.code);
            EditText editText5 = findViewById(R.id.tv_username);
            if(editText2.getText().toString().equals(editText3.getText().toString())){
                FormBody body =new FormBody.Builder()
                        .add("username",editText5.getText().toString())
                        .add("mail",editText1.getText().toString())
                        .add("password",editText2.getText().toString())
                        .add("code",editText4.getText().toString())
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://116.204.11.171:8080/register")
                        .post(body)
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
                                if(code !=0){
                                    String msg = element.getAsJsonObject().get("msg").toString();
                                    String token = element.getAsJsonObject().get("data").toString();
                                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token", token);
                                    editor.apply();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }else {
                                    
                                }

                            }
                        }
                );
            }else {
                Toast.makeText(this,"两次密码不一致",Toast.LENGTH_SHORT);
            }

        }
    }
}