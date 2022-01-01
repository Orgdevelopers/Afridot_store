package com.orgdevelopers.hamzs;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.orgdevelopers.hamzs.ApiClasses.ApiConfig;
import com.orgdevelopers.hamzs.ApiClasses.ApiRequests;
import com.orgdevelopers.hamzs.Interfaces.FragmentCallBack;
import com.orgdevelopers.hamzs.SimpleClasses.Functions;
import com.orgdevelopers.hamzs.SimpleClasses.Variables;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    String appid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ApiRequests.getAllapps(getApplicationContext(), new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                proceed();
            }
        });

        ApiRequests.updateShareUrl(this);

        try {
            getSupportActionBar().hide();

        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Intent i = getIntent();
            if (i!=null && i.getData()!=null){
                ////
                if (i.getData().getScheme().equals("https")){
                String split_string= Variables.Http+getResources().getString(R.string.appstore_url_domain)+getResources().getString(R.string.appstore_url_domain_pathprefix)+"?id=";

                try {
                    Uri uri = i.getData();
                    String urL= uri.toString();
                    if (urL.contains(getResources().getString(R.string.appstore_url_domain))){
                        String app_id=urL.replace(split_string,"");
                        appid=app_id;
                    }

                }catch (Exception e ){
                    e.printStackTrace();
                }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void proceed(){
        Functions.ShowToast(SplashActivity.this,"splash timer");
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        try {
            if (!appid.equals("") && !(appid.contains(Variables.Http))){
                intent.putExtra(ApiConfig.POST_App_Id,appid);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

        try {
            Handler handler =new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },10);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}