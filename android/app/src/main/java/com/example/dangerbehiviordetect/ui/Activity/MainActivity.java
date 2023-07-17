package com.example.dangerbehiviordetect.ui.Activity;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.example.dangerbehiviordetect.R;
import com.example.dangerbehiviordetect.ui.gallery.GalleryFragment;
import com.example.dangerbehiviordetect.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dangerbehiviordetect.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;//表示让应用主题内容占据系统状态栏的空间
            decorView.setSystemUiVisibility(option);
            setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色为透明
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                navController.navigate(R.id.fragment_slideshow);
            }
        });


        FloatingActionButton floatingActionButton = binding.appBarMain.fab;

        FloatingActionButton fab1 = binding.appBarMain.fab1;
        FloatingActionButton fab2 = binding.appBarMain.fab2;
        FloatingActionButton fab3 = binding.appBarMain.fab3;

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_gallery);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_home);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_slideshow);
            }
        });

        //Animations
        Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_fade);

        Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_fade);

        Animation show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        Animation hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_fade);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) fab1.getLayoutParams();
                    layoutParams1.rightMargin -= (int) (fab1.getWidth() * 1.4);
                    layoutParams1.bottomMargin -= (int) (fab1.getHeight() * 1.5);
                    fab1.setLayoutParams(layoutParams1);
                    fab1.startAnimation(hide_fab_1);
                    fab1.setClickable(false);

                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
                    layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.7);
                    layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 0.25);
                    fab2.setLayoutParams(layoutParams2);
                    fab2.startAnimation(hide_fab_2);
                    fab2.setClickable(false);
//
                    FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
                    layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
                    layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
                    fab3.setLayoutParams(layoutParams3);
                    fab3.startAnimation(hide_fab_3);
                    fab3.setClickable(false);
                    flag = false;
                }else {
                    FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) fab1.getLayoutParams();
                    layoutParams1.rightMargin += (int) (fab1.getWidth() * 1.4);
                    layoutParams1.bottomMargin += (int) (fab1.getHeight() * 1.5);
                    fab1.setLayoutParams(layoutParams1);
                    fab1.startAnimation(show_fab_1);
                    fab1.setClickable(true);


                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
                    layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.7);
                    layoutParams2.bottomMargin += (int) (fab2.getHeight() * 0.25);
                    fab2.setLayoutParams(layoutParams2);
                    fab2.startAnimation(show_fab_2);
                    fab2.setClickable(true);
//
//
                    FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
                    layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
                    layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
                    fab3.setLayoutParams(layoutParams3);
                    fab3.startAnimation(show_fab_3);
                    fab3.setClickable(true);

                    flag = true;
                }
            }
        });
    }
    public void setStatusBarColor(int color) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}