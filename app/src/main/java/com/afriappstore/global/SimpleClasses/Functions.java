package com.afriappstore.global.SimpleClasses;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Adepters.Categories_adapter;
import com.afriappstore.global.Adepters.DialogAdapters.D_allcatAdaper;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Model.CategoriesModel;
import com.afriappstore.global.Model.SliderModel;
import com.airbnb.lottie.LottieAnimationView;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import io.paperdb.Paper;

public class Functions {

    static Dialog loading_dialog=null;

    public static void showBigImage(Context context, Drawable img ,String path){
        final Dialog dialog = new Dialog(context);
        //dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.big_image_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().getAttributes().windowAnimations=R.style.my_custom_animation;

        final ImageView big_img=dialog.findViewById(R.id.big_image);
        final ImageView big_back=dialog.findViewById(R.id.back_big_image);
        Picasso picasso = Picasso.get();
        if (img!=null){
            big_img.setImageDrawable(img);
            dialog.show();
        }else if (!path.equals("")){
            if (path.contains("http://")){
                picasso.load(path).into(big_img);
                dialog.show();
            }else{
                try{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    big_img.setImageBitmap(bitmap);
                    dialog.show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            Functions.ShowToast(context,"path error");
        }

        big_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void ShowToast(Context context, String text) {
        if (context != null && Variables.Toast_enabled == true) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static String Format_numbers(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }


    }

    public static void Showdouble_btn_alert(Context context,String header, String subheader,String neg_btn_txt,String positive_btn_txt,Boolean outsidetuch_cancel,FragmentCallBack callBack){
        final Dialog dialog = new Dialog(context);
        //dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.double_btn_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(outsidetuch_cancel);
        dialog.getWindow().getAttributes().windowAnimations=R.style.Animation_Design_BottomSheetDialog;

        final TextView title,subtitle,cancel,ok;
        title=dialog.findViewById(R.id.double_btn_popup_title);
        subtitle=dialog.findViewById(R.id.double_btn_popup_subtitle);
        cancel=dialog.findViewById(R.id.double_btn_popup_cancel);
        ok=dialog.findViewById(R.id.double_btn_popup_ok);

        title.setText(header);
        subtitle.setText(subheader);
        cancel.setText(neg_btn_txt);
        ok.setText(positive_btn_txt);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action","cancel");
                callBack.onResponce(bundle);
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action","ok");
                callBack.onResponce(bundle);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("ResourceAsColor")
    public static void showDownloadConfirmer(Context context, String app_name, String App_size, boolean isCancelable, FragmentCallBack callBack) {
        final Dialog dialog = new Dialog(context);
        //dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.show_download_confirmer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.getWindow().getAttributes().windowAnimations=R.style.Animation_Design_BottomSheetDialog;

        final TextView appname,sizename,cancel_btn;
        final Button download_btn;
        appname=dialog.findViewById(R.id.pupup_appname);
        sizename=dialog.findViewById(R.id.popup_appsize);
        cancel_btn=dialog.findViewById(R.id.popup_cancel);
        download_btn=dialog.findViewById(R.id.popup_download_btn);
        download_btn.setBackgroundColor(context.getResources().getColor(R.color.install_btn_bg));

        appname.setText("Downlaod "+app_name);
        sizename.setText("App size: "+App_size+" MB");

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle b =new Bundle();
                b.putString("click","cancel");
                callBack.onResponce(b);
            }
        });

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle b2 = new Bundle();
                b2.putString("click","download");
                callBack.onResponce(b2);
            }
        });


        dialog.show();
    }

    public static void DownloadWithLoading(Context context,String download_uri,String path, String app_name_s,int pos,FragmentCallBack callBack){
        final Dialog download_dialog = new Dialog(context);
        download_dialog.setContentView(R.layout.download_with_loading);
        download_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        download_dialog.setCanceledOnTouchOutside(false);
        download_dialog.getWindow().getAttributes().windowAnimations=R.style.Animation_Design_BottomSheetDialog;

        final LottieAnimationView animation = download_dialog.findViewById(R.id.popup_download_animation);
        final TextView cancel_btn=download_dialog.findViewById(R.id.popup_download_cancel);
        final Button open_btn=download_dialog.findViewById(R.id.popup_download_open);
        final TextView app_name=download_dialog.findViewById(R.id.popup_download_appname);
        final TextView app_size=download_dialog.findViewById(R.id.popup_download_appsize);
        final TextView app_percent=download_dialog.findViewById(R.id.popup_download_percent);

        app_name.setText("Download "+app_name_s);


        PRDownloader.initialize(context.getApplicationContext());
        String filename=app_name_s+get_random_int(999,99999)+".apk";
        Integer downloadId=0;
        String last_download_path="";

        //animation.playAnimation();

        open_btn.setBackgroundColor(context.getResources().getColor(R.color.open_btn_untuchable_color));

        download_dialog.show();
         if (Variables.download_information==null){
             downloadId = PRDownloader.download(download_uri,path, filename)
                     .build()
                     .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                         @Override
                         public void onStartOrResume() {

                         }
                     })
                     .setOnPauseListener(new OnPauseListener() {
                         @Override
                         public void onPause() {

                         }
                     })
                     .setOnCancelListener(new OnCancelListener() {
                         @Override
                         public void onCancel() {

                         }
                     })
                     .setOnProgressListener(new OnProgressListener() {
                         @Override
                         public void onProgress(Progress progress) {
                             long current_bytes=progress.currentBytes;
                             long total_bytes=progress.totalBytes;
                             Float percent= Float.valueOf((current_bytes*100/total_bytes));
                             animation.setProgress(percent/100);


                             app_percent.setText(percent+"%");
                             if (app_size.getText().equals("App size: 0 MB")){
                                 Float size_inmb=Float.valueOf(total_bytes*1000/1048576);
                                 app_size.setText("App size: "+size_inmb/1000 +" MB");
                             }
                             Log.wtf("progress","current: "+ percent+ "     "+current_bytes+"  " +total_bytes+"   "+percent/100);
                         }
                     })
                     .start(new OnDownloadListener() {
                         @Override
                         public void onDownloadComplete() {
                             open_btn.setBackgroundColor(context.getResources().getColor(R.color.install_btn_bg));
                             open_btn.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     //Toast.makeText(context,"File stored in "+path+ File.separator+filename, Toast.LENGTH_SHORT).show();
                                     download_dialog.dismiss();
                                     Bundle bundle = new Bundle();
                                     bundle.putString("action","install");
                                     bundle.putString("path",path+ File.separator+filename);
                                     callBack.onResponce(bundle);
                                 }
                             });
                             //
                             Log.wtf("complete","dinufddcbvjdfnvnkvslvnljvnjvdkjnjvdnjcv");
                         }

                         @Override
                         public void onError(Error error) {
                             download_dialog.dismiss();
                             Log.wtf("error",error.toString());
                         }
                     });
         }else{
             JSONObject appdata = null;
             try {
                 appdata=Variables.download_information.getJSONObject(pos);
             } catch (JSONException e) {
                 e.printStackTrace();
                 appdata=null;
             }

             if (appdata==null){
                 downloadId = PRDownloader.download(download_uri,path, filename)
                         .build()
                         .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                             @Override
                             public void onStartOrResume() {

                             }
                         })
                         .setOnPauseListener(new OnPauseListener() {
                             @Override
                             public void onPause() {

                             }
                         })
                         .setOnCancelListener(new OnCancelListener() {
                             @Override
                             public void onCancel() {

                             }
                         })
                         .setOnProgressListener(new OnProgressListener() {
                             @Override
                             public void onProgress(Progress progress) {
                                 long current_bytes=progress.currentBytes;
                                 long total_bytes=progress.totalBytes;
                                 Float percent= Float.valueOf((current_bytes*100/total_bytes));
                                 animation.setProgress(percent/100);


                                 app_percent.setText(percent+"%");
                                 if (app_size.getText().equals("App size: 0 MB")){
                                     Float size_inmb=Float.valueOf(total_bytes*1000/1048576);
                                     app_size.setText("App size: "+size_inmb/1000 +" MB");
                                 }
                                 Log.wtf("progress","current: "+ percent+ "     "+current_bytes+"  " +total_bytes+"   "+percent/100);
                             }
                         })
                         .start(new OnDownloadListener() {
                             @Override
                             public void onDownloadComplete() {
                                 open_btn.setBackgroundColor(context.getResources().getColor(R.color.install_btn_bg));
                                 open_btn.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         //Toast.makeText(context,"File stored in "+path+ File.separator+filename, Toast.LENGTH_SHORT).show();
                                         download_dialog.dismiss();
                                         Bundle bundle = new Bundle();
                                         bundle.putString("action","install");
                                         bundle.putString("path",path+ File.separator+filename);
                                         callBack.onResponce(bundle);
                                     }
                                 });
                                 //
                                 Log.wtf("complete","dinufddcbvjdfnvnkvslvnljvnjvdkjnjvdnjcv");
                             }

                             @Override
                             public void onError(Error error) {
                                 download_dialog.dismiss();
                                 Log.wtf("error",error.toString());
                             }
                         });
             }else {
                 try {
                     last_download_path=appdata.getString("path");
                     Bundle bundle = new Bundle();
                     bundle.putString("action","install");
                     bundle.putString("path",last_download_path);
                     callBack.onResponce(bundle);
                     download_dialog.dismiss();
                 } catch (Exception e) {
                     e.printStackTrace();
                     Bundle bundle = new Bundle();
                     bundle.putString("action","cancel");
                     callBack.onResponce(bundle);
                     download_dialog.dismiss();
                 }
             }
         }
        Integer finalDownloadId = downloadId;
        cancel_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 download_dialog.dismiss();
                 try{
                     PRDownloader.cancel(finalDownloadId);
                 }catch (NullPointerException e){
                     e.printStackTrace();
                 }
                 Toast.makeText(context, "Download canceled", Toast.LENGTH_SHORT).show();
                 Bundle bundle = new Bundle();
                 bundle.putString("action","cancel");
                 callBack.onResponce(bundle);
             }
         });


    }

    public static Integer get_random_int(int minn,int maxx){
        int min = minn;
        int max = maxx;

        //Generate random int value from 50 to 100
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        return (random_int);
    }

    public static SharedPreferences getSharedPreference(Context context) {
        if (ShearedPrefs.sharedPreferences != null)
            return ShearedPrefs.sharedPreferences;
        else {
            ShearedPrefs.sharedPreferences = context.getSharedPreferences(ShearedPrefs.PREF_NAME, Context.MODE_PRIVATE);
            return ShearedPrefs.sharedPreferences;
        }

    }


    public static void showLoader(Context context){
        if (loading_dialog!=null) {
            loading_dialog = null;
        }
            loading_dialog=new Dialog(context);
            loading_dialog.setContentView(R.layout.loading_dialog_item);
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loading_dialog.setCanceledOnTouchOutside(false);
            loading_dialog.getWindow().getAttributes();
            loading_dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_bounce_animation;

            loading_dialog.show();


    }

    public static void cancelLoader(){
        if (loading_dialog!=null){
            loading_dialog.dismiss();
            loading_dialog=null;
        }
    }

    public static String Convert_rating(Float rating){
        String bc=new DecimalFormat("#.#").format(rating);
        return bc;
    }

    public static Integer convert_appid_to_pos(int app_id){
        Integer pos=101;
        try{
            for (int i=0;i<Variables.array.length();i++){
                if (Variables.array.getJSONObject(i).getString("id").equals(String.valueOf(app_id))){
                    pos=i;
                    break;
                }

            }
            return pos;
        }catch (Exception e){
            e.printStackTrace();
            return pos;

        }
    }

    public static boolean is_first(String appid){
        Boolean ret=true;
        if (Variables.AppDetails_isFirst!=null){
            try{
                if (Variables.AppDetails_isFirst.getBoolean(appid)){
                    ret=false;
                }
            }catch (Exception e){
                e.printStackTrace();
                ret=true;
            }

        }else{
            ret=true;
            Variables.AppDetails_isFirst=new JSONObject();
        }

        if (ret){
            try {
                Variables.AppDetails_isFirst.put(appid,true);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return ret;
    }

    public static void phoneSignUp_updateData(Context context ,FirebaseUser user,Bundle bundle){
        SharedPreferences.Editor editor = Functions.getSharedPreference(context).edit();
        editor.putString(ShearedPrefs.U_ID, user.getUid());
        editor.putString(ShearedPrefs.U_PHONE, user.getPhoneNumber());
        editor.putBoolean(ShearedPrefs.IS_LOGIN,true);
        editor.putString(ShearedPrefs.U_FNAME, bundle.getString(ApiConfig.Request_PostF_Name));
        editor.putString(ShearedPrefs.U_LNAME, bundle.getString(ApiConfig.Request_PostL_Name));
        editor.putString(ShearedPrefs.SIGN_IN_TYPE,ShearedPrefs.SIGN_IN_TYPE_PHONE);
        editor.putString(ShearedPrefs.U_PIC,bundle.getString("image"));

        editor.commit();

    }

    public static Boolean is_Login(Context context){

        Boolean is_login = Functions.getSharedPreference(context).getBoolean(ShearedPrefs.IS_LOGIN,false);

        return is_login;
    }

    public static String Login_Type(Context context){

        String Login_type = Functions.getSharedPreference(context).getString(ShearedPrefs.SIGN_IN_TYPE,"not");

        String ret = "";

        if (Login_type.equals("not")){
            ret= "not";
        }else {
            ret=Login_type;
        }
        return ret;


    }

    public static void Log_Out(Context context){
        try {
            String login_type=Functions.getSharedPreference(context).getString(ShearedPrefs.SIGN_IN_TYPE,"not");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            if (login_type.equals(ShearedPrefs.SIGN_IN_TYPE_PHONE)){
                if (user!=null){
                    auth.signOut();
                    clear_saves(context);
                }
            }else if (login_type.equals(ShearedPrefs.SIGN_IN_TYPE_EMAIL)){
                if (user!=null){
                    auth.signOut();

                }
                clear_saves(context);

            }else if (login_type.equals(ShearedPrefs.SIGN_IN_TYPE_GOOGLE)){
                if (user!=null){
                    auth.signOut();
                    clear_saves(context);
                }

            }else if (login_type.equals("not")){
                if (user!=null){
                    auth.signOut();
                    clear_saves(context);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void clear_saves(Context context) {
        SharedPreferences.Editor editor = Functions.getSharedPreference(context).edit();
        editor.putBoolean(ShearedPrefs.IS_LOGIN,false);
        editor.putString(ShearedPrefs.U_ID, null);
        editor.putString(ShearedPrefs.U_PHONE, null);
        editor.putString(ShearedPrefs.U_EMAIL, null);
        editor.putString(ShearedPrefs.U_FNAME, null);
        editor.putString(ShearedPrefs.U_LNAME, null);
        editor.putString(ShearedPrefs.SIGN_IN_TYPE,null);
        editor.putString(ShearedPrefs.U_PIC,null);

        editor.commit();
        editor.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void Image_to_Base64(String image,FragmentCallBack callBack){
        String base64="";
        try {
            ByteArrayOutputStream baos =new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG,10,baos);
            byte[] image_bytes=baos.toByteArray();
            base64= Base64.getEncoder().encodeToString(image_bytes);

        }catch (Exception e){
            e.printStackTrace();
            base64="default";
        }

        Bundle bundle = new Bundle();
        bundle.putString("img",base64);
        //Log.wtf("img",base64);
        callBack.onResponce(bundle);
    }

    public static Boolean Log_in_via_email(Context context ,JSONObject user){
        boolean b=false;

        try {
            //save data


            SharedPreferences.Editor editor = Functions.getSharedPreference(context).edit();
            editor.putString(ShearedPrefs.U_ID, user.getString("u_id"));
            editor.putString(ShearedPrefs.U_PHONE,"");
            editor.putString(ShearedPrefs.U_EMAIL,user.getString("email"));
            editor.putBoolean(ShearedPrefs.IS_LOGIN,true);
            editor.putString(ShearedPrefs.U_FNAME, user.getString("first_name"));
            editor.putString(ShearedPrefs.U_LNAME, user.getString("last_name"));
            editor.putString(ShearedPrefs.SIGN_IN_TYPE,ShearedPrefs.SIGN_IN_TYPE_EMAIL);
            editor.putString(ShearedPrefs.U_PIC, user.getString("pic"));

            editor.commit();

            b=true;

        }catch (Exception e){
            e.printStackTrace();
            b=false;
        }

        return b;
    }

    public static String generateRandomU_Id(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuioplkjhgfdsazxcvbnm1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 29) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String uid = salt.toString();
        return uid;
    }

    public static void isVerified(Context context,FragmentCallBack callBack) {

        Bundle bundle = new Bundle();
        if (Functions.getSharedPreference(context).getString(ShearedPrefs.SIGN_IN_TYPE,"not").equals(ShearedPrefs.SIGN_IN_TYPE_EMAIL)){

            try {

                    ApiRequests.checkverificationstatus(context, Functions.getSharedPreference(context).getString(ShearedPrefs.U_ID, ""), new FragmentCallBack() {
                        @Override
                        public void onResponce(Bundle bundle) {

                            callBack.onResponce(new Bundle());

                        }
                    });




            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
            callBack.onResponce(new Bundle());
        }

    }

    public static void show_select_categories(Context context,FragmentCallBack callBack){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog_show_all_categories);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.less_round_edge_ractengle_white));
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().getAttributes();
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_bounce_animation;


        dialog.show();

        ProgressBar loading = dialog.findViewById(R.id.loading_progress_bar);
        RecyclerView cat_list=dialog.findViewById(R.id.all_cat_list);

        loading.setVisibility(View.VISIBLE);
        cat_list.setVisibility(View.GONE);

        ApiRequests.getAllCategories(context, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                loading.setVisibility(View.GONE);
                cat_list.setVisibility(View.VISIBLE);
                try {
                    if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)) {
                        final ArrayList<CategoriesModel> datalist=new ArrayList<>();
                        JSONArray categories = new JSONArray(bundle.getString(ApiConfig.Request_response));

                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject singlecat = categories.getJSONObject(i);

                            CategoriesModel model = new CategoriesModel();

                            model.id = singlecat.getString("id");
                            model.name = singlecat.getString("name");
                            model.pic = singlecat.getString("pic");

                            datalist.add(model);

                        }
                        if (datalist != null && !datalist.isEmpty()) {
                            LinearLayoutManager manager = new LinearLayoutManager(context);
                            D_allcatAdaper adapter = new D_allcatAdaper(context, datalist, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    callBack.onResponce(bundle);
                                    dialog.dismiss();
                                }
                            });

                            cat_list.setLayoutManager(manager);
                            cat_list.setAdapter(adapter);
                        } else {
                            Toast.makeText(context, "no categories on server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public static boolean CopyFile(String target,String destination){
        boolean b = false;
        try {
            InputStream in = new FileInputStream(target);
            OutputStream out = new FileOutputStream(destination);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
            b=true;
            //Toast.makeText(C, "file copied to " +destination_path, Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, "Total files "+files.length, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            b=false;
        }

        return b;
    }

    public static void make_app_dirs(){
        File file = null;
        try {
            file = new File(Variables.Appcopypath);
            if (!file.exists()){
                file.mkdirs();
            }else{
                file.delete();
                file.exists();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String convertStatus(String status_type){

        String ret="";
        if (status_type.equals("0")){
            ret="In Review...";

        }else if (status_type.equals("1")){
            ret="Live";

        }else if (status_type.equals("2")){
            ret="Unpublished";

        }else if (status_type.equals("3")){
            ret="Suspended";

        }else{
            ret="Unspecified";
        }

        return ret;

    }

    public static void log(String response) {
        Log.wtf("log",response);
    }


}
