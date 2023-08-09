package com.afriappstore.global;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afriappstore.global.Adepters.All_reviews_Adapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.ReviewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class All_ReviewsActivity extends AppCompatActivity {
    RecyclerView all_review_list;
    NestedScrollView main_layout;
    ProgressBar loading;
    ArrayList<ReviewModel> data_list;
    int app_id;
    Boolean first_time=true;
    LinearLayout shimmer_loading;
    All_reviews_Adapter all_reviews_adapter;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);
        //
        try {
            Intent start_value=getIntent();
            if (start_value!=null && !start_value.getStringExtra(ApiConfig.POST_App_Id).equals("")){
                app_id=Integer.parseInt(start_value.getStringExtra(ApiConfig.POST_App_Id));
            }else {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        }catch (Exception e){
            e.printStackTrace();
            finish();
            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
        }

        data_list=new ArrayList<>();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        View view =getSupportActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        });


        //find views
        all_review_list = findViewById(R.id.all_review_list);
        main_layout = findViewById(R.id.idNestedSV);
        loading = findViewById(R.id.loading_progress_bar);
        shimmer_loading=findViewById(R.id.shimmer_outer_layout);
        getData();

        all_reviews_adapter = new All_reviews_Adapter(All_ReviewsActivity.this,data_list);
        LinearLayoutManager manager =new LinearLayoutManager(All_ReviewsActivity.this);
        all_review_list.setLayoutManager(manager);
        all_review_list.setAdapter(all_reviews_adapter);




    }

    private void getData() {

        ApiRequests.getAllReviews(All_ReviewsActivity.this, String.valueOf(app_id), new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                    try {
                        String response=bundle.getString(ApiConfig.Request_response);
                        JSONObject resp = new JSONObject(response);
                         if (resp.getString("code").equalsIgnoreCase("200")){
                             JSONArray array = resp.getJSONArray("msg");
                             for (int i = 0; i < array.length(); i++) {

                                 ReviewModel model = DataParsing.parseReviewModel(array.getJSONObject(i));
                                 data_list.add(model);
                                 updatedata();

                             }

                         }



                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(All_ReviewsActivity.this, "error at all reviews:148 please contact developer", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    private void updatedata() {

                loading.setVisibility(View.INVISIBLE);
                shimmer_loading.setVisibility(View.GONE);
                all_reviews_adapter.notifyDataSetChanged();


    }
}