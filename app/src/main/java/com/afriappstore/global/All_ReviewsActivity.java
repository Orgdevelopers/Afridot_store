package com.afriappstore.global;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Adepters.All_reviews_Adapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.Interfaces.ApiCallback;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.ReviewModel;
import com.google.android.gms.common.api.Api;

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

    int page = 0;
    boolean isApiRunning = false;

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


        main_layout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    //                    count++;
                    // on below line we are making our progress bar visible.
                    loading.setVisibility(View.VISIBLE);
                    getData();
                }
            }
        });
    }




    private void getData() {
        if (isApiRunning){
            return;
        }

        isApiRunning = true;

        JSONObject params = new JSONObject();
        try {
            //params.put("app_id",app_id);
            params.put("page",page+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequests.getRequest(this, ApiConfig.showAllReviews+"?app_id=9", params, new ApiCallback() {
            @Override
            public void onResponse(String response) {
                isApiRunning = false;
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equalsIgnoreCase("200")){
                        JSONArray array = resp.getJSONArray("msg");
                        for (int i = 0; i < array.length(); i++) {

                            ReviewModel model = DataParsing.parseReviewModel(array.getJSONObject(i));
                            if (model!=null && model.user !=null){
                                data_list.add(model);
                            }
                            page++;
                            updatedata();

                        }

                    }else{
                        Toast.makeText(All_ReviewsActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                isApiRunning = false;
                Toast.makeText(All_ReviewsActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updatedata() {

                loading.setVisibility(View.INVISIBLE);
                shimmer_loading.setVisibility(View.GONE);
                all_reviews_adapter.notifyDataSetChanged();


    }
}