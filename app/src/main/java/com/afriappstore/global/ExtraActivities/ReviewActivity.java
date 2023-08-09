package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.Model.UserModel;
import com.airbnb.lottie.LottieAnimationView;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.ShearedPrefs;
import com.afriappstore.global.SimpleClasses.Variables;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ReviewActivity extends AppCompatActivity {

    float rating;
    ImageView back_button,app_image;
    CircleImageView user_image;
    TextView app_name_txt,post_btn,review_limit,username,privacy_text;
    EditText detailed_rev;
    LottieAnimationView user_img_loading;
    String mode="post",review="";

    RatingBar ratingBar;

    String app_id, appName, app_icon;

    Picasso picasso;
    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getSupportActionBar().hide();

        try {
            Intent intent = getIntent();
            mode=intent.getStringExtra("mode");
            rating=intent.getFloatExtra("rating",0);

            if (mode.equals("edit")){
                review=intent.getStringExtra("review");
            }

            app_id = intent.getStringExtra("app_id");
            app_icon = intent.getStringExtra("app_icon");
            appName = intent.getStringExtra("app_name");

            user = Paper.book().read("user",new UserModel());

        }catch (Exception e){
            e.printStackTrace();

        }

        picasso = Picasso.get();

        find_allviews();

        set_event_lisners();

        setup_screen_data();



    }

    private void setup_screen_data() {
        app_name_txt.setText(appName);

        picasso.load(app_icon).fetch(new Callback() {
            @Override
            public void onSuccess() {
                picasso.load(app_icon).into(app_image);
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onError(Exception e) {
                app_image.setImageDrawable(ReviewActivity.this.getResources().getDrawable(R.drawable.ic_baseline_android_24));
            }
        });

        String img = user.profile_pic;

        if (img.equals("default")) {
            user_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));

        }else{
            picasso.setLoggingEnabled(false);

            picasso.load(Uri.parse(img)).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    picasso.load(Uri.parse(img)).into(user_image);
                    user_img_loading.cancelAnimation();
                    user_img_loading.setVisibility(View.GONE);
                    picasso.setLoggingEnabled(true);


                }

                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onError(Exception e) {
                    user_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_icon));
                    user_img_loading.cancelAnimation();
                    user_img_loading.setVisibility(View.GONE);
                    picasso.setLoggingEnabled(true);


                }
            });

        }

        username.setText(user.first_name+" "+user.last_name);
        ratingBar.setRating(rating);

        try{
            if (mode.equals("edit")){
                detailed_rev.setText(review);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        ratingBar.setStepSize(1);

        detailed_rev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                review_limit.setText(text.length()+"/250");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        Link link = new Link(this.getString(R.string.privacy_policy));
        link.setTextColor(getResources().getColor(R.color.gainsbro));
        link.setTextColorOfHighlightedLink(getResources().getColor(R.color.Login_tab_txt_color));
        link.setUnderlined(true);
        link.setBold(false);
        link.setHighlightAlpha(.20f);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                //Toast.makeText(AboutUs_A.this, ""+clickedText, Toast.LENGTH_SHORT).show();
                //openWebUrl(view.getContext().getString(R.string.terms_of_use), ApiConfig.Terms_url);

                Intent it = new Intent();
                it.setAction(Intent.ACTION_VIEW);
                it.setData(Uri.parse(ApiConfig.Privacy_url));
                startActivity(it);
            }
        });

        ArrayList<Link> links = new ArrayList<>();
        links.add(link);
        CharSequence sequence = LinkBuilder.from(this, privacy_text.getText().toString())
                .addLinks(links)
                .build();
        privacy_text.setText(sequence);
        privacy_text.setMovementMethod(TouchableMovementMethod.getInstance());

    }

    private void set_event_lisners() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view== back_button){
                    Functions.Showdouble_btn_alert(ReviewActivity.this, "discard review?", "", "Cancel", "Discard", true, new FragmentCallBack() {
                        @Override
                        public void onResponse(Bundle bundle) {
                            if (bundle.getString("action").equals("ok")){
                                finish_animated();
                            }
                        }
                    });

                }else if (view==post_btn){
                    if (detailed_rev.getText().length()>0){
                        Functions.showLoader(ReviewActivity.this);
                        String uname =Functions.getSharedPreference(ReviewActivity.this).getString(ShearedPrefs.U_FNAME,"")+" "+Functions.getSharedPreference(ReviewActivity.this).getString(ShearedPrefs.U_LNAME,"");


                        ApiRequests.RateApp(ReviewActivity.this, app_id, uname,
                                String.valueOf(ratingBar.getRating()),
                                detailed_rev.getText().toString(),
                                new FragmentCallBack() {
                            @Override
                            public void onResponse(Bundle bundle) {
                                Functions.cancelLoader();

                                String code = bundle.getString(ApiConfig.Request_code);
                                if (code.equals(ApiConfig.RequestSuccess)){
                                    Variables.my_review=detailed_rev.getText().toString();
                                    Toast.makeText(ReviewActivity.this, "Review posted successfully", Toast.LENGTH_SHORT).show();
                                    finish_animated();

                                }else{
                                    Toast.makeText(ReviewActivity.this, "Failed to post", Toast.LENGTH_SHORT).show();

                                }


                            }
                        });

                    }else {
                        detailed_rev.setError("review required");
                    }
                }

            }
        };

        back_button.setOnClickListener(clickListener);
        post_btn.setOnClickListener(clickListener);

    }

    private void find_allviews() {
        back_button=findViewById(R.id.allRev_us_back);
        app_image=findViewById(R.id.app_image);
        app_name_txt=findViewById(R.id.app_name);
        post_btn=findViewById(R.id.post_rev_btn);
        user_image=findViewById(R.id.user_img);
        username=findViewById(R.id.username);
        ratingBar = findViewById(R.id.final_rating_bar);
        detailed_rev=findViewById(R.id.detailed_review_box);
        review_limit=findViewById(R.id.review_limit);
        privacy_text=findViewById(R.id.privacy_text);
        user_img_loading=findViewById(R.id.user_img_loading);

    }

    private void finish_animated(){
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        Functions.Showdouble_btn_alert(ReviewActivity.this, "discard review?", "", "Cancel", "Discard", true, new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                if (bundle.getString("action").equals("ok")){
                    finish_animated();
                }
            }
        });
    }
}