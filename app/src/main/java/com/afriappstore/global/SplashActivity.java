package com.afriappstore.global;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;

import io.paperdb.Paper;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    String appid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Paper.init(this);

        proceed();

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
            },2000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}