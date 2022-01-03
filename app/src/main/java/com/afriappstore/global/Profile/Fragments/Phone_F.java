package com.afriappstore.global.Profile.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Profile.SignUp.SignupActivity;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SplashActivity;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Phone_F extends Fragment {
    Context context;
    View view;
    TextView loginTermsConditionTxt,tvCountryCode;
    CountryCodePicker ccp;
    EditText phoneEdit,otp_box;
    Button btnSendCode;
    String phoneNo;
    CheckBox chBox;

    //firebase
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    RelativeLayout otp_div;

    public Phone_F(Context context) {
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_phone, container, false);
        initViews();
        SetupScreenData();
        mAuth = FirebaseAuth.getInstance();

        setClickListeners(); //do it in last

        tvCountryCode.setText(ccp.getDefaultCountryNameCode() + " " + ccp.getDefaultCountryCodeWithPlus());

        return view;
    }

    private void initViews(){
        phoneEdit=view.findViewById(R.id.phone_edit);
        tvCountryCode=view.findViewById(R.id.country_code);
        loginTermsConditionTxt=view.findViewById(R.id.login_terms_condition_txt);
        btnSendCode=view.findViewById(R.id.btn_send_code);
        chBox=view.findViewById(R.id.chBox);
        otp_div=view.findViewById(R.id.otp_div);
        otp_box=view.findViewById(R.id.otp_box);


        ccp = new CountryCodePicker(view.getContext());
        ccp.setCountryForNameCode(ccp.getDefaultCountryNameCode());
        ccp.registerPhoneNumberTextView(phoneEdit);




        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String txtName = phoneEdit.getText().toString();
                if (loginTermsConditionTxt.getCurrentTextColor()== ContextCompat.getColor(view.getContext(),R.color.redcolor))
                {
                    loginTermsConditionTxt.setError(null);
                    loginTermsConditionTxt.setTextColor(ContextCompat.getColor(view.getContext(),R.color.ultra_light_grey));
                }
                if (txtName.length() > 0) {
                    btnSendCode.setEnabled(true);
                    btnSendCode.setClickable(true);
                } else {
                    btnSendCode.setEnabled(false);
                    btnSendCode.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setClickListeners(){

        View.OnClickListener clickListener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.country_code:
                        opencountry();
                        break;

                    case R.id.btn_send_code: {
                        if (btnSendCode.getText().equals("Check otp")){

                            if (!(otp_box.getText().length()<6)){
                                String otp= otp_box.getText().toString().trim();
                                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                                signInWithPhoneAuthCredential(credential);
                            }else {
                                if (otp_box.getText()!=null){
                                    otp_box.setError("OTP must be 6 characters long");

                                }else{
                                    otp_box.setError("Please enter valid OTP");
                                }

                            }

                        }else{

                        if (checkValidation()) {

                            if (!ccp.isValid()) {
                                phoneEdit.setError("Invalid Phone Number");
                                phoneEdit.setFocusable(true);
                                return;
                            }

                            phoneNo= phoneEdit.getText().toString();
                            if (phoneNo.charAt(0)=='0')
                            {
                                phoneNo=phoneNo.substring(1);
                            }
                            phoneNo=phoneNo.replace("+","");
                            phoneNo=phoneNo.replace(ccp.getSelectedCountryCode(),"");
                            phoneNo=ccp.getSelectedCountryCodeWithPlus()+phoneNo;
                            phoneNo=phoneNo.replace(" ","");
                            phoneNo=phoneNo.replace("(","");
                            phoneNo=phoneNo.replace(")","");
                            phoneNo=phoneNo.replace("-","");

                                if (!(chBox.isChecked()))
                                {
                                    loginTermsConditionTxt.setError(view.getContext().getString(R.string.please_confirm_terms_and_condition));
                                    loginTermsConditionTxt.setTextColor(ContextCompat.getColor(view.getContext(),R.color.redcolor));
                                    return;
                                }



                            callApiOtp();
                        }
                        }
                        break;
                    }
                }
            }
        };

        tvCountryCode.setOnClickListener(clickListener);
        btnSendCode.setOnClickListener(clickListener);

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

        //Toast.makeText(context, title+"     "+url, Toast.LENGTH_SHORT).show();

    }

    // this will open the county picker screen
    @SuppressLint("WrongConstant")
    public void opencountry() {
        final CountryPicker picker = CountryPicker.newInstance(view.getContext().getString(R.string.select_country));
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                ccp.setCountryForNameCode(code);
                tvCountryCode.setText(code + " " + dialCode);
                picker.dismiss();

            }
        });
        picker.show(getChildFragmentManager(), view.getContext().getString(R.string.select_country));
    }

    public boolean checkValidation() {

        final String st_phone = phoneEdit.getText().toString();

        if (TextUtils.isEmpty(st_phone)) {
            Toast.makeText(getActivity(), view.getContext().getString(R.string.please_enter_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void callApiOtp() {
        Functions.showLoader(context);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("verfication", "onVerificationCompleted:" + credential);
                Functions.cancelLoader();
                otp_box.setText(credential.getSmsCode());


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("failed", "onVerificationFailed", e);
                Functions.cancelLoader();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_LONG).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.w("invalid", "onVerificationFailed", e);

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.w("sms", "onVerificationFailed", e);
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("sent", "onCodeSent:" + verificationId);
                Functions.cancelLoader();


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(context, "otp sent", Toast.LENGTH_SHORT).show();

                otp_div.setVisibility(View.VISIBLE);
                btnSendCode.setText("Submit");

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Functions.showLoader(context);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            proceed_phone_API(user);

                        } else {
                            Functions.cancelLoader();
                            int sw=0;
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(context, "Invalid code entered", Toast.LENGTH_SHORT).show();
                                sw++;
                            }

                            if (sw==0){
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void proceed_phone_API(FirebaseUser user) {
        ApiRequests.callApiForPhoneSignIn(context, user.getPhoneNumber(), new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                Functions.cancelLoader();
                String req_code=bundle.getString(ApiConfig.Request_code);
                if (req_code.equals(ApiConfig.RequestSuccess)){
                    try {
                        JSONObject data=new JSONObject(bundle.getString(ApiConfig.Request_response));
                        String action = data.optString(ApiConfig.Request_Phone_respType);

                        if (action.equals(ApiConfig.Request_Phone_respTypeLogin)){
                            JSONObject user = data.getJSONObject(ApiConfig.Request_parse_user);
                            //update data in perf
                            String f_name="";
                            String l_name="";
                            String profile_pic="";
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            try {
                                f_name= user.getString("first_name");
                                l_name= user.optString("last_name");
                                profile_pic= user.getString("pic");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if (!f_name.equals("")){
                                Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT).show();

                                Bundle b = new Bundle();
                                b.putString(ApiConfig.Request_PostF_Name,f_name);
                                b.putString(ApiConfig.Request_PostL_Name,l_name);
                                b.putString("image",profile_pic);
                                Functions.phoneSignUp_updateData(context,firebaseUser,b);
                            }else{
                                Toast.makeText(context, "Login Failed please try again", Toast.LENGTH_SHORT).show();

                            }

                            Intent intent = new Intent(context, SplashActivity.class);
                            startActivity(intent);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        getActivity().finish();
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            },50);



                        }else if (action.equals(ApiConfig.Request_Phone_respTypeSignup)){
                            Toast.makeText(context, "Signup success", Toast.LENGTH_SHORT).show();
                            //update view for getting more data from user
                            Intent signup = new Intent(context, SignupActivity.class);
                            signup.putExtra("type","phone");
                            signup.putExtra("phone",user.getPhoneNumber());
                            signup.putExtra("auth_id",user.getUid());
                            startActivity(signup);
                            getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().finish();

                                }
                            },1000);


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    Functions.ShowToast(context,"Error:- "+bundle.getString(ApiConfig.Request_response));
                }
            }
        });
    }
}