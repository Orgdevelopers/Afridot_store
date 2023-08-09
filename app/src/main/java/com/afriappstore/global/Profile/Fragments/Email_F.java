package com.afriappstore.global.Profile.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.MainActivity;
import com.afriappstore.global.Model.UserModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Email_F extends Fragment {
    Context context;
    View view;
    TextView loginTermsConditionTxt;
    EditText email_edit,password_edit,otp_edit;
    Button next_button,verify_otp_button;
    CheckBox privacy_check_box;
    TextView forgot_pass_btn,resend_button,enter_otp,create_password;
    int sec=60;
    CountDownTimer timer;

    RelativeLayout tabPassword,login_layout;
    LinearLayout verify_otp_layout;

    int email_checked_switch=0;
    String email_otp;


    public Email_F(Context context) {
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_email, container, false);
        initViews();
        SetupScreenData();
        setListeners();



        return view;
    }

    private void setListeners() {

        //next button
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (privacy_check_box.isChecked()){
                    if (email_edit.length()>5){
                        callAPiLogin();

                    }else{
                        Toast.makeText(context,"please enter valid email",Toast.LENGTH_SHORT).show();
                    }


                }else{
                    loginTermsConditionTxt.setTextColor(Email_F.this.getResources().getColor(R.color.redcolor));
                    privacy_check_box.requestFocus();
                }

            }
        });



        //email box
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                validate_email_pass();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        password_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {


                validate_email_pass();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        forgot_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loop
                setupotplayout();
                /*
                forgot_pass_btn.setClickable(false);
                forgot_pass_btn.setTextColor(context.getResources().getColor(R.color.light_white));
                timer = new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long l) {
                        forgot_pass_btn.setText("resend in: "+sec);
                        sec=sec-1;

                    }

                    @Override
                    public void onFinish() {
                        timer.cancel();
                        forgot_pass_btn.setText("resend otp");
                        forgot_pass_btn.setTextColor(context.getResources().getColor(R.color.light_black));
                        forgot_pass_btn.setClickable(true);

                    }
                };
                timer.start();
                */
            }
        });

    }

    private void callAPiLogin() {
        Functions.showLoader(context);
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.Login, new Response.Listener<String>() {
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

                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);

                        try {
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else {
                        Toast.makeText(context, ""+resp.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("error",error.getMessage());

               Functions.cancelLoader();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
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

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    private void setupotplayout() {
        if(email_edit.length()>5 && email_edit.getText().toString().contains("@")){
            login_layout.setVisibility(View.GONE);
            verify_otp_layout.setVisibility(View.VISIBLE);
            callApiForgetpass();

        }else {
            Toast.makeText(context, "Please enter valid email first", Toast.LENGTH_SHORT).show();
        }

    }

    private void callApiForgetpass() {


    }

    private void setupcreatepassview() {

        enter_otp.setText("Enter new password");
        create_password.setText("create password minimum length (6)");
        otp_edit.setHint("enter new password");
        verify_otp_button.setText("create password");
        otp_edit.setMaxEms(18);
        otp_edit.setInputType(InputType.TYPE_CLASS_TEXT);
        otp_edit.setText("");

    }

    private void setup_resend_view() {


        resend_button.setVisibility(View.VISIBLE);
        resend_button.setClickable(false);
        resend_button.setTextColor(context.getResources().getColor(R.color.ultra_light_grey));
        sec=60;
        timer=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                resend_button.setText("resend otp in: "+sec);
                sec=sec-1;

            }

            @Override
            public void onFinish() {
                timer.cancel();
                resend_button.setClickable(true);
                resend_button.setTextColor(context.getResources().getColor(R.color.light_black));
                resend_button.setText("resend otp");
            }
        };

        timer.start();
    }

    private void update_password_view() {
        tabPassword.setVisibility(View.VISIBLE);
        next_button.setEnabled(false);
        next_button.setText("Log In");

        password_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (text.length()>0){
                    next_button.setEnabled(true);


                }else{
                    next_button.setEnabled(false);
                    //password_edit.setError("invalid password");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void initViews(){
        loginTermsConditionTxt=view.findViewById(R.id.login_terms_condition_txt);
        next_button = view.findViewById(R.id.btn_next);
        email_edit=view.findViewById(R.id.email_edit);
        password_edit=view.findViewById(R.id.password_edit);
        privacy_check_box=view.findViewById(R.id.privacy_check_box);
        forgot_pass_btn=view.findViewById(R.id.forgot_pass_btn);
        tabPassword=view.findViewById(R.id.tabPassword);
        verify_otp_layout=view.findViewById(R.id.verify_otp_layout);
        login_layout=view.findViewById(R.id.login_layout);
        resend_button=view.findViewById(R.id.resend_otp_txt);
        otp_edit=view.findViewById(R.id.otp_edit);
        verify_otp_button=view.findViewById(R.id.verify_otp_button);
        enter_otp=view.findViewById(R.id.enter_otp);
        create_password=view.findViewById(R.id.create_password);

    }


    private void SetupScreenData() {

        Link link = new Link(view.getContext().getString(R.string.terms_of_use));
        link.setTextColor(getResources().getColor(R.color.black));
        link.setTextColorOfHighlightedLink(getResources().getColor(R.color.Login_tab_txt_color));
        link.setUnderlined(true);
        link.setBold(false);
        link.setHighlightAlpha(.20f);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(view.getContext().getString(R.string.terms_of_use), ApiConfig.Terms_url);
            }
        });

        Link link2 = new Link(view.getContext().getString(R.string.privacy_policy));
        link2.setTextColor(getResources().getColor(R.color.black));
        link2.setTextColorOfHighlightedLink(getResources().getColor(R.color.Login_tab_txt_color));
        link2.setUnderlined(true);
        link2.setBold(false);
        link2.setHighlightAlpha(.20f);
        link2.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(view.getContext().getString(R.string.privacy_policy),ApiConfig.Privacy_url);
            }
        });
        ArrayList<Link> links = new ArrayList<>();
        links.add(link);
        links.add(link2);
        CharSequence sequence = LinkBuilder.from(view.getContext(), loginTermsConditionTxt.getText().toString())
                .addLinks(links)
                .build();
        loginTermsConditionTxt.setText(sequence);
        loginTermsConditionTxt.setMovementMethod(TouchableMovementMethod.getInstance());
    }



    public void openWebUrl(String title, String url) {

        Toast.makeText(context,"this function is still in development", Toast.LENGTH_SHORT).show();

    }

    private void validate_email_pass(){
        String email = email_edit.getText().toString();
        String pass = password_edit.getText().toString();
        if (email.length()>5){
            if (email.contains("@")){
                if (pass.length()>5){
                    next_button.setEnabled(true);
                    next_button.setFocusable(true);
                    return;
                }
            }
        }
        next_button.setEnabled(false);
        next_button.setFocusable(false);



    }


}