package com.afriappstore.global;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Adepters.AppSlideradapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ExtraActivities.ReviewActivity;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.Download_model;
import com.afriappstore.global.Profile.LoginActivity;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.ShearedPrefs;
import com.afriappstore.global.SimpleClasses.Variables;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class AppDetail extends AppCompatActivity {

    Button install_btn;
    ImageView app_icon,back_button,share_btn,rating_1star,rating_2star,rating_3star,rating_4star,rating_5star,show_allrating_arrow;
    TextView app_size, app_name,app_description,read_more,rating_big_text,horizontal_bar_rating,horizontal_bar_review_count,horizontal_bar_dow;

    boolean IS_installed=false;

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
    RatingBar ratingBar;
    RatingReviews ratingReviews;

    //rating
    public Integer pos;

    int UnknowSourcePermission=1827;

    JSONObject app = null;
    RecyclerView recyclerView;
    String p_App_id="";
    String downloads_count="";

    Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        //checking for data

        if (Variables.array==null){
            startActivity(new Intent(this,SplashActivity.class));
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },200);
            return;
        }

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
        if (intent != null) {

            String code=intent.getStringExtra(ApiConfig.Request_code);
            if (code.equals(ApiConfig.POST_App_Id)){
                int id=intent.getIntExtra(ApiConfig.POST_App_Id,0);
                if (id!=0){
                    pos=Functions.convert_appid_to_pos(id);


                }else{
                    finish();
                }

            }else{
                pos=intent.getIntExtra("pos",0);
            }

        } else {
            finish();
        }
        }catch (Exception e)
        {
            e.printStackTrace();
            finish();
        }

        try {
            app = Variables.array.getJSONObject(pos);

            downloads_count=Functions.Format_numbers(Integer.parseInt(app.getString("downloads")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (app == null) {
            finish();
        }

        //checking for data end

        //shimmer loading
        CountDownTimer loadin= new CountDownTimer(2000,500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                try {
                    shimmer_layout.setVisibility(View.GONE);
                    all_stuff.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        loadin.start();

        context=AppDetail.this;

        Picasso picasso = Picasso.get();
        boolean is_images_loaded=false;
        try {
            p_App_id=app.getString("id");
            if (Variables.image_array!=null){
                try {
                    if (Variables.image_array.getJSONArray(Integer.parseInt(p_App_id))!=null){
                        is_images_loaded=true;
                    }
                }catch (JSONException e){
                    e.printStackTrace();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //find all views

        init_ratings(context);
        init_views();

        //show loading
        if (!Functions.is_first(p_App_id)){
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmer_layout.setVisibility(View.GONE);
                    }
                },800);
            }catch (Exception e){
                e.printStackTrace();

            }
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==share_btn){
                    String link = Variables.Http+getResources().getString(R.string.appstore_url_domain)+getResources().getString(R.string.appstore_url_domain_pathprefix)+"?id="+p_App_id;
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_TEXT,link);
                    startActivity(i);
                }else{
                    Toast.makeText(context, "This Feature is still in development", Toast.LENGTH_SHORT).show();
                }

            }
        };

        ///////////////////////////////////////////////////////



        ///////////////////////////////////////////////////////
        share_btn.setOnClickListener(listener);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        });

        //fetch description


        try {
            app_name.setText(app.getString("name"));
            String icon;
            if (!app.getString("icon").contains("http")){
                icon=ApiConfig.Base_url+app.getString("icon");
            }else{
                icon=app.getString("icon");
            }

            picasso.load(Uri.parse(icon)).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    try {
                        picasso.load(Uri.parse(icon)).into(app_icon);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

            app_size.setText(app.getString("size") + " MB");
            String package_name = app.getString("package").trim();
            String download_link= app.getString("download_link");
            //Toast.makeText(this, ""+package_name, Toast.LENGTH_SHORT).show();
            boolean installed = isPackageInstalled(package_name, getPackageManager());

            if (installed) {
                IS_installed=true;
                install_btn.setText("OPEN");
                rating_bar_bg.setVisibility(View.VISIBLE);
                if (Functions.is_Login(context)){
                    my_review_layout.setVisibility(View.GONE);
                }
                install_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(package_name);
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        } else {
                            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (package_name.equals("coming soon")) {
                install_btn.setText("COMING SOON");
                Toast.makeText(AppDetail.this, "This app will be available soon", Toast.LENGTH_SHORT).show();
                install_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            } else if (package_name.contains("https://")) {
                install_btn.setText("BROWS");
                install_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent();
                        it.setAction(Intent.ACTION_VIEW);
                        it.setData(Uri.parse(package_name));
                        startActivity(it);
                    }
                });
            } else {
                install_btn.setText("INSTALL");
                install_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            if (!getPackageManager().canRequestPackageInstalls()){
                                //Toast.makeText(context, "request permission", Toast.LENGTH_SHORT).show();

                                Functions.Showdouble_btn_alert(context, "Permission needed for auto install", "click on settings and allow permission for enabling auto install", "cancel", "settings", false, new FragmentCallBack() {
                                    @Override
                                    public void onResponce(Bundle bundle) {
                                        if (bundle.getString("action").equals("ok")){
                                            startActivityForResult(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+BuildConfig.APPLICATION_ID)),UnknowSourcePermission);
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


                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        install_btn.setBackgroundColor(getResources().getColor(R.color.install_btn_bg));
    }


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
        //rating


        //vertical bar
        horizontal_bar_dow=findViewById(R.id.horizontal_bar_dow);
        horizontal_bar_rating=findViewById(R.id.horizontal_bar_rating);
        horizontal_bar_review_count=findViewById(R.id.horizontal_bar_review_count);
        //vertical bar


        rating_big_text=findViewById(R.id.big_rating_text);
        rating_1star=findViewById(R.id.rating_star_fill1);
        rating_2star=findViewById(R.id.rating_star_fill2);
        rating_3star=findViewById(R.id.rating_star_fill3);
        rating_4star=findViewById(R.id.rating_star_fill4);
        rating_5star=findViewById(R.id.rating_star_fill5);
        show_allrating_arrow=findViewById(R.id.show_all_ratings_arrow);
        rating_bar_background_layout=findViewById(R.id.rating_bar_background_layout);
        total_rates_txt=findViewById(R.id.total_rates_txt);
        ratingBar=findViewById(R.id.rating_bar);
        rating_bar_bg=findViewById(R.id.rating_bar_bgbgb);

        my_review_layout=findViewById(R.id.my_review_layout);
        some_reviews=findViewById(R.id.some_reviews);
        first_review_layout=findViewById(R.id.first_review_layout);
        second_review_layout=findViewById(R.id.second_review_layout);
        third_review_layout=findViewById(R.id.third_review_layout);

        myrevstar1=findViewById(R.id.my_review_star1);
        myrevstar2=findViewById(R.id.my_review_star2);
        myrevstar3=findViewById(R.id.my_review_star3);
        myrevstar4=findViewById(R.id.my_review_star4);
        myrevstar5=findViewById(R.id.my_review_star5);
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
                            public void onResponce(Bundle bundle) {
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
                see_all_reviews(p_App_id);

            }
        });

        see_all_rev_txt_bottom=findViewById(R.id.see_all_rev_btn);

        see_all_rev_txt_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                see_all_reviews(p_App_id);
            }
        });

        show_allrating_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                see_all_reviews(p_App_id);
            }
        });


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        horizontal_bar_dow.setText(downloads_count);

    }

    private void check_myreview() {


        if (Functions.is_Login(context)){
            ApiRequests.checkReviewForThisApp(context, p_App_id, new FragmentCallBack() {
                @Override
                public void onResponce(Bundle bundle) {
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

        r_star11=findViewById(R.id.review_star_fill11);
        r_star12=findViewById(R.id.review_star_fill21);
        r_star13=findViewById(R.id.review_star_fill31);
        r_star14=findViewById(R.id.review_star_fill41);
        r_star15=findViewById(R.id.review_star_fill51);

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


    private void save_description(String resp, String appid) {
        if (Variables.description_array==null){
            Variables.description_array=new JSONArray();
        }
        
        JSONObject object = new JSONObject();
        try {
            object.put(appid,resp);
            Variables.description_array.put(Integer.parseInt(appid),object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void do_rest(){
        String packagename = "";
        try {
            packagename = app.getString("package");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            File folder = new File(Variables.AppDownloadpath);
            if(!folder.exists()){
                folder.mkdirs();
            }

            Functions.showDownloadConfirmer(context, app.getString("name"), app.getString("size"), false, new FragmentCallBack() {
                @Override
                public void onResponce(Bundle bundle) {
                    if (bundle.getString("click").equals("download")) {

                        try {
                            String download_link;
                            if(app.getString("download_link").contains("http")){
                                download_link=app.getString("download_link");
                            }else {
                                download_link=ApiConfig.Base_url+app.getString("download_link");
                            }
                            Functions.DownloadWithLoading(context,download_link, Variables.AppDownloadpath, app.getString("name"), pos, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    if (bundle.getString("action").equals("install")) {
                                        String path = bundle.getString("path");

                                        String package_name = null;
                                        String version = null;
                                        try {
                                            package_name = app.getString("package");
                                            version = app.getString("version");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //add value in array
                                        if (Variables.download_information == null) {
                                            Variables.download_information = new JSONArray();
                                        }
                                        JSONObject object = new JSONObject();
                                        try {
                                            object.put("path", path);
                                            object.put("package", package_name);

                                            Variables.download_information.put(pos, object);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        Uri app_uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(path));

                                        //installPackage(apk);
                                        Intent install = new Intent(Intent.ACTION_VIEW);
                                        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                                        install.setData(app_uri);
                                        //install.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                                        startActivity(install);


                                    } else {
                                        //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    //Toast.makeText(context, ""+bundle.getString("click"), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}