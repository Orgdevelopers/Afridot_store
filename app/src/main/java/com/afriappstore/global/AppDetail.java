package com.afriappstore.global;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Adepters.All_reviews_Adapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.ExtraActivities.ReviewActivity;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.AppSettings;
import com.afriappstore.global.Model.Download_model;
import com.afriappstore.global.Model.ReviewModel;
import com.afriappstore.global.Profile.LoginActivity;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.ShearedPrefs;
import com.afriappstore.global.SimpleClasses.Variables;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.taufiqrahman.reviewratings.Bar;
import com.taufiqrahman.reviewratings.RatingReviews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class AppDetail extends AppCompatActivity {

    Button install_btn;
    ImageView app_icon,back_button,share_btn,rating_1star,rating_2star,rating_3star,rating_4star,rating_5star,show_allrating_arrow;
    TextView app_size, app_name,app_description,read_more,rating_big_text,horizontal_bar_rating,horizontal_bar_review_count,horizontal_bar_dow;


    //rating
    int rating_click_switch=0;


    TextView r_username1,r_username2,r_username3,see_all_rev_txt_bottom,total_rates_txt;
    TextView r_discription1,r_discription2,r_discription3;
    TextView r_date1,r_date2,r_date3;
    TextView my_review_username,my_detailed_review,my_review_date,edit_review_btn;
    ImageView myrevstar1,myrevstar2,myrevstar3,myrevstar4,myrevstar5;
    ImageView r_star11,r_star12,r_star13,r_star14,r_star15,r_star21,r_star22,r_star23,r_star24,r_star25,r_star31,r_star32,r_star33,r_star34,r_star35;
    CircleImageView r_profile1,r_profile2,r_profile3,my_review_profile;
    LinearLayout rating_bar_background_layout,shimmer_layout,rating_bar_bg,my_review_layout,some_reviews,first_review_layout,second_review_layout,third_review_layout,no_review_found;
    ScrollView all_stuff;

    //rating
    public Integer pos;

    int UnknownSourcePermission=1827;

    RecyclerView recyclerView;

    //new update
    Context context;
    Picasso picasso;

    //models
    AppSettings appSettings;
    AppModel item;

    //variables
    String app_id = "0";
    boolean is_installed = false;

    //views
    RatingBar ratingBar, rating_bar_small, my_rating_bar;
    RatingReviews ratingReviews;
    RecyclerView someReviewsRcView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        context=AppDetail.this;
        picasso = Picasso.get();

        appSettings = Paper.book().read("appSettings",new AppSettings());

        //views
        init_views();
        //init_ratings(context);

        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("app_id")) {
                app_id = intent.getStringExtra("app_id");


            } else {
                finish();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            finish();
        }

//        shimmer_layout.setVisibility(View.VISIBLE);
//        all_stuff.setVisibility(View.GONE);

        install_btn.setBackgroundColor(getResources().getColor(R.color.install_btn_bg));

        init_data();

        callApi();

    }

    private void callApi() {
        shimmer_layout.setVisibility(View.VISIBLE);
        all_stuff.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.showAppDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("appDetails",response);
                shimmer_layout.setVisibility(View.GONE);
                all_stuff.setVisibility(View.VISIBLE);

                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equalsIgnoreCase("200")){

                        item = DataParsing.parseAppModel(resp.getJSONObject("msg"));
                        updateData();

                    }else{
                        Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //shimmer_layout.setVisibility(View.GONE);
                Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                finish();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("app_id",app_id);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void init_data() {

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = Variables.Http+getResources().getString(R.string.appstore_url_domain)+getResources().getString(R.string.appstore_url_domain_pathprefix)+"?id="+item.id;
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT,link);
                startActivity(i);

            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        });



    }


    private void updateData(){
        if (item!= null){

            is_installed = isPackageInstalled(item.package_name, getPackageManager());
            set_local_description(context,item.long_description);

            //fill data
            app_name.setText(item.app_name);
            app_size.setText(item.size+" MB");
            total_rates_txt.setText(Functions.Format_numbers(item.Ratings.total));
            horizontal_bar_review_count.setText(Functions.Format_numbers(item.Ratings.total));

            //rating bars
            String[] labels = new String[]{"5 ","4 ","3 ","2 ","1 "};
            int[] values = new int[]{item.Ratings.star5,item.Ratings.star4,item.Ratings.star3,item.Ratings.star2, item.Ratings.star1};
            ratingReviews.createRatingBars(item.Ratings.total,labels, getResources().getColor(R.color.install_btn_bg),values, getResources().getColor(R.color.light_white));

            if (appSettings.user_actual_rating){
                //use live calculated rating
                rating_bar_small.setRating(Float.parseFloat(item.Ratings.rating));
                horizontal_bar_rating.setText(item.Ratings.rating);
                rating_big_text.setText(item.Ratings.rating);

            }else{
                //use fake rating set by admin
                rating_bar_small.setRating(Float.parseFloat(item.rating));
                horizontal_bar_rating.setText(item.rating);
                rating_big_text.setText(item.rating);

            }

            // reviews
            if (item.Reviews != null && item.Reviews.size() > 0){
                some_reviews.setVisibility(View.VISIBLE);
                no_review_found.setVisibility(View.GONE);
                All_reviews_Adapter adapter = new All_reviews_Adapter(AppDetail.this,item.Reviews);
                LinearLayoutManager manager = new LinearLayoutManager(AppDetail.this);

                someReviewsRcView.setLayoutManager(manager);
                someReviewsRcView.setAdapter(adapter);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int height = item.Reviews.size() * someReviewsRcView.getChildAt(0).getHeight();

                        ViewGroup.LayoutParams layoutParams = someReviewsRcView.getLayoutParams();
                        layoutParams.height = height;
                        someReviewsRcView.setLayoutParams(layoutParams);

                    }
                },10);

            }else{
                no_review_found.setVisibility(View.VISIBLE);
                some_reviews.setVisibility(View.GONE);

            }

            picasso.load(item.app_icon).into(app_icon, new Callback() {
                @Override
                public void onSuccess() {
                    //loaded
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();

                }
            });

            horizontal_bar_dow.setText(Functions.Format_numbers(Integer.parseInt(item.downloads)));


            install_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (is_installed){
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(item.package_name);
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        } else {
                            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                        }


                    }else if (item.package_name.equals("coming soon")){
                        Toast.makeText(AppDetail.this, "This app will be available soon", Toast.LENGTH_SHORT).show();

                    }else if (item.package_name.contains("https://")) {

                        Intent it = new Intent();
                        it.setAction(Intent.ACTION_VIEW);
                        it.setData(Uri.parse(item.package_name));
                        startActivity(it);

                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (!getPackageManager().canRequestPackageInstalls()){

                                Functions.Showdouble_btn_alert(context, "Permission needed for auto install", "click on settings and allow permission for enabling auto install", "cancel", "settings", false, new FragmentCallBack() {
                                    @Override
                                    public void onResponse(Bundle bundle) {
                                        if (bundle.getString("action").equals("ok")){
                                            startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:"+ BuildConfig.APPLICATION_ID)));

                                        }else{
                                            Toast.makeText(context, "permission required for install", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                //Toast.makeText(context, "do rest", Toast.LENGTH_SHORT).show();
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                                    do_rest();
                                }else{
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9090);

                                }

                            }

                        }else{
                            do_rest();

                        }

                    }


                }
            });


            if (is_installed) {
                install_btn.setText("OPEN");
                rating_bar_bg.setVisibility(View.VISIBLE);
                if (Functions.is_Login(context)){
                    my_review_layout.setVisibility(View.GONE);
                }

            } else if (item.package_name.equals("coming soon")) {
                install_btn.setText("COMING SOON");

            } else if (item.package_name.contains("https://")) {
                install_btn.setText("BROWS");

            } else {
                install_btn.setText("INSTALL");

            }

        }
    }


    public ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                    }
                }
            });

    private void init_views() {

        install_btn = findViewById(R.id.install_btn);
        app_icon = findViewById(R.id.app_icon_img);
        app_size = findViewById(R.id.app_size_txt);
        app_name = findViewById(R.id.app_name_txt);
        back_button=findViewById(R.id.appdetailback);
        share_btn=findViewById(R.id.appdetailshare);
        app_description=findViewById(R.id.description_txt);
        read_more=findViewById(R.id.read_more);
        read_more.setVisibility(View.INVISIBLE);
        someReviewsRcView = findViewById(R.id.some_reviews_rcView);
        ratingReviews = findViewById(R.id.rating_reviews);

        //vertical bar
        horizontal_bar_dow=findViewById(R.id.horizontal_bar_dow);
        horizontal_bar_rating=findViewById(R.id.horizontal_bar_rating);
        horizontal_bar_review_count=findViewById(R.id.horizontal_bar_review_count);
        //vertical bar


        rating_big_text=findViewById(R.id.big_rating_text);
//        rating_1star=findViewById(R.id.rating_star_fill1);
//        rating_2star=findViewById(R.id.rating_star_fill2);
//        rating_3star=findViewById(R.id.rating_star_fill3);
//        rating_4star=findViewById(R.id.rating_star_fill4);
//        rating_5star=findViewById(R.id.rating_star_fill5);
        show_allrating_arrow=findViewById(R.id.show_all_ratings_arrow);
        rating_bar_background_layout=findViewById(R.id.rating_bar_background_layout);
        total_rates_txt=findViewById(R.id.total_rates_txt);

        ratingBar=findViewById(R.id.rating_bar);
        rating_bar_small = findViewById(R.id.rating_bar_small);
        my_rating_bar = findViewById(R.id.my_review_rating_bar);

        rating_bar_bg=findViewById(R.id.rating_bar_bgbgb);

        my_review_layout=findViewById(R.id.my_review_layout);
        some_reviews=findViewById(R.id.some_reviews);
        first_review_layout=findViewById(R.id.first_review_layout);
        second_review_layout=findViewById(R.id.second_review_layout);
        third_review_layout=findViewById(R.id.third_review_layout);

//        myrevstar1=findViewById(R.id.my_review_star1);
//        myrevstar2=findViewById(R.id.my_review_star2);
//        myrevstar3=findViewById(R.id.my_review_star3);
//        myrevstar4=findViewById(R.id.my_review_star4);
//        myrevstar5=findViewById(R.id.my_review_star5);
        my_review_username=findViewById(R.id.my_review_username);
        my_review_date=findViewById(R.id.my_review_date);
        my_detailed_review=findViewById(R.id.my_detailed_review);
        my_review_profile=findViewById(R.id.my_review_img);
        edit_review_btn=findViewById(R.id.edit_review_btn);

        no_review_found = findViewById(R.id.no_rev_found_layout);


        //shimmer
        shimmer_layout=findViewById(R.id.shimmer_layout_app_detail);
        all_stuff=findViewById(R.id.all_stuff);


        check_myreview();
        //rating bar setup
        rating_bar_bg.setVisibility(View.GONE);
        ratingBar.setRating(0);
        ratingBar.setStepSize(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if (rating_click_switch==0){
                    rating_click_switch=1;
                    //Toast.makeText(context, "This function is in development", Toast.LENGTH_SHORT).show();
                    if (Functions.is_Login(context)){
                        Intent reviewsA = new Intent(context, ReviewActivity.class);
                        reviewsA.putExtra("mode","post");
                        reviewsA.putExtra("rating",ratingBar.getRating());
                        reviewsA.putExtra("pos",pos);

                        startActivity(reviewsA);
                        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                    }else{

                        ratingBar.setRating(0);
                        Functions.Showdouble_btn_alert(context, "You are not Logged in", "log in to see your profile and enjoy other benefits", "Cancel", "Log in", true, new FragmentCallBack() {
                            @Override
                            public void onResponse(Bundle bundle) {
                                if (bundle.getString("action").equals("ok")){
                                    logIn();
                                }
                            }
                        });
                    }

                    CountDownTimer timer = null;
                    timer=new CountDownTimer(200,100) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            rating_click_switch=0;
                        }
                    };

                    try {
                        timer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }




            }
        });

        rating_bar_background_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                see_all_reviews(item.id);

            }
        });

        see_all_rev_txt_bottom=findViewById(R.id.see_all_rev_btn);

        see_all_rev_txt_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                see_all_reviews(item.id);

            }
        });

        show_allrating_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                see_all_reviews(item.id);

            }
        });


    }

    private void check_myreview() {

        if (Functions.is_Login(context)){
            ApiRequests.checkReviewForThisApp(context, item.id, new FragmentCallBack() {
                @Override
                public void onResponse(Bundle bundle) {
                    String code = bundle.getString(ApiConfig.Request_code);
                    if (code.equals(ApiConfig.RequestSuccess)){

                        try {
                            my_review_layout.setVisibility(View.VISIBLE);
                            ratingBar.setVisibility(View.GONE);
                            JSONObject rev = new JSONObject(bundle.getString(ApiConfig.Request_response));
                            String name = Functions.getSharedPreference(context).getString(ShearedPrefs.U_FNAME,"")+" "+Functions.getSharedPreference(context).getString(ShearedPrefs.U_LNAME,"");
                            my_review_username.setText(name);
                            Variables.my_review=rev.getString("review");
                            my_detailed_review.setText(rev.getString("review"));
                            my_review_date.setText(rev.getString("date"));
                            String star = rev.getString("star");
                            String pic = Functions.getSharedPreference(context).getString(ShearedPrefs.U_PIC,"");

                            edit_review_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent edit_review = new Intent(AppDetail.this,ReviewActivity.class);
                                    edit_review.putExtra("mode","edit");
                                    edit_review.putExtra("rating",Float.valueOf(star));
                                    edit_review.putExtra("review",my_detailed_review.getText());
                                    edit_review.putExtra("pos",pos);
                                    //Toast.makeText(context, "bfbdjjrhudbfiu", Toast.LENGTH_SHORT).show();
                                    startActivity(edit_review);
                                }
                            });

                            //Toast.makeText(context, ""+star, Toast.LENGTH_SHORT).show();

                            if (star.equals("5")){
                                //
                                myrevstar5.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                                myrevstar4.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                                myrevstar3.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                                myrevstar2.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                            }else if (star.equals("4")){
                                //uncheck stars
                                myrevstar5.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                //check stars
                                myrevstar4.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                                myrevstar3.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                                myrevstar2.setColorFilter(getResources().getColor(R.color.install_btn_bg));

                            }else if (star.equals("3")){
                                myrevstar5.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                myrevstar4.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                //
                                myrevstar3.setColorFilter(getResources().getColor(R.color.install_btn_bg));
                                myrevstar2.setColorFilter(getResources().getColor(R.color.install_btn_bg));

                            }else if (star.equals("2")){
                                myrevstar5.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                myrevstar4.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                myrevstar3.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                //
                                myrevstar2.setColorFilter(getResources().getColor(R.color.install_btn_bg));

                            }else if (star.equals("1")){
                                myrevstar5.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                myrevstar4.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                myrevstar3.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                                myrevstar2.setColorFilter(getResources().getColor(R.color.ultra_light_grey));

                            }


                            if (!pic.equals("default")){
                                Picasso picasso = Picasso.get();
                                picasso.setLoggingEnabled(false);
                                pic=ApiConfig.Base_url+pic;

                                String finalPic = pic;
                                picasso.load(pic).fetch(new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        picasso.load(finalPic).into(my_review_profile);
                                        picasso.setLoggingEnabled(true);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        picasso.setLoggingEnabled(true);
                                        my_review_profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));
                                    }
                                });
                            }

                            //set_reviews(context);
                            //Toast.makeText(context, ""+bundle.getString(ApiConfig.Request_response), Toast.LENGTH_SHORT).show();

                        }catch (Exception e){
                            e.printStackTrace();
                            my_review_layout.setVisibility(View.GONE);
                            ratingBar.setVisibility(View.VISIBLE);
                        }



                    }else{
                        //Toast.makeText(context,bundle.getString(ApiConfig.Request_response),Toast.LENGTH_SHORT).show();
                        my_review_layout.setVisibility(View.GONE);
                        ratingBar.setVisibility(View.VISIBLE);
                    }

                }
            });
        }else{
            my_review_layout.setVisibility(View.GONE);
            ratingBar.setVisibility(View.VISIBLE);
        }
    }

    private void logIn(){
        Intent i= new Intent(this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    private void init_ratings(Context context){

//        r_star11=findViewById(R.id.review_star_fill11);
//        r_star12=findViewById(R.id.review_star_fill21);
//        r_star13=findViewById(R.id.review_star_fill31);
//        r_star14=findViewById(R.id.review_star_fill41);
//        r_star15=findViewById(R.id.review_star_fill51);

        r_star21=findViewById(R.id.review_star_fill12);
        r_star22=findViewById(R.id.review_star_fill22);
        r_star23=findViewById(R.id.review_star_fill32);
        r_star24=findViewById(R.id.review_star_fill42);
        r_star25=findViewById(R.id.review_star_fill52);

        r_star31=findViewById(R.id.review_star_fill13);
        r_star32=findViewById(R.id.review_star_fill23);
        r_star33=findViewById(R.id.review_star_fill33);
        r_star34=findViewById(R.id.review_star_fill43);
        r_star35=findViewById(R.id.review_star_fill53);


        r_profile1=findViewById(R.id.review_user_img1);
        r_profile2=findViewById(R.id.review_user_img2);
        r_profile3=findViewById(R.id.review_user_img3);

        r_date1=findViewById(R.id.review_date1);
        r_date2=findViewById(R.id.review_date2);
        r_date3=findViewById(R.id.review_date3);

        r_discription1= findViewById(R.id.detaild_review1);
        r_discription2 = findViewById(R.id.detaild_review2);
        r_discription3 = findViewById(R.id.detaild_review3);

        r_username1 = findViewById(R.id.review_username1);
        r_username2 = findViewById(R.id.review_username2);
        r_username3 = findViewById(R.id.review_username3);


        ///////////////////////////////////////////////////////////////

    }

    private void set_local_description(Context context,String desc) {
        app_description.setText(desc);
        read_more.setVisibility(View.VISIBLE);
        final Integer[] desc_switch = {0};
        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (desc_switch[0] ==0){
                    read_more.setText("read less");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMarginStart(80);
                    params.setMarginEnd(80);
                    app_description.setLayoutParams(params);
                    desc_switch[0] =1;
                }else if (desc_switch[0]==1){
                    read_more.setText("read more");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110);
                    params.setMarginStart(80);
                    params.setMarginEnd(80);
                    app_description.setLayoutParams(params);
                    desc_switch[0] =0;
                }

            }
        });
    }

//
//    private void save_description(String resp, String appid) {
//        if (Variables.description_array==null){
//            Variables.description_array=new JSONArray();
//        }
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put(appid,resp);
//            Variables.description_array.put(Integer.parseInt(appid),object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void do_rest(){
        ArrayList<Download_model> downloaded_apps = Paper.book().read("downloads",new ArrayList<>());
        if (downloaded_apps != null){
            for (int i = 0; i < downloaded_apps.size(); i++) {
                Download_model model = downloaded_apps.get(i);
                if (model.app_id.equalsIgnoreCase(item.id) && model.version.equalsIgnoreCase(item.version)){
                    //already downloaded
                    installApp(model.file_path);
                    return;
                }

            }
        }

        //download and install
        try {
            File folder = new File(Variables.AppDownloadpath);
            if(!folder.exists()){
                folder.mkdirs();

            }



            Functions.showDownloadConfirmer(context, item.app_name, item.size, false, new FragmentCallBack() {
                @Override
                public void onResponse(Bundle bundle) {
                    if (bundle.getString("click").equals("download")) {

                        String download_link;
                        if(item.download_link.contains("http")){
                            download_link = item.download_link;
                        }else {
                            download_link=ApiConfig.Base_url + item.download_link;
                        }

                        Functions.DownloadWithLoading(context,download_link, Variables.AppDownloadpath, item.app_name, item, new FragmentCallBack() {
                            @Override
                            public void onResponse(Bundle bundle) {
                                if (bundle.getString("action").equals("install")) {
                                    String path = bundle.getString("path");

                                    Download_model model = new Download_model();
                                    model.app_id = item.id;
                                    model.version = item.version;
                                    model.file_path = path;

                                    ArrayList<Download_model> downloaded_apps = Paper.book().read("downloads",new ArrayList<>());

                                    if (downloaded_apps == null) downloaded_apps = new ArrayList<>();

                                    downloaded_apps.add(model);
                                    Paper.book().write("downloads", downloaded_apps);

                                    installApp(path);


                                } else {
                                    //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                    //Toast.makeText(context, ""+bundle.getString("click"), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installApp(String apk_path){
        Uri app_uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(apk_path));

        //installPackage(apk);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        install.setData(app_uri);
        //install.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        startActivity(install);

    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void installPackage(File file) {
        String ACTION_INSTALL_COMPLETE = "com.orgdevelopers.hamzs.INSTALL_COMPLETE";

        PackageInstaller pi = getApplicationContext().getPackageManager().getPackageInstaller();
        int sessionId =
                0;
        try {
            sessionId = pi.createSession(new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));

            PackageInstaller.Session session = pi.openSession(sessionId);


            long sizeBytes = 0;

            if (file.isFile()) {
                sizeBytes = file.length();
            }

            InputStream input = null;
            OutputStream out = null;

            try {
                input = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            out = session.openWrite("app_store_session", 0, sizeBytes);

            long total = 0;

            byte[] buffer = new byte[65536];

            int len;

            do {
                len = input.read(buffer);
                if (len != -1) {
                    total += len;
                    out.write(buffer, 0, len);
                }

            } while (len != -1);

            session.fsync(out);
            input.close();
            out.close();

            PendingIntent broadCastTest = PendingIntent.getBroadcast(getApplicationContext(),
                    sessionId, new Intent(ACTION_INSTALL_COMPLETE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(broadCastTest.getIntentSender());
            session.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        try {
            my_detailed_review.setText(Variables.my_review);

            check_myreview();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
        super.onBackPressed();
    }

    private void see_all_reviews(String app_id){
        Intent intent = new Intent(context,All_ReviewsActivity.class);
        intent.putExtra(ApiConfig.POST_App_Id,app_id);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    public void setfirst(JSONObject first){
        try {
            //set first
            if (!first.getString("pic").equals("")){
                Picasso picasso = Picasso.get();
                picasso.setLoggingEnabled(false);
                picasso.load(first.getString("pic")).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            picasso.load(first.getString("pic")).into(r_profile1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        picasso.setLoggingEnabled(true);

                    }

                    @Override
                    public void onError(Exception e) {
                        r_profile1.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));
                        picasso.setLoggingEnabled(true);

                    }
                });
            }
            r_username1.setText(first.getString("username"));

            r_date1.setText(first.getString("date"));

            r_discription1.setText(first.getString("review"));

            String start1=first.getString("star");
            if (start1.equals("5")){
                //
            }else if (start1.equals("4")){
                r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }else if (start1.equals("3")){
                r_star14.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));

            }else if (start1.equals("2")){
                r_star14.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star13.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }else if (start1.equals("1")){
                r_star14.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star13.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star12.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void settwo(JSONObject first,JSONObject second){
        Picasso picasso = Picasso.get();

        try {

        //set first
        if (!first.getString("pic").equals("")){
            picasso.setLoggingEnabled(false);
            picasso.load(first.getString("pic")).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    try {
                        picasso.load(first.getString("pic")).into(r_profile1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    picasso.setLoggingEnabled(true);

                }

                @Override
                public void onError(Exception e) {
                    r_profile1.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));
                    picasso.setLoggingEnabled(true);

                }
            });
        }
        r_username1.setText(first.getString("username"));

        r_date1.setText(first.getString("date"));

        r_discription1.setText(first.getString("review"));

        String start1=first.getString("star");
        if (start1.equals("5")){
            //
        }else if (start1.equals("4")){
            r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
        }else if (start1.equals("3")){
            r_star14.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));

        }else if (start1.equals("2")){
            r_star14.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            r_star13.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
        }else if (start1.equals("1")){
            r_star14.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            r_star15.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            r_star13.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            r_star12.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
        }



            //set second
            if (!second.getString("pic").equals("")){
                picasso.setLoggingEnabled(false);
                picasso.load(second.getString("pic")).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            picasso.load(second.getString("pic")).into(r_profile2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        picasso.setLoggingEnabled(true);

                    }

                    @Override
                    public void onError(Exception e) {
                        r_profile2.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));
                        picasso.setLoggingEnabled(true);

                    }
                });

            }
            r_username2.setText(second.getString("username"));

            r_date2.setText(second.getString("date"));

            r_discription2.setText(second.getString("review"));

            String start2=second.getString("star");
            if (start2.equals("5")){
                //
            }else if (start2.equals("4")){
                r_star25.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }else if (start2.equals("3")){
                r_star24.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star25.setColorFilter(getResources().getColor(R.color.ultra_light_grey));

            }else if (start2.equals("2")){
                r_star24.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star25.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star23.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }else if (start2.equals("1")){
                r_star24.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star25.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star23.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star22.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    public void setall(JSONObject first,JSONObject second,JSONObject third){

        try {

            settwo(first,second);

            Picasso picasso = Picasso.get();
            //set third
            if (!third.getString("pic").equals("")){
                picasso.setLoggingEnabled(false);
                picasso.load(third.getString("pic")).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            picasso.load(third.getString("pic")).into(r_profile3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        picasso.setLoggingEnabled(true);

                    }

                    @Override
                    public void onError(Exception e) {
                        r_profile3.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));
                        picasso.setLoggingEnabled(true);

                    }
                });
                picasso.load(third.getString("pic")).into(r_profile3);
            }
            r_username3.setText(third.getString("username"));

            r_date3.setText(third.getString("date"));

            r_discription3.setText(third.getString("review"));

            String start3=third.getString("star");
            if (start3.equals("5")){
                //
            }else if (start3.equals("4")){
                r_star35.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }else if (start3.equals("3")){
                r_star34.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star35.setColorFilter(getResources().getColor(R.color.ultra_light_grey));

            }else if (start3.equals("2")){
                r_star34.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star35.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star33.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }else if (start3.equals("1")){
                r_star34.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star35.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star33.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
                r_star32.setColorFilter(getResources().getColor(R.color.ultra_light_grey));
            }

        }catch (Exception e ){
            e.printStackTrace();
        }


    }


}