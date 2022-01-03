package com.afriappstore.global.ExtraActivities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SplashActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class Email_signUp extends AppCompatActivity {

    EditText email_edit,firstname_edit,lastname_edit,create_pass_edit,confirm_pass_edit;
    ImageView hide_confirm,hide_create;
    CircleImageView profile_picker;
    Button continue_btn;

    String email="",fisrtname,lastname,password,profile_pic="default";

    int hide_switch_create=0,hide_switch_confirm=0;
    Integer PICK_PROFILE_PIC=101;
    Integer ASK_PERMISSION=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        try {
            Intent intent = getIntent();
            email=intent.getStringExtra("email");

        } catch (Exception e) {
            e.printStackTrace();
        }

        init_views();

        setup_screen();
        setup_event_listeners();


    }

    private void setup_event_listeners() {

        View.OnClickListener listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (view==hide_create){

                    if (hide_switch_create==0){
                        hide_switch_create=1;

                        create_pass_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    }else{
                        hide_switch_create=0;
                        create_pass_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }


                }else if (view==hide_confirm){

                    if (hide_switch_confirm==0){
                        hide_switch_confirm=1;
                        confirm_pass_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    }else{
                        hide_switch_confirm=0;
                        confirm_pass_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }


                }else if (view==profile_picker){

                    Toast.makeText(Email_signUp.this, "this function is in development \n you can change profile pic later in profile", Toast.LENGTH_LONG).show();
                    /*
                    if (ActivityCompat.checkSelfPermission(Email_signUp.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        Email_signUp.this.startActivityForResult(i,PICK_PROFILE_PIC);
                    }else{
                        ActivityCompat.requestPermissions(Email_signUp.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ASK_PERMISSION);
                    }
*/
                }else if (view==continue_btn){
                    if (check_inputs()){
                        //
                        Functions.showLoader(Email_signUp.this);
                        //
                        if (!profile_pic.equals("default")){
                            Functions.Image_to_Base64(profile_pic, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    profile_pic=bundle.getString("img");
                                    proceed_api();

                                }
                            });
                        }else{
                            proceed_api();
                        }


                    }

                }

            }
        };

        hide_create.setOnClickListener(listener);
        hide_confirm.setOnClickListener(listener);
        profile_picker.setOnClickListener(listener);
        continue_btn.setOnClickListener(listener);

    }

    private void proceed_api() {
        String email=email_edit.getText().toString().trim();
        String f_name=firstname_edit.getText().toString().trim();
        String l_name=lastname_edit.getText().toString().trim();
        String password=create_pass_edit.getText().toString().trim();

        ApiRequests.createEmailUser(Email_signUp.this, email, f_name, l_name, password, profile_pic, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                Functions.cancelLoader();
                String code=bundle.getString(ApiConfig.Request_code);
                if (code.equals(ApiConfig.RequestSuccess)){
                    Toast.makeText(Email_signUp.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Email_signUp.this, SplashActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

                    Handler handler = new Handler(
                    );
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1500);

                }

            }
        });
    }

    private boolean check_inputs() {
        boolean b = false;

        if (validate_email(email_edit.getText().toString())){
            b=true;
        }else {
            b=false;
            email_edit.setError("invalid email");
        }

        if (firstname_edit.getText().length()>3 && firstname_edit.getText().length()<16){
            b=true;
        }else {
            b=false;
            firstname_edit.setError("firstname limit min 4 to max 15");
        }

        if (lastname_edit.getText().length()>3 && lastname_edit.getText().length()<16){
            b=true;
        }else {
            b=false;
            lastname_edit.setError("lastname limit min 4 to max 15");
        }

        if (create_pass_edit.getText().length()>3 && create_pass_edit.getText().length()<16){
            b=true;
        }else {
            b=false;
            create_pass_edit.setError("password limit min 4 to max 15");
        }

        if (confirm_pass_edit.getText().toString().trim().equals(create_pass_edit.getText().toString().trim())){
            b=true;
        }else {
            b=false;
            confirm_pass_edit.setError("password didn\'t matched");
        }

        return b;
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

    private void setup_screen() {
        //
        if (!email.equals("")){
            email_edit.setText(email);
        }


        InputFilter filter = new InputFilter() {
            String blockCharacterSet="~`|•√π÷×¶∆€¥$¢^°=%©®™✓*':;!?,";

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };

        create_pass_edit.setFilters(new InputFilter[]{filter});

    }

    private void init_views() {
        email_edit=findViewById(R.id.email_edit);
        firstname_edit=findViewById(R.id.firstname_txt);
        lastname_edit=findViewById(R.id.lastname_txt);
        create_pass_edit=findViewById(R.id.create_password_edit);
        confirm_pass_edit=findViewById(R.id.confirm_password_edit);
        hide_confirm=findViewById(R.id.hide_confirm_pass);
        hide_create=findViewById(R.id.hide_create_pass);
        profile_picker=findViewById(R.id.profile_picker);
        continue_btn=findViewById(R.id.sign_up_btn);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_PROFILE_PIC){
            if (resultCode== Activity.RESULT_OK){
                try {
                    Uri image_uri = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(image_uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //
                    String picturePath = cursor.getString(columnIndex);
                    //
                    cursor.close();

                    profile_pic=picturePath;
                    Bitmap image = BitmapFactory.decodeFile(picturePath);
                    profile_picker.setImageBitmap(image);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(Email_signUp.this, "please select a image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(Email_signUp.this, "please select a image", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode==ASK_PERMISSION){
            if (!(resultCode==Activity.RESULT_OK)){
                Toast.makeText(Email_signUp.this, "permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}