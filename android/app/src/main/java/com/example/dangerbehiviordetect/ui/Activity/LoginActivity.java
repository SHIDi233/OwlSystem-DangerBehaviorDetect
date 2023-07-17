package com.example.dangerbehiviordetect.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dangerbehiviordetect.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//2982437139@qq.com
//731596
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "login_password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;//表示让应用主题内容占据系统状态栏的空间
            decorView.setSystemUiVisibility(option);
            setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色为透明
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        TextInputLayout textInputLayout = findViewById(R.id.til_password);
        textInputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

    }

    public void setStatusBarColor(int color) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.register){
            startActivity(new Intent(this, RegisterActivity.class));
        }
        if(v.getId() == R.id.login_button){
            //发送请求
            EditText editText = findViewById(R.id.et_email);
            String mail = editText.getText().toString();
            EditText password = findViewById(R.id.et_password);
            String pw = password.getText().toString();
            FormBody body = new FormBody.Builder()
                    .add("mail",mail)
                    .add("password",pw)
                    .build();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://116.204.11.171:8080/login")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(
                    new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            // 处理响应失败的情况，获取错误信息等
                            Log.d(TAG, "请求失败");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            // 处理响应成功的情况，解析 responseBody 等
                            JsonElement element = JsonParser.parseString(responseBody);
                            int code;
                            try {
                                code = Integer.parseInt(element.getAsJsonObject().get("code").toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                code=0;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"登陆出错",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            if(code!=1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"账号或者密码错误",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                            if(code!=0){
                                String msg = element.getAsJsonObject().get("msg").toString();
                                String token = element.getAsJsonObject().get("data").toString();
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", token.substring(1,token.length()-1));
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
            );
//            Log.d(TAG, "onClick: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        }
    }
}