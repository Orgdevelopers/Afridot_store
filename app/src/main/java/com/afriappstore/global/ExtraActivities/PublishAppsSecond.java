package com.afriappstore.global.ExtraActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.Adepters.Selected_uploadimgAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.FileUploadServices.FileUploadA;
import com.afriappstore.global.FileUploadServices.UploadFileAsync;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.AdvancedInfoModel;
import com.afriappstore.global.Model.SelectedImageModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class PublishAppsSecond extends AppCompatActivity {
    
    LinearLayout select_file_layout,apkinfolayout;
    RecyclerView selected_image_list;
    Selected_uploadimgAdapter adapter;
    LinearLayout check_package_availability,publish_app_btn;

    String package_name,app_path,version_name,version_code;

    ArrayList<SelectedImageModel> main_list;
    int PICK_APK=101, PICK_IMAGE=102,ASK_PERMISSION=103,UPDATE_IMG=104,adapter_pos=0;

    Boolean publish_enabled=false,check_package_enabled=false;


    ///
    TextView select_file_tex,app_name_txt,version_name_txt,version_code_txt,package_name_txt,file_name_txt;

    ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_apps_second);
        main_list=new ArrayList<>();
        initViews();
        setupimageselector();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==select_file_layout){
                    Intent intent = new Intent();
                    //sets the select file to all types of files
                    intent.setType("application/vnd.android.package-archive");
                    //allows to select data and return it
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //starts new activity to select file and return data
                    startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_APK);

                }else if (view==check_package_availability){
                    if (check_package_enabled){
                        Functions.showLoader(PublishAppsSecond.this);
                        //
                        ApiRequests.isPackageAvailable(PublishAppsSecond.this, package_name, new FragmentCallBack() {
                            @Override
                            public void onResponse(Bundle bundle) {
                                Functions.cancelLoader();
                                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                                    publish_enabled=true;
                                    publish_app_btn.setBackgroundColor(getResources().getColor(R.color.install_btn_bg));

                                }

                            }
                        });

                    }else{
                        Toast.makeText(PublishAppsSecond.this, "Please select a valid apk", Toast.LENGTH_SHORT).show();
                    }

                }else if (view==publish_app_btn){
                    if (publish_enabled){
                        if (check_data()){
                            String destination_filename="",destination_filepath;
                            destination_filename=package_name+" time-"+System.currentTimeMillis()+".apk";
                            destination_filepath=Variables.Appcopypath+destination_filename;

                            Variables.advancedInfoModel=new AdvancedInfoModel();

                            boolean b =Functions.CopyFile(app_path,destination_filepath);
                            //upload_apk_file();
                            if (b){
                                Variables.advancedInfoModel=new AdvancedInfoModel();
                                Variables.advancedInfoModel.version_code=version_code;
                                Variables.advancedInfoModel.version_name=version_name;
                                Variables.advancedInfoModel.package_name=package_name;
                                Variables.advancedInfoModel.apk_path=destination_filepath;
                            }else{
                                Variables.advancedInfoModel.apk_path=app_path;
                            }
                            Handler handler =new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Variables.selectedappimagelist=null;
                                    Variables.selectedappimagelist=main_list;

                                    Intent intent =  new Intent(PublishAppsSecond.this, FileUploadA.class);
                                    startActivity(intent);


                                    Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    },500);
                                }
                            },100);


                        }else{
                            Toast.makeText(PublishAppsSecond.this, "please atleast two images", Toast.LENGTH_SHORT).show();
                        }
                    }
                }


            }
        };

        select_file_layout.setOnClickListener(clickListener);
        check_package_availability.setOnClickListener(clickListener);
        publish_app_btn.setOnClickListener(clickListener);


    }

    private void upload_apk_file() {
        Functions.showLoader(PublishAppsSecond.this);
        AsyncTask<String,Void,String> task = new UploadFileAsync(app_path,"",new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                Functions.cancelLoader();
               /* if (bundle.getString("t").equals("t")){
                    //Toast.makeText(PublishApps.this, "done", Toast.LENGTH_SHORT).show();
                    Log.wtf("resp","done");

                }else {
                    Log.wtf("resp","fail");
                }*/
            }
        });
        task.execute();

    }

    private boolean check_data() {
        boolean c = false;
        if (main_list.size()>=2){
            c=true;
        }

        return c;
    }

    private void upload_data() {

    }

    private void setupimageselector() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        adapter = new Selected_uploadimgAdapter(this, new ArrayList<>(), new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                if (bundle.getString("pos").equals(String.valueOf(main_list.size()))){
                    //here the last image is clicked
                    if (ActivityCompat.checkSelfPermission(PublishAppsSecond.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(i,PICK_IMAGE);
                    }else{
                        ActivityCompat.requestPermissions(PublishAppsSecond.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ASK_PERMISSION);
                    }

                }else{
                    adapter_pos=Integer.parseInt(bundle.getString("pos"));
                    if (ActivityCompat.checkSelfPermission(PublishAppsSecond.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(i,UPDATE_IMG);
                    }else{
                        ActivityCompat.requestPermissions(PublishAppsSecond.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ASK_PERMISSION);
                    }

                }

            }
        });

        selected_image_list.setLayoutManager(linearLayoutManager);
        selected_image_list.setAdapter(adapter);

    }

    private void initViews() {
        select_file_layout=findViewById(R.id.select_file_layout);
        apkinfolayout=findViewById(R.id.apkinfolayout);
        selected_image_list=findViewById(R.id.selected_image_list);
        check_package_availability=findViewById(R.id.check_package_availability);
        publish_app_btn=findViewById(R.id.publish_app_btn);
        select_file_tex=findViewById(R.id.select_file_txt);
        app_name_txt=findViewById(R.id.selected_app_name);
        version_name_txt=findViewById(R.id.selected_version_name);
        version_code_txt=findViewById(R.id.selected_version_code);
        package_name_txt=findViewById(R.id.selected_package_name);
        file_name_txt=findViewById(R.id.selected_app_filename);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE){
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

                    SelectedImageModel item = new SelectedImageModel();
                    item.path=picturePath;
                    main_list.add(item);

                    adapter.AddImage(item);


                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PublishAppsSecond.this, "please select a image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(PublishAppsSecond.this, "please select a image", Toast.LENGTH_SHORT).show();
            }


        }else if (requestCode==PICK_APK){
            if (resultCode== Activity.RESULT_OK){
               String real_path=getPath(PublishAppsSecond.this,data.getData());
               if(real_path!=null){
                   app_path=real_path;
                   check_apk_file();

               }else {
                   Toast.makeText(this, "please try selecting apk again", Toast.LENGTH_SHORT).show();
               }

            }else{
                Toast.makeText(PublishAppsSecond.this, "please select a apk", Toast.LENGTH_SHORT).show();
            }

        }else if (requestCode==ASK_PERMISSION){
            if (!(requestCode==RESULT_OK)){

                Toast.makeText(this, "permission required", Toast.LENGTH_SHORT).show();
            }

        }else if (requestCode==UPDATE_IMG){
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

                    SelectedImageModel item = new SelectedImageModel();
                    item.path=picturePath;

                   main_list.remove(adapter_pos);
                   main_list.add(adapter_pos,item);
                   adapter.UpdateImage(main_list);


                    //main_list.add(item);
                    //adapter.AddImage(item);


                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PublishAppsSecond.this, "please select a image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(PublishAppsSecond.this, "please select a image", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void check_apk_file() {

        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(app_path,0);
            packageInfo.applicationInfo.sourceDir       = app_path;
            packageInfo.applicationInfo.publicSourceDir = app_path;

            package_name=packageInfo.packageName;
            version_code=String.valueOf(packageInfo.versionCode);
            version_name=packageInfo.versionName;

            select_file_layout.setVisibility(View.VISIBLE);
            apkinfolayout.setVisibility(View.VISIBLE);
            select_file_tex.setVisibility(View.GONE);
            version_name_txt.setText("Version name: "+version_name);
            version_code_txt.setText("Version code: "+version_code);
            package_name_txt.setText("package: "+package_name);
            File file = new File(app_path);
            file_name_txt.setText(file.getName());
            check_package_enabled=true;

            app_name_txt.setText(Variables.publishAppModel.app_name);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri;
                try {
                    contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}