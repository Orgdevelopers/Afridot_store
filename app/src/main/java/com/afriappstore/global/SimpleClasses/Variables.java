package com.afriappstore.global.SimpleClasses;

import android.os.Environment;

import com.afriappstore.global.ExtraActivities.PublishApps;
import com.afriappstore.global.Model.AdvancedInfoModel;
import com.afriappstore.global.Model.PublishAppModel;
import com.afriappstore.global.Model.SelectedImageModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Variables {

    public static final String App_path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separator+"Afri Appstore";
    public static final String Http="https://";
    public static final String Appcopypath=App_path+File.separator+"operations/";


    //App settings//

    public static Boolean Toast_enabled=false;


    //Data lists//
    public static JSONArray array;

    public static JSONArray image_array,description_array,rating3_array,rating_count;

    public static JSONArray download_information;


    //Activity configs don't change
    public static JSONObject AppDetails_isFirst;

    public static String my_review;
    public static Boolean is_verify;


    //pub.
    public static PublishAppModel publishAppModel;
    public static ArrayList<SelectedImageModel> selectedappimagelist;
    public static AdvancedInfoModel advancedInfoModel;


}
