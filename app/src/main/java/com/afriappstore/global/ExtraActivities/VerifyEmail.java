package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.ShearedPrefs;

public class VerifyEmail extends AppCompatActivity {

    TextView email_txt,cancel_button,verify_button,header_txt;
    LinearLayout buttons_layout;
    String email="";
    String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        email= Functions.getSharedPreference(this).getString(ShearedPrefs.U_EMAIL,"");
        uid= Functions.getSharedPreference(this).getString(ShearedPrefs.U_ID,"");
        if (!(!email.equals("") && !uid.equals(""))){
            Toast.makeText(this, "email account error", Toast.LENGTH_SHORT).show();
            finish();
        }

        email_txt=findViewById(R.id.email_txt);
        cancel_button=findViewById(R.id.cancelbutton);
        verify_button=findViewById(R.id.verifyemailbutton);
        header_txt=findViewById(R.id.header_txt);
        buttons_layout=findViewById(R.id.button_layout);


        email_txt.setText(email_txt.getText().toString().replace("adrrr",email));

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.showLoader(VerifyEmail.this);
                ApiRequests.verifyEmail(VerifyEmail.this, uid, email, new FragmentCallBack() {
                    @Override
                    public void onResponse(Bundle bundle) {
                        Functions.cancelLoader();
                        if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                            buttons_layout.setVisibility(View.GONE);
                            header_txt.setText(R.string.email_sent_successfully);
                            email_txt.setText(R.string.also_check_spam);

                        }else{
                            Toast.makeText(VerifyEmail.this, "something went wrong please try again later", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


    }
}