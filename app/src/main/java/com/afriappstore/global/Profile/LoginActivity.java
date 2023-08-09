package com.afriappstore.global.Profile;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.MainActivity;
import com.afriappstore.global.Model.UserModel;
import com.afriappstore.global.Profile.Fragments.SignUp_F;
import com.afriappstore.global.Profile.SignUp.SignupActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Profile.Fragments.Email_F;
import com.afriappstore.global.Profile.Fragments.Phone_F;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    //views
    TextView signup_btn, terms_conditions_txt, forget_pass_btn, resend_txt;
    RelativeLayout  forget_pass_otp_edit_layout, forget_pass_new_password_edit_layout, forget_pass_layout, login_layout;
    Button login_btn, next_btn;
    EditText email_edit, password_edit, forget_pass_email_edit, forget_pass_otp_edit, forget_pass_new_password_edit;
    ImageView ivHide;
    CheckBox privacy_check_box;

    //variables
    boolean is_pass_hide = true;
    String otp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
        setupPrivacyLinks();
    }

    private void setupListeners() {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate_inputs()){
                    callAPiLogin();
                }
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();

                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });

        ivHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_pass_hide){
                    is_pass_hide = false;
                    password_edit.setTransformationMethod(null);

                }else{
                    is_pass_hide = true;
                    password_edit.setTransformationMethod(new PasswordTransformationMethod());
                }

            }
        });

        privacy_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean is_check) {
                if (is_check){
                    terms_conditions_txt.setTextColor(getResources().getColor(R.color.ultra_light_grey));
                }else{
                    terms_conditions_txt.setTextColor(getResources().getColor(R.color.redcolor));
                }
            }
        });

        forget_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forget_pass_layout.setVisibility(View.VISIBLE);
                login_layout.setVisibility(View.GONE);

                forget_pass_email_edit.setText("");
                forget_pass_email_edit.setVisibility(View.VISIBLE);
                forget_pass_otp_edit_layout.setVisibility(View.GONE);
                forget_pass_new_password_edit_layout.setVisibility(View.GONE);



            }
        });


        ////////////////////// forget password //////////////////

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forget_pass_otp_edit_layout.getVisibility() == View.VISIBLE){
                    //otp sent next button for verify otp

                    if (forget_pass_otp_edit.getText().length() != 6){
                        forget_pass_otp_edit.setError("please enter valid Verification code");
                        forget_pass_otp_edit.requestFocus();

                    }
                    Functions.showLoader(LoginActivity.this);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Functions.cancelLoader();

                            if (forget_pass_otp_edit.getText().toString().trim().equalsIgnoreCase(otp)){

                                forget_pass_otp_edit_layout.setVisibility(View.GONE);
                                forget_pass_new_password_edit_layout.setVisibility(View.VISIBLE);

                            }else{
                                forget_pass_otp_edit.setError("incorrect Verification code");
                                forget_pass_otp_edit.requestFocus();
                            }

                        }
                    },500);

                }else if (forget_pass_new_password_edit_layout.getVisibility() == View.VISIBLE){
                    //otp verified new password validation
                    if (forget_pass_new_password_edit.getText().toString().trim().length()>5){
                        callApiUpdatePassword();

                    }else{
                        forget_pass_new_password_edit.setError("min length 6");
                        forget_pass_new_password_edit.requestFocus();
                    }

                }else{
                    //send otp
                    if (forget_pass_email_edit.getText().length()> 4 && forget_pass_email_edit.getText().toString().contains("@")){
                        callApiForgetPass();

                    }else{
                        forget_pass_email_edit.setError("please enter valid email");
                        forget_pass_email_edit.requestFocus();

                    }
                }

            }
        });


        resend_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resend_txt.getText().toString().trim().equalsIgnoreCase("Resend")){
                    callApiForgetPass();
                }
            }
        });


    }

    private void callApiUpdatePassword() {
        String newPass = forget_pass_new_password_edit.getText().toString().trim();
        Functions.showLoader(this);

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.updatePassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Functions.cancelLoader();
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equalsIgnoreCase("200")){
                        Toast.makeText(LoginActivity.this, "Password updated please log in", Toast.LENGTH_SHORT).show();

                        login_layout.setVisibility(View.VISIBLE);
                        forget_pass_layout.setVisibility(View.GONE);

                    }else{
                        Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Functions.cancelLoader();
                Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email",forget_pass_email_edit.getText().toString());
                params.put("password",newPass);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void callApiForgetPass() {
        Functions.showLoader(this);

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.forgetPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Functions.cancelLoader();
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equalsIgnoreCase("200")){
                        otp = resp.getString("msg");

                        forget_pass_otp_edit_layout.setVisibility(View.VISIBLE);
                        forget_pass_email_edit.setVisibility(View.GONE);

                        startTimer();

                    }else{
                        Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Functions.cancelLoader();
                Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email",forget_pass_email_edit.getText().toString());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    int time = 120;

    private void startTimer() {
        CountDownTimer timer = new CountDownTimer(120000,1000) {
            @Override
            public void onTick(long remaining_time) {
                if (time!=0){
                    time = time-1;
                }
                if (time>60){
                    time = time-60;
                    if (time>=10){
                        resend_txt.setText("01:"+time);
                    }else{
                        resend_txt.setText("01:0"+time);
                    }

                }else{
                    if (time>=10){
                        resend_txt.setText("00:"+time);
                    }else{
                        resend_txt.setText("00:0"+time);
                    }

                }
            }

            @Override
            public void onFinish() {
                time = 120;
                resend_txt.setText("Resend");

            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (forget_pass_layout.getVisibility() == View.VISIBLE){
            forget_pass_layout.setVisibility(View.GONE);
            login_layout.setVisibility(View.VISIBLE);

        }else{
            super.onBackPressed();
        }
    }

    private void callAPiLogin() {
        Functions.showLoader(this);

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("response",response);
                Functions.cancelLoader();
                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equals("200")){

                        JSONObject user = resp.getJSONObject("msg");
                        UserModel userModel = DataParsing.parseUserModel(user);

                        Paper.book().write("user",userModel);
                        Paper.book().write("isLogin",true);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);



                    }else {
                        Toast.makeText(LoginActivity.this, ""+resp.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("error",error.getMessage());
                Functions.cancelLoader();
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("email",email_edit.getText().toString());
                param.put("password",password_edit.getText().toString());
                return param;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }



    private boolean validate_inputs() {
        if (email_edit.getText().length()>4 && email_edit.getText().toString().contains("@")){
            if (password_edit.getText().length()>5){
                if (privacy_check_box.isChecked()){

                    return true;
                }else{
                    terms_conditions_txt.setTextColor(getResources().getColor(R.color.redcolor));

                }

            }else{
                password_edit.setError("min length 6");
                password_edit.requestFocus();
            }
        }else{
            email_edit.setError("please enter a valid email");
            email_edit.requestFocus();
        }

        return false;

    }


    private void setupPrivacyLinks() {

        Link link = new Link(getString(R.string.terms_of_use));
        link.setTextColor(getResources().getColor(R.color.black));
        link.setTextColorOfHighlightedLink(getResources().getColor(R.color.Login_tab_txt_color));
        link.setUnderlined(true);
        link.setBold(false);
        link.setHighlightAlpha(.20f);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(getString(R.string.terms_of_use), ApiConfig.Terms_url);
            }
        });

        Link link2 = new Link(getString(R.string.privacy_policy));
        link2.setTextColor(getResources().getColor(R.color.black));
        link2.setTextColorOfHighlightedLink(getResources().getColor(R.color.Login_tab_txt_color));
        link2.setUnderlined(true);
        link2.setBold(false);
        link2.setHighlightAlpha(.20f);
        link2.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(getString(R.string.privacy_policy),ApiConfig.Privacy_url);
            }
        });
        ArrayList<Link> links = new ArrayList<>();
        links.add(link);
        links.add(link2);
        CharSequence sequence = LinkBuilder.from(LoginActivity.this, terms_conditions_txt.getText().toString())
                .addLinks(links)
                .build();
        terms_conditions_txt.setText(sequence);
        terms_conditions_txt.setMovementMethod(TouchableMovementMethod.getInstance());

    }

    private void openWebUrl(String title, String link){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);

    }

    private void initViews() {
        signup_btn = findViewById(R.id.signup_btn);
        terms_conditions_txt = findViewById(R.id.login_terms_condition_txt);
        login_btn = findViewById(R.id.login_btn);
        email_edit = findViewById(R.id.email_edit);
        password_edit = findViewById(R.id.password_edit);
        ivHide = findViewById(R.id.iv_hide);
        privacy_check_box = findViewById(R.id.privacy_check_box);

        forget_pass_email_edit = findViewById(R.id.forget_pass_email_edit);
        forget_pass_otp_edit = findViewById(R.id.forget_pass_otp_edit);
        forget_pass_new_password_edit = findViewById(R.id.forget_pass_password_edit);
        forget_pass_otp_edit_layout = findViewById(R.id.forget_pass_otp_edit_layout);
        forget_pass_new_password_edit_layout = findViewById(R.id.forget_pass_password_edit_layout);
        resend_txt = findViewById(R.id.resend_otp_txt);
        forget_pass_layout = findViewById(R.id.forget_pass_layout);
        login_layout = findViewById(R.id.login_layout);
        forget_pass_btn = findViewById(R.id.forget_pass_btn);
        next_btn = findViewById(R.id.next_btn);


        //config
        password_edit.setTransformationMethod(new PasswordTransformationMethod());

    }


}