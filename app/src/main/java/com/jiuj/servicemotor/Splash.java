package com.jiuj.servicemotor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.karan.churi.PermissionManager.PermissionManager;

public class Splash extends AppCompatActivity {
    Activity activ;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        if (android.os.Build.VERSION.SDK_INT >= 23) {
            permissionManager = new PermissionManager() {};
            permissionManager.checkAndRequestPermissions(this);
        }

        this.activ = this;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions,grantResults);
    }
}
