package com.afriappstore.global.Profile.SignUp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    EditText firstname,lastname;
    Button complete_signup_btn;
    CircleImageView pick_image;

    Integer PICK_PROFILE_PIC=101;
    Integer ASK_PERMISSION=200;
    String profile_pic="default";

    String TYPE="NULL";
    String PHONE="NULL";
    String EMAIL="NULL";
    String Auth_ID="NULL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        try {
            Intent intent = getIntent();
            String type = intent.getStringExtra("type");
            if (type.equals("phone")){
                TYPE="phone";
                PHONE= intent.getStringExtra("phone");
                Auth_ID=intent.getStringExtra("auth_id");
            }else {
                TYPE="email";
                EMAIL= intent.getStringExtra("email");
                Auth_ID=intent.getStringExtra("auth_id");

            }

            if (TYPE.equals("NULL") && EMAIL.equals("NULL") && PHONE.equals("NULL") && Auth_ID.equals("NULL")){
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

            }
        }catch (Exception e){
            finish();
            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
        }

        firstname=findViewById(R.id.firstname_txt);
        lastname=findViewById(R.id.lastname_txt);
        complete_signup_btn=findViewById(R.id.sign_up_btn);
        pick_image=findViewById(R.id.profile_picker);
        pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SignupActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    SignupActivity.this.startActivityForResult(i,PICK_PROFILE_PIC);
                }else{
                    ActivityCompat.requestPermissions(SignupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ASK_PERMISSION);
                }


            }
        });

        firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>1){
                    if (!complete_signup_btn.isEnabled()){
                        complete_signup_btn.setEnabled(true);
                        complete_signup_btn.setClickable(true);
                    }
                }else{
                    if (complete_signup_btn.isEnabled()){
                        complete_signup_btn.setEnabled(false);
                        complete_signup_btn.setClickable(false);
                    }
                }
            }
        });

        complete_signup_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String f_name=firstname.getText().toString();
                String l_name=lastname.getText().toString();
                if (!f_name.equals("") && !l_name.equals("")){
                    if (!(f_name.length()<4)){
                        call_signup_api();
                    }else{
                        firstname.setError("Firstname should be min 4 characters long");
                    }

                }else{
                    if (f_name.equals("")){
                        firstname.setError("First name is required");
                    }else{
                        lastname.setError("Last name is required");
                    }
                }
            }
        });

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
                    pick_image.setImageBitmap(image);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this, "please select a image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(SignupActivity.this, "please select a image", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode==ASK_PERMISSION){
            if (!(resultCode==Activity.RESULT_OK)){
                Toast.makeText(SignupActivity.this, "permission required", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void call_signup_api() {
        Functions.showLoader(SignupActivity.this);
        String f_name= firstname.getText().toString();
        String l_name=lastname.getText().toString();
        String image=profile_pic;

        if (profile_pic.equals("default")){
            image=profile_pic;
        }else{
            try {
                ByteArrayOutputStream baos =new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(profile_pic);
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
                byte[] image_bytes=baos.toByteArray();
                image= Base64.getEncoder().encodeToString(image_bytes);

            }catch (Exception e){
                e.printStackTrace();
                image=profile_pic;
            }


        }

        ApiRequests.createPhoneUser(SignupActivity.this, PHONE, f_name,l_name,Auth_ID, image, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                Functions.cancelLoader();
                String code = bundle.getString(ApiConfig.Request_code);
                if (code.equals(ApiConfig.RequestSuccess)){
                    //success
                    Bundle b = new Bundle();
                    b.putString(ApiConfig.Request_PostF_Name,f_name);
                    b.putString(ApiConfig.Request_PostL_Name,l_name);
                    b.putString(ApiConfig.AUTH_ID,Auth_ID);
                    b.putString("image",bundle.getString(ApiConfig.Request_response));
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Functions.phoneSignUp_updateData(SignupActivity.this,user,b);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                        }
                    },20);
                    Toast.makeText(SignupActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(SignupActivity.this, ""+bundle.getString(ApiConfig.Request_response), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Functions.Showdouble_btn_alert(SignupActivity.this, "Do you really want to exit", "Your current login progress will be lost", "Go back", "Exit", true, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                String action = bundle.getString("action");

                if (action.equals("ok")){
                    finish();
                    overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                }
            }
        });
    }
}