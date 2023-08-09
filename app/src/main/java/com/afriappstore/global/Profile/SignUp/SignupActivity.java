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
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.Model.UserModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SignupActivity extends AppCompatActivity {

    EditText firstname,lastname,email_edit,password_edit;
    Button complete_signup_btn;
    CircleImageView pick_image;
    TextView loginTermsConditionTxt;

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

//        try {
//            Intent intent = getIntent();
//            String type = intent.getStringExtra("type");
//            if (type.equals("phone")){
//                TYPE="phone";
//                PHONE= intent.getStringExtra("phone");
//                Auth_ID=intent.getStringExtra("auth_id");
//            }else {
//                TYPE="email";
//                EMAIL= intent.getStringExtra("email");
//                Auth_ID=intent.getStringExtra("auth_id");
//
//            }
//
//            if (TYPE.equals("NULL") && EMAIL.equals("NULL") && PHONE.equals("NULL") && Auth_ID.equals("NULL")){
//                finish();
//                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
//
//            }
//        }catch (Exception e){
//            finish();
//            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
//        }

        firstname=findViewById(R.id.firstname_txt);
        lastname=findViewById(R.id.lastname_txt);
        complete_signup_btn=findViewById(R.id.sign_up_btn);
        email_edit = findViewById(R.id.email_edit);
        password_edit =findViewById(R.id.password_edit);
        pick_image=findViewById(R.id.profile_picker);
        loginTermsConditionTxt=findViewById(R.id.login_terms_condition_txt);
        SetupScreenData();

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
                String email = email_edit.getText().toString();
                String pass = password_edit.getText().toString();

                if (f_name.length()>3){
                    if (l_name.length()>1){
                        if (email.contains("@")& email.length()>4){
                            if (pass.length()>5){


                                call_signup_api();


                            }else {
                                password_edit.setError("minimum length 6");
                                password_edit.requestFocus();
                            }
                        }else{
                            email_edit.setError("Please enter a email");
                            email_edit.requestFocus();

                        }

                    }else{
                        lastname.setError("Last name is required");
                        lastname.requestFocus();

                    }
                }else{
                    firstname.setError("min length 4");
                    firstname.requestFocus();
                }





            }
        });

        }

    private void SetupScreenData() {

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
        CharSequence sequence = LinkBuilder.from(SignupActivity.this, loginTermsConditionTxt.getText().toString())
                .addLinks(links)
                .build();
        loginTermsConditionTxt.setText(sequence);
        loginTermsConditionTxt.setMovementMethod(TouchableMovementMethod.getInstance());
    }

    private void openWebUrl(String string, String terms_url) {

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(terms_url));
        startActivity(intent);

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
        String email = email_edit.getText().toString();
        String pass = password_edit.getText().toString();
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

        callApiSignup(f_name,l_name,email,pass);


    }


        private void callApiSignup(String f_name,String l_name,String email,String pass) {
            StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Signup, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject resp = new JSONObject(response);
                        if (resp.getString("code").equalsIgnoreCase("200")){

                            JSONObject user = resp.getJSONObject("msg");


                            UserModel model = DataParsing.parseUserModel(user);
                            Paper.book().write("user",model);
                            Paper.book().write("isLogin",true);



                            try {
                                finish();
                                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else {
                            Toast.makeText(SignupActivity.this, ""+resp.getString("msg"), Toast.LENGTH_SHORT).show();
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("firstname", f_name);
                    params.put("lastname", l_name);
                    params.put("email", email);
                    params.put("password", pass);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
            queue.add(request);



        }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Functions.Showdouble_btn_alert(SignupActivity.this, "Do you really want to exit", "Your current login progress will be lost", "Go back", "Exit", true, new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                String action = bundle.getString("action");

                if (action.equals("ok")){
                    finish();
                    overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                }
            }
        });
    }
}