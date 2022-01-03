package com.afriappstore.global.Profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.ShearedPrefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileA extends AppCompatActivity {


    EditText firstname,lastname;
    CircleImageView pfp;
    LottieAnimationView profile_image_loading;
    Button update_prof_btn;
    FloatingActionButton edit_prof;
    TextView click_to_change;
    String f_nam,l_nam;
    String pic_path="";
    ImageView back_button;

    Integer PICK_PROFILE_PIC=101;
    Integer ASK_PERMISSION=200;

    boolean pfp_clickable=false;
    int Switch=0,pic_switch=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        back_button=findViewById(R.id.profile_back_button);
        firstname=findViewById(R.id.firstname_txt);
        lastname=findViewById(R.id.lastname_txt);
        pfp=findViewById(R.id.prof_pic);
        profile_image_loading=findViewById(R.id.profile_image_loading);
        edit_prof=findViewById(R.id.edit_profile_button);
        update_prof_btn=findViewById(R.id.update_profile_button);
        click_to_change=findViewById(R.id.click_to_change);

        //
        firstname.setFocusable(false);
        lastname.setFocusable(false);

        //

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish_animated();
            }
        });

        try {
            f_nam=Functions.getSharedPreference(this).getString(ShearedPrefs.U_FNAME,"");
            l_nam=Functions.getSharedPreference(this).getString(ShearedPrefs.U_LNAME,"");

            firstname.setText(f_nam);
            lastname.setText(l_nam);
            String img1 = Functions.getSharedPreference(this).getString(ShearedPrefs.U_PIC,"");

            Log.wtf("image",img1);

            String img=ApiConfig.Base_url+img1;
            if (!img1.equals("default")){
                Picasso picasso = Picasso.get();
                picasso.setLoggingEnabled(false);
                picasso.load(Uri.parse(img)).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        picasso.load(Uri.parse(img)).into(pfp);
                        profile_image_loading.setVisibility(View.GONE);
                        profile_image_loading.cancelAnimation();
                        picasso.invalidate(Uri.parse(img));
                        Log.wtf("image",img1);

                        picasso.setLoggingEnabled(true);

                    }

                    @Override
                    public void onError(Exception e) {
                        pfp.setImageDrawable(getDrawable(R.drawable.ic_user_icon));
                        profile_image_loading.setVisibility(View.GONE);
                        profile_image_loading.cancelAnimation();
                        Log.wtf("fail   ",img1+" failed");

                        picasso.setLoggingEnabled(true);

                    }
                });
            }else{
                profile_image_loading.setVisibility(View.GONE);
                profile_image_loading.cancelAnimation();
            }

            pfp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pfp_clickable){

                        if (ActivityCompat.checkSelfPermission(ProfileA.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            i.setType("image/*");
                            ProfileA.this.startActivityForResult(i,PICK_PROFILE_PIC);
                        }else{
                            ActivityCompat.requestPermissions(ProfileA.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ASK_PERMISSION);
                        }
                    }
                }
            });

            edit_prof.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    edit_prof.setEnabled(false);
                    lastname.setFocusable(true);
                    firstname.setFocusable(true);
                    update_prof_btn.setVisibility(View.VISIBLE);
                    firstname.setFocusableInTouchMode(true);
                    lastname.setFocusableInTouchMode(true);

                    click_to_change.setVisibility(View.VISIBLE);
                    pfp_clickable=true;
                }
            });

            firstname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                    String new_name=text+l_nam, old_name=f_nam+l_nam;

                    if (!new_name.equals(old_name)){
                        if (Switch==0){
                            Switch=1;
                            update_prof_btn.setEnabled(true);
                        }
                    }else {
                        if (Switch==1){
                            Switch=0;
                            update_prof_btn.setEnabled(false);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            lastname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                    String new_name=text+l_nam, old_name=f_nam+l_nam;

                    if (!new_name.equals(old_name)){
                        if (Switch==0){
                            Switch=1;
                            update_prof_btn.setEnabled(true);
                        }
                    }else {
                        if (Switch==1){
                            Switch=0;
                            update_prof_btn.setEnabled(false);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            update_prof_btn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    String new_name = firstname.getText().toString()+lastname.getText().toString(),old_name=f_nam+l_nam;

                    int type=1;

                        Functions.showLoader(ProfileA.this);

                        if(pic_switch==0){
                            type=1;
                        }else{
                            type=2;
                        }
                        //type 1=firstname lastname update; type=2 full update;

                        //calling api
                        if (type==1){
                            proceed_Api(pic_path,type);
                        }else{
                            int finalType = type;
                            Functions.Image_to_Base64(pic_path, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    proceed_Api(bundle.getString("img"), finalType);
                                }
                            });
                        }


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void change_text() {
        firstname.setText(f_nam);
        lastname.setText(l_nam);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void default_view(){
        edit_prof.setEnabled(true);
        update_prof_btn.setVisibility(View.GONE);
        update_prof_btn.setEnabled(false);

        firstname.setFocusable(false);
        lastname.setFocusable(false);

        firstname.setFocusableInTouchMode(false);
        lastname.setFocusableInTouchMode(false);

        pfp_clickable=false;
        click_to_change.setVisibility(View.GONE);

    }

    public void proceed_Api(String img,int type){
        ApiRequests.update_profile(ProfileA.this, firstname.getText().toString(), lastname.getText().toString(), img, type, new FragmentCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponce(Bundle bundle) {
                Functions.cancelLoader();
                String code=bundle.getString(ApiConfig.Request_code);
                if (code.equals(ApiConfig.RequestSuccess)) {
                    default_view();
                    firstname.setText(bundle.getString(ApiConfig.Request_PostF_Name));
                    lastname.setText(bundle.getString(ApiConfig.Request_PostL_Name));

                    SharedPreferences.Editor editor = Functions.getSharedPreference(ProfileA.this).edit();

                    if (pic_switch!=0){
                        editor.putString(ShearedPrefs.U_PIC,bundle.getString(ApiConfig.Request_PostPic));

                    }

                    editor.putString(ShearedPrefs.U_FNAME,bundle.getString(ApiConfig.Request_PostF_Name));
                    editor.putString(ShearedPrefs.U_LNAME,bundle.getString(ApiConfig.Request_PostL_Name));
                    editor.commit();

                    Toast.makeText(ProfileA.this,"Profile Updated",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(ProfileA.this,"Failed",Toast.LENGTH_SHORT).show();
                    default_view();
                    change_text();
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

                    pic_path=picturePath;
                    pic_switch=1;

                    Bitmap bitmap = BitmapFactory.decodeFile(pic_path);
                    pfp.setImageBitmap(bitmap);

                    update_prof_btn.setEnabled(true);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ProfileA.this, "please select a image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ProfileA.this, "please select a image", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode==ASK_PERMISSION){
            if (!(resultCode==Activity.RESULT_OK)){
                Toast.makeText(ProfileA.this, "permission required", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish_animated();
    }

    private void finish_animated() {
        Picasso picasso = Picasso.get();
        picasso.invalidate(Uri.parse(ApiConfig.Base_url+Functions.getSharedPreference(ProfileA.this).getString(ShearedPrefs.U_PIC,"")));

        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
    }
}
