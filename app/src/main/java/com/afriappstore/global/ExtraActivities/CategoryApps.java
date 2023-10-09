package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.Adepters.CategoryApplistAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.CatAppModel;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;
import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryApps extends AppCompatActivity {

    RecyclerView applist;
    ArrayList<AppModel> dataList;
    String cat_id="1";
    RelativeLayout nothing_found_layout;
    LottieAnimationView animationView;
    TextView nothing_found_text,cat_name_txt;
    ImageView back_btn;
    String cat_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_apps);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            cat_id=getIntent().getStringExtra("id");
            cat_name=getIntent().getStringExtra("name");

        }catch (Exception e){
            e.printStackTrace();
            finish_animated();
        }
        applist=findViewById(R.id.app_list);
        nothing_found_layout=findViewById(R.id.nothing_found_layout);
        nothing_found_text=findViewById(R.id.nothing_found_text);
        animationView=findViewById(R.id.nothing_found_animation);
        back_btn=findViewById(R.id.back_btn);
        cat_name_txt=findViewById(R.id.cat_name_txt);


        cat_name_txt.setText(cat_name);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish_animated();
            }
        });

        Functions.showLoader(this);


        ApiRequests.getCategoryapps(this, cat_id, "0", new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                Functions.cancelLoader();
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){

                    try {
                        JSONArray list=new JSONArray(bundle.getString(ApiConfig.Request_response));

                        dataList=new ArrayList<>();
                        for (int i = 0; i<list.length(); i++){
                            JSONObject app = list.getJSONObject(i);
                            AppModel item = DataParsing.parseAppModel(app);
                            dataList.add(item);
                        }

                    }catch (Exception e){
                        e.printStackTrace();

                    }

                    LinearLayoutManager manager = new LinearLayoutManager(CategoryApps.this);
                    CategoryApplistAdapter adapter = new CategoryApplistAdapter(CategoryApps.this,dataList);
                    applist.setLayoutManager(manager);
                    applist.setAdapter(adapter);

                    applist.setVisibility(View.VISIBLE);
                    nothing_found_layout.setVisibility(View.GONE);

                }else{
                    applist.setVisibility(View.GONE);
                    nothing_found_layout.setVisibility(View.VISIBLE);
                    nothing_found_text.setText(nothing_found_text.getText().toString().replace("this category",cat_name));

                    animationView.setFrame(1);
                    animationView.playAnimation();
                    //Toast.makeText(CategoryApps.this, "no records found", Toast.LENGTH_SHORT).show();
                }
                if (dataList.isEmpty()){
                    applist.setVisibility(View.GONE);
                    nothing_found_layout.setVisibility(View.VISIBLE);
                    nothing_found_text.setText(nothing_found_text.getText().toString().replace("this category",cat_name));

                    animationView.setFrame(1);
                    animationView.playAnimation();

                }

            }
        });

        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationView.pauseAnimation();
                animationView.setFrame(1);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

    }

    public void finish_animated(){
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

    }
}