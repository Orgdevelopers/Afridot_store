package com.orgdevelopers.hamzs.Profile.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.orgdevelopers.hamzs.ApiClasses.ApiConfig;
import com.orgdevelopers.hamzs.ApiClasses.ApiRequests;
import com.orgdevelopers.hamzs.ExtraActivities.Email_signUp;
import com.orgdevelopers.hamzs.Interfaces.FragmentCallBack;
import com.orgdevelopers.hamzs.R;
import com.orgdevelopers.hamzs.SimpleClasses.Functions;
import com.orgdevelopers.hamzs.SplashActivity;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

public class Email_F extends Fragment {
    Context context;
    View view;
    TextView loginTermsConditionTxt;
    EditText email_edit,password_edit;
    Button next_button;
    CheckBox privacy_check_box;
    TextView forgot_pass_btn;

    RelativeLayout tabPassword;

    int email_checked_switch=0;


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
        setLisners();

        return view;
    }

    private void setLisners() {

        //next button
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (privacy_check_box.isChecked()){
                    if (email_checked_switch==0){
                        //
                        Functions.showLoader(context);
                        //


                        loginTermsConditionTxt.setTextColor(Email_F.this.getResources().getColor(R.color.ultra_light_grey));
                        if (!TextUtils.isEmpty(email_edit.getText())){
                            ApiRequests.CheckEmailFromServer(context, email_edit.getText().toString().trim(), new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    Functions.cancelLoader();
                                    String code = bundle.getString(ApiConfig.Request_code);
                                    if (code.equals(ApiConfig.RequestSuccess)){
                                        String type = bundle.getString("type");
                                        if (type.equals("login")){

                                            email_checked_switch=1;
                                            update_password_view();

                                        }else if (type.equals("signup")){
                                            Functions.Showdouble_btn_alert(context, "No account found on this email", "create one?", "Cancel", "Create Account", false, new FragmentCallBack() {
                                                @Override
                                                public void onResponce(Bundle bundle) {
                                                    if (bundle.getString("action").equals("ok")){
                                                        Intent signup = new Intent(getActivity(), Email_signUp.class);
                                                        signup.putExtra("email",email_edit.getText().toString());

                                                        startActivity(signup);
                                                        try {
                                                            getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.fade_out);
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    getActivity().finish();
                                                                }
                                                            },100);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }



                                                    }

                                                }
                                            });

                                        }


                                    }else{
                                        Toast.makeText(context, "something went wrong please try again later", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }else{
                            email_edit.setError("email required");
                        }

                    }else if (email_checked_switch==1){
                        if (password_edit.getText().length()>=4){
                            //
                            Functions.showLoader(context);
                            //

                            String email=email_edit.getText().toString().trim();
                            String pass = password_edit.getText().toString().trim();
                            ApiRequests.EmailLogin(context, email, pass, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    Functions.cancelLoader();
                                    String code = bundle.getString(ApiConfig.Request_code);
                                    if (code.equals(ApiConfig.RequestSuccess)){
                                        //login success
                                        Toast.makeText(context, "logged in success fully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getActivity(),SplashActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                        try {
                                            getActivity().overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                                            Handler handler = new Handler(
                                            );
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getActivity().finish();
                                                }
                                            },1500);
                                        }catch (Exception E){
                                            E.printStackTrace();
                                        }



                                    }else if (code.equals(ApiConfig.RequestError)){
                                        //error password
                                        password_edit.setError("Wrong password entered");
                                        forgot_pass_btn.setVisibility(View.VISIBLE);

                                    }else if (code.equals("101")){
                                        Toast.makeText(context, "server error 101", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            password_edit.setError("password should be 4 characters long");
                        }
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
                if (email_checked_switch==0){
                    if (validate_email(text.toString())) {
                        next_button.setEnabled(true);

                    }else{
                        next_button.setEnabled(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

    private Boolean validate_email(String text){
        boolean b=false;
        if (text.length()>0){
            if (text.contains("@")){
                b= true;
            }
        }

        return b;
    }


}