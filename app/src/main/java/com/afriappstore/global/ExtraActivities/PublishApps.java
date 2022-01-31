package com.afriappstore.global.ExtraActivities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Adepters.MyappsAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.BuildConfig;
import com.afriappstore.global.FileUploadServices.UploadFileAsync;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.MainActivity;
import com.afriappstore.global.Model.CategoriesModel;
import com.afriappstore.global.Model.MyappModel;
import com.afriappstore.global.Model.PublishAppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;
import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Permission;
import java.util.ArrayList;

public class PublishApps extends AppCompatActivity {

    //first layout components
    RecyclerView my_apps_list;
    LottieAnimationView no_apps_found_animation;
    CardView my_all_apps_layout,publish_app_form;
    RelativeLayout no_apps_found_layout;
    LinearLayout create_app_btn;
    int switchlauout=0;
    ArrayList<MyappModel> datalist;
    
    int PICK_PROFILE_PIC=101;
    int ASK_PERMISSION=9909,ASK_MANAGE_STORAGE_PERMISSION=6463;
    
    //second layout components
    TextView app_name_wordcounter,app_description_wordcounter,select_category_button,selected_cat_name_txt,next_btn;
    EditText app_name_txt,app_description_txt;
    ImageView app_icon_img,publish_apps_back;
    String app_icon_path;

    CategoriesModel categoriesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_apps);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initviews();
        loadmyapps();
        setuppublishappform();

        create_app_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (!checkPermission()){
                    Functions.Showdouble_btn_alert(PublishApps.this, "Permission alert",
                            "these permissions required for App's functionality",
                            "Cancel", "Continue", true, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    if (bundle.getString("action").equals("ok")){
                                        //startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,Uri.parse("package:"+ BuildConfig.APPLICATION_ID)));
                                        try {
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                            intent.addCategory("android.intent.category.DEFAULT");
                                            intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                                            startActivityForResult(intent, 2296);
                                        } catch (Exception e) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                            startActivityForResult(intent, 2296);
                                        }


                                    }else{
                                        Toast.makeText(PublishApps.this, "Permission required for publishing apps", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }else{
                    showCreateapplayout();
                }



            }
        });


        publish_apps_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

    }

    private boolean checkPermission(){
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(PublishApps.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(PublishApps.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void showCreateapplayout() {
        my_all_apps_layout.setVisibility(View.GONE);
        publish_app_form.setVisibility(View.VISIBLE);
        switchlauout=1;

    }

    private void showmyappslayout() {
        my_all_apps_layout.setVisibility(View.VISIBLE);
        publish_app_form.setVisibility(View.GONE);
        switchlauout=0;

    }

    private void loadmyapps() {

        ApiRequests.getallmyapps(this, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                String code = bundle.getString(ApiConfig.Request_code);
                if (code.equals(ApiConfig.RequestSuccess)){
                    try {
                        datalist=new ArrayList<>();
                        JSONArray array = new JSONArray(bundle.getString(ApiConfig.Request_response));
                        for (int i=0;i<array.length();i++){
                            MyappModel item = new MyappModel();
                            JSONObject app = array.getJSONObject(i);
                            item.app_id=app.getString("id");
                            item.app_name=app.getString("name");
                            item.app_icon=app.getString("icon");
                            item.version=app.getString("version");
                            item.description=app.getString("desc");
                            item.size=app.getString("size");
                            item.downloads=app.getString("downloads");
                            item.download_link=app.getString("download_link");
                            item.tags=app.getString("tags");
                            item.rating=app.getString("rating");
                            item.package_name=app.getString("package");
                            item.owner=app.getString("owner_id");
                            item.status=app.getString("status");

                            datalist.add(item);
                        }

                        MyappsAdapter adapter = new MyappsAdapter(PublishApps.this,datalist);
                        LinearLayoutManager manager = new LinearLayoutManager(PublishApps.this);

                        my_apps_list.setAdapter(adapter);
                        my_apps_list.setLayoutManager(manager);

                    }catch (Exception e){
                        e.printStackTrace();
                        noappsfound();
                    }

                }else{
                    noappsfound();
                }

            }
        });

    }

    private void noappsfound() {
        my_apps_list.setVisibility(View.GONE);
        no_apps_found_layout.setVisibility(View.VISIBLE);
        no_apps_found_animation.setFrame(1);
        no_apps_found_animation.playAnimation();

        no_apps_found_animation.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                no_apps_found_animation.pauseAnimation();
                no_apps_found_animation.setFrame(1);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                
            }
        });

    }

    private void initviews() {
        
        //first layout
        my_apps_list=findViewById(R.id.my_apps_list);
        no_apps_found_animation=findViewById(R.id.no_apps_found_animation);
        my_all_apps_layout=findViewById(R.id.my_all_apps_layout);
        no_apps_found_layout=findViewById(R.id.no_apps_found_layout);
        create_app_btn=findViewById(R.id.create_app_btn);
        publish_app_form=findViewById(R.id.publish_app_form);

        
        
        //second layout
        app_name_txt=findViewById(R.id.app_name_txt);
        app_description_txt=findViewById(R.id.app_description_txt);
        app_name_wordcounter=findViewById(R.id.appname_wordcounter);
        app_description_wordcounter=findViewById(R.id.description_wordcounter);
        select_category_button=findViewById(R.id.select_category_button);
        selected_cat_name_txt=findViewById(R.id.selected_category_name_txt);
        next_btn=findViewById(R.id.next_btn);
        app_icon_img=findViewById(R.id.app_icon_img);
        publish_apps_back=findViewById(R.id.publish_apps_back);
        
    }

    private void finish_animated(){
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

    }

    @Override
    public void onBackPressed() {
        if (switchlauout==0){
            finish_animated();
        }else{
            Functions.Showdouble_btn_alert(this, "Are you sure you want to discard app", "Your current progress will be lost", "cancel", "Discard", true, new FragmentCallBack() {
                @Override
                public void onResponce(Bundle bundle) {

                    if (bundle.getString("action").equals("ok")){

                        showmyappslayout();
                    }
                    
                }
            });

        }
    }

    private void setuppublishappform() {
        //set app name word counter
        
        app_name_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    app_name_wordcounter.setText(charSequence.length()+"/20");
                    
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        
        //setup app description counter
        app_description_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    app_description_wordcounter.setText(charSequence.length()+"/350");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==app_icon_img){
                    if (ActivityCompat.checkSelfPermission(PublishApps.this, READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(i,PICK_PROFILE_PIC);
                    }else{
                        ActivityCompat.requestPermissions(PublishApps.this,new String[]{READ_EXTERNAL_STORAGE},ASK_PERMISSION);
                    }
                    
                }else if(view==select_category_button){
                    //Toast.makeText(PublishApps.this, "in progress", Toast.LENGTH_SHORT).show();
                    Functions.show_select_categories(PublishApps.this, new FragmentCallBack() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            categoriesModel=new CategoriesModel();
                            categoriesModel.id=bundle.getString("id");
                            categoriesModel.name=bundle.getString("name");

                            //update views
                            selected_cat_name_txt.setText(categoriesModel.name);

                        }
                    });

                }else if(view==next_btn){
                    if (Validate_edittexts()){
                        if (categoriesModel!=null && app_icon_path!=null){
                            setupdata();

                            Intent intent = new Intent(PublishApps.this,PublishAppsSecond.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish_animated();
                                }
                            },500);

                        }else if (categoriesModel==null){
                            Toast.makeText(PublishApps.this, "please select a valid category", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PublishApps.this, "please select a valid app icon", Toast.LENGTH_SHORT).show();
                        }

                    }
                    
                }
                
            }
        };
        
        next_btn.setOnClickListener(clickListener);
        app_icon_img.setOnClickListener(clickListener);
        select_category_button.setOnClickListener(clickListener);
        
    }

    private void setupdata() {
        Variables.publishAppModel=null;
        PublishAppModel item = new PublishAppModel();
        item.app_name=app_name_txt.getText().toString().trim();
        item.app_icon=app_icon_path;
        item.app_cat_id=categoriesModel.id;
        item.app_description=app_description_txt.getText().toString().trim();

        Variables.publishAppModel=item;

    }

    private boolean Validate_edittexts() {
        boolean ret=false;

        if (!app_name_txt.getText().toString().isEmpty() && !(app_name_txt.getText().length()<3)){
            if (!(app_description_txt.getText().toString().isEmpty()) && !(app_description_txt.getText().length()<19)){
                ret=true;

            }else if (app_description_txt.getText().toString().isEmpty()){
                app_description_txt.setError("please enter a valid description");
                ret=false;
            }else {
                app_description_txt.setError("minimum description length 20 words");
                ret=false;
            }
        }else if (app_name_txt.getText().toString().isEmpty()){
            app_name_txt.setError("please enter a valid app name");
            ret=false;
        }else{
            app_name_txt.setError("minimum app name length 3 words");
            ret=false;
        }

        return ret;
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

                    app_icon_path=picturePath;

                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(app_icon_path);
                        app_icon_img.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                        Picasso picasso = Picasso.get();
                        picasso.load(picturePath).into(app_icon_img);
                    }



                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PublishApps.this, "please select a image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(PublishApps.this, "please select a image", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode==ASK_PERMISSION){
            if (!(resultCode==Activity.RESULT_OK)){
                Toast.makeText(PublishApps.this, "permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}