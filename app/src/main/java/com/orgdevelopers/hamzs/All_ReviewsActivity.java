package com.orgdevelopers.hamzs;

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

import com.orgdevelopers.hamzs.Adepters.All_reviews_Adapter;
import com.orgdevelopers.hamzs.ApiClasses.ApiConfig;
import com.orgdevelopers.hamzs.ApiClasses.ApiRequests;
import com.orgdevelopers.hamzs.Interfaces.FragmentCallBack;
import com.orgdevelopers.hamzs.Model.Review_Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class All_ReviewsActivity extends AppCompatActivity {
    RecyclerView all_review_list;
    NestedScrollView main_layout;
    ProgressBar loading;
    ArrayList<Review_Model> data_list;
    int total_reviews=0;
    int app_id;
    Boolean first_time=true;
    LinearLayout shimmer_loading;

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
        all_review_list=findViewById(R.id.all_review_list);
        main_layout=findViewById(R.id.idNestedSV);
        loading=findViewById(R.id.loading_progress_bar);
        shimmer_loading=findViewById(R.id.shimmer_outer_layout);
        getData(0);

        main_layout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {

                    if (!first_time){
                        loading.setVisibility(View.VISIBLE);
                    }
                    getData(total_reviews);
                }
            }
        });

    }

    private void getData(int sp) {

        ApiRequests.getAllReviews(All_ReviewsActivity.this, String.valueOf(app_id), sp, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                    try {
                        String response=bundle.getString(ApiConfig.Request_response);
                        JSONArray temp_array=new JSONArray(response);
                        String username,image,stars,date,review;

                        if (first_time){
                            shimmer_loading.setVisibility(View.GONE);
                            main_layout.setVisibility(View.VISIBLE);
                            first_time=false;
                        }

                        for (int i=0;i<temp_array.length();i++){
                            JSONObject object = temp_array.getJSONObject(i);
                            username=object.getString("username");
                            image=object.getString("pic");
                            stars=object.getString("star");
                            date=object.getString("date");
                            review= object.getString("review");

                            Review_Model model = new Review_Model();
                            model.username=username;
                            model.image=image;
                            model.stars=stars;
                            model.date=date;
                            model.review=review;

                            data_list.add(model);

                            total_reviews++;
                        }

                        All_reviews_Adapter all_reviews_adapter = new All_reviews_Adapter(All_ReviewsActivity.this,data_list);
                        LinearLayoutManager manager =new LinearLayoutManager(All_ReviewsActivity.this);
                        all_review_list.setLayoutManager(manager);
                        all_review_list.setAdapter(all_reviews_adapter);
                        loading.setVisibility(View.INVISIBLE);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(All_ReviewsActivity.this, "error at all reviews:148 please contact developer", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
}