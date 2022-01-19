package com.afriappstore.global.FileUploadServices;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.AdvancedInfoModel;
import com.afriappstore.global.Model.PublishAppModel;
import com.afriappstore.global.Model.SelectedImageModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FileUploadA extends AppCompatActivity {

    TextView uploading_item_txt,uploading_progress_txt;
    ProgressBar loading_bar;
    LinearLayout rollout_button,retry_btn;

    JSONArray image_array;
    JSONObject app_obj;

    PublishAppModel app_basic_details;
    AdvancedInfoModel app_advanced_details;
    ArrayList<SelectedImageModel> apps_images_list;
    ArrayList<SelectedImageModel> copied_images_list;
    int uploaded_count=0;
    int failed_type=0;
    int failed_image_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        retry_btn=findViewById(R.id.retry_btn);
        uploading_item_txt=findViewById(R.id.uploading_item_txt);
        uploading_progress_txt=findViewById(R.id.uploading_progress_txt);
        loading_bar=findViewById(R.id.uploading_progress_bar);
        rollout_button=findViewById(R.id.rollout_app_button);

        try {
            app_basic_details= Variables.publishAppModel;
            apps_images_list=Variables.selectedappimagelist;
            app_advanced_details=Variables.advancedInfoModel;

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "something is missing please fill all information again", Toast.LENGTH_SHORT).show();
            finish_animated();
        }

        uploading_item_txt.setText("uploading apk file");
        uploading_progress_txt.setVisibility(View.GONE);
        loading_bar.setVisibility(View.VISIBLE);

        //Functions.showLoader(this);
        AsyncTask<String,Void,String> uploading_apk = new UploadFileAsync(app_advanced_details.apk_path,ApiConfig.APPUPLOADURI, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                uploading_item_txt.setText("apk uploaded successfully");
                                loading_bar.setVisibility(View.GONE);
                                uploading_item_txt.setTextColor(FileUploadA.this.getResources().getColor(R.color.green));


                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        upload_app_images();
                                    }
                                },700);

                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();

                            }
                        }
                    });
                    //

                    //Functions.cancelLoader();

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploading_item_txt.setText("failed to upload apk");
                            loading_bar.setVisibility(View.GONE);
                            uploading_item_txt.setTextColor(FileUploadA.this.getResources().getColor(R.color.warningred));
                            retry_btn.setVisibility(View.VISIBLE);
                            failed_type=1;

                        }
                    });

                }

            }
        });

        uploading_apk.execute();
        //upload_app_images();

        rollout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rollout_button.setClickable(false);
                rollout_button.setBackgroundColor(getResources().getColor(R.color.ultra_light_grey));
                Functions.showLoader(FileUploadA.this);
                try {
                    makearrays();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            JSONObject data = null;
                            try {
                                data = new JSONObject();
                                data.put("appdetails",app_obj);
                                data.put("imgs",image_array);
                                //////////////////////////////////////////////////////////////////////////////////////
                                ApiRequests.Rollout_my_app(FileUploadA.this, data, new FragmentCallBack() {
                                    @Override
                                    public void onResponce(Bundle bundle) {
                                        Functions.cancelLoader();
                                        if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                                            Toast.makeText(FileUploadA.this, "app sent for verification", Toast.LENGTH_SHORT).show();
                                            close_page();

                                        }else{
                                            Toast.makeText(FileUploadA.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    },10);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(failed_type==1){
                    //app fail
                    upload_app_again();

                }else if (failed_type==2){
                    //images failed
                    upload_app_images();

                }else if(failed_type==3){
                    //icon failed
                    upload_app_icon();

                }

            }
        });

    }

    private void upload_app_again() {
        //this method will try to upload apk again
        AsyncTask<String,Void,String> upload_apk_again=new UploadFileAsync(app_advanced_details.apk_path, ApiConfig.APPUPLOADURI, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                            try {
                                uploading_item_txt.setText("apk uploaded successfully");
                                loading_bar.setVisibility(View.GONE);
                                uploading_item_txt.setTextColor(FileUploadA.this.getResources().getColor(R.color.green));

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        upload_app_images();
                                    }
                                },700);

                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();

                            }
                        }else{
                            uploading_item_txt.setText("failed to upload apk");
                            loading_bar.setVisibility(View.GONE);
                            uploading_item_txt.setTextColor(FileUploadA.this.getResources().getColor(R.color.warningred));
                            retry_btn.setVisibility(View.VISIBLE);
                            failed_type=1;
                        }
                    }
                });


            }
        });

        upload_apk_again.execute();

    }

    int sec=3;
    CountDownTimer timer;
    private void close_page() {
        timer= new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long l) {
                uploading_item_txt.setText("this page will close in "+sec);
                sec=sec-1;
            }

            @Override
            public void onFinish() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                    }
                },10);
                timer.cancel();
            }
        };
        timer.start();
    }

    private void makearrays() {
        //Toast.makeText(this, ""+copied_images_list.size(), Toast.LENGTH_SHORT).show();
        image_array=new JSONArray();
        for (int i=0;i<copied_images_list.size();i++){
            try {
                JSONObject img_obj = new JSONObject();
                img_obj.put("img",copied_images_list.get(i).path);
                image_array.put(i,img_obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        app_obj=new JSONObject();
        try {
            File apkfile = new File(app_advanced_details.apk_path),iconfile = new File(app_basic_details.app_icon);

            app_obj.put("name",app_basic_details.app_name);
            app_obj.put("icon",iconfile.getName());
            app_obj.put("version",app_advanced_details.version_name);
            app_obj.put("link",apkfile.getName());
            app_obj.put("package",app_advanced_details.package_name);
            app_obj.put("cat",app_basic_details.app_cat_id);
            app_obj.put("desc",app_basic_details.app_description);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void upload_app_images() {
        try {
            uploading_item_txt.setText("uploading images");
            uploading_item_txt.setTextColor(getResources().getColor(R.color.dark_grey));
            uploading_progress_txt.setVisibility(View.VISIBLE);
            uploading_progress_txt.setText("uploaded 0/"+apps_images_list.size());
            loading_bar.setVisibility(View.VISIBLE);
            copied_images_list=new ArrayList<>();
            begin_loop();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void begin_loop() {

        try {
            Log.wtf("process","process reached at 106  "+uploaded_count);
            if (uploaded_count<apps_images_list.size()){
                String pic_name,random_name;
                if(apps_images_list.get(uploaded_count).path.contains("png")){
                    random_name=random_name()+".png";
                }else{
                    random_name=random_name()+".jpg";
                }

                SelectedImageModel item  = new SelectedImageModel();
                item.path=random_name;
                copied_images_list.add(item);
                pic_name=Variables.Appcopypath+random_name;

                Functions.CopyFile(apps_images_list.get(uploaded_count).path,pic_name);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AsyncTask<String,Void,String> upload_image = new UploadFileAsync(pic_name, ApiConfig.APPIMAGEUPLOADURI, new FragmentCallBack() {
                            @Override
                            public void onResponce(Bundle bundle) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                                            uploaded_count++;
                                            uploading_progress_txt.setText("uploaded "+uploaded_count+"/"+apps_images_list.size());
                                            begin_loop();

                                        }else {
                                            uploading_item_txt.setText("uploading failed");
                                            uploading_item_txt.setTextColor(getResources().getColor(R.color.warningred));
                                            retry_btn.setVisibility(View.VISIBLE);
                                            failed_type=2;
                                            failed_image_position=uploaded_count;

                                        }
                                    }
                                });


                            }
                        });

                        upload_image.execute();

                    }
                },10);

            }else{
                //all images are uploaded
                uploading_item_txt.setText("all images are uploaded");
                uploading_progress_txt.setVisibility(View.GONE);
                uploading_item_txt.setTextColor(getResources().getColor(R.color.green));
                loading_bar.setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        upload_app_icon();

                    }
                },1000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void upload_app_icon() {
        uploading_item_txt.setText("uploading app icon");
        uploading_item_txt.setTextColor(getResources().getColor(R.color.dark_grey));
        loading_bar.setVisibility(View.VISIBLE);

        String copy_name,copy_file;

        if(app_basic_details.app_icon.contains("png")){
            copy_name=random_name()+"icon512"+".png";
        }else{
            copy_name=random_name()+"icon512"+".jpg";
        }

        copy_file=Variables.Appcopypath+copy_name;

        if (Functions.CopyFile(app_basic_details.app_icon,copy_file)){
         app_basic_details.app_icon=copy_file;
        }

        AsyncTask<String,Void,String> icon_upload = new UploadFileAsync(copy_file, ApiConfig.APPICONUPLOADURI, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_bar.setVisibility(View.GONE);
                        if(bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)) {
                            final_step();
                        }else{
                            //Toast.makeText(FileUploadA.this, "icon uploading failed", Toast.LENGTH_SHORT).show();
                            uploading_item_txt.setText("icon uploading failed");
                            uploading_item_txt.setTextColor(getResources().getColor(R.color.warningred));
                            retry_btn.setVisibility(View.VISIBLE);
                            failed_type=3;
                        }
                    }
                });

            }
        });

        icon_upload.execute();
    }

    private void final_step() {
        rollout_button.setVisibility(View.VISIBLE);
        uploading_item_txt.setTextColor(getResources().getColor(R.color.green));
        uploading_item_txt.setText("app icon uploaded successfully");
    }

    private String random_name() {
        String name = app_advanced_details.package_name+System.currentTimeMillis();
        return name;

    }


    public void finish_animated(){
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

    }
}