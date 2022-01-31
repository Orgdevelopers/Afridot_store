package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.Model.MyappModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class EditMyApp extends AppCompatActivity {
    MyappModel App;
    ImageView editmyapp_back,app_icon_img,app_status_info_button;
    TextView app_name,app_status;
    View.OnClickListener onclick;
    Picasso picasso;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_app);
        try {
            getSupportActionBar().hide();
            App = (MyappModel) getIntent().getSerializableExtra("app");
            
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "can't edit this app", Toast.LENGTH_SHORT).show();
            finish_animated();
        }


        //initialize all the views
        init_views();
        set_event_listeners();
        setupData();


    }

    private void setupData() {
        //fill the data in views

        //set app name
        app_name.setText(App.app_name);

        //load app icon
        picasso.load(App.app_icon).fetch(new Callback() {
            @Override
            public void onSuccess() {
                picasso.load(App.app_icon).into(app_icon_img);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

        //set status
        app_status.setText(Functions.convertStatus(App.status));

    }

    private void set_event_listeners() {

        onclick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==editmyapp_back){
                    finish_animated();

                    }
                else if (view==app_status_info_button){
                    //show info about status

                    }

            }
        };

    }

    private void init_views() {

        picasso = Picasso.get();
        app_icon_img=findViewById(R.id.app_icon_img);
        app_status=findViewById(R.id.app_status_txt);
        app_name=findViewById(R.id.app_name_txt);
        app_status_info_button=findViewById(R.id.app_status_info_btn);
        editmyapp_back=findViewById(R.id.editmyapp_back);
    }

    private void finish_animated() {
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
    }
}