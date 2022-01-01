package com.orgdevelopers.hamzs.SimpleClasses;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;

public class Variables {

    public static final String App_path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separator+"Hamz Appstore";
    public static final String Http="https://";


    //App settings//

    public static Boolean Toast_enabled=false;


    //Data lists//
    public static JSONArray array;

    public static JSONArray image_array,description_array,rating3_array,rating_count;

    public static JSONArray download_information;


    //Activity configs don't change
    public static JSONObject AppDetails_isFirst;

    public static String my_review;

}
