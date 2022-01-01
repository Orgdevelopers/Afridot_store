package com.orgdevelopers.hamzs.ApiClasses;

import static com.orgdevelopers.hamzs.ApiClasses.ApiConfig.Request_UpdateProfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Api;
import com.orgdevelopers.hamzs.Interfaces.FragmentCallBack;
import com.orgdevelopers.hamzs.R;
import com.orgdevelopers.hamzs.SimpleClasses.Functions;
import com.orgdevelopers.hamzs.SimpleClasses.ShearedPrefs;
import com.orgdevelopers.hamzs.SimpleClasses.Variables;
import com.orgdevelopers.hamzs.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequests {

    public static void getappimage(Context context, String app_id, FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,response);
                callBack.onResponce(bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                bundle.putString("err", error.getMessage());
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put(ApiConfig.Request,ApiConfig.Request_getappimages);
                params.put(ApiConfig.POST_App_Id,app_id);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }


    public static void getAllapps(Context c,FragmentCallBack callBack) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.wtf("response", response + "end");
                if (!response.equals("")) {
                    try {
                        //Log.wtf("response", "72");
                        //JSONObject v = new JSONObject(response);
                        //v.getJSONArray("");
                        JSONArray temp_array = new JSONArray(response);
                        //Log.wtf("response", "76");

                        if (Variables.array!=null){
                            Variables.array = null;
                        }
                        Log.wtf("response", "81");

                        Variables.array=new JSONArray();
                        Variables.array = temp_array;

                        Bundle bundle = new Bundle();
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);

                        callBack.onResponce(bundle);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Functions.ShowToast(c, error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                params.put(ApiConfig.Request,ApiConfig.Request_getallapps);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(c);
        queue.add(request);

    }

    public static void get_app_long_description(Context context,String appid,FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,response);
                callBack.onResponce(bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                bundle.putString("err", error.getMessage());
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put(ApiConfig.Request,ApiConfig.Request_getlongdescription);
                params.put(ApiConfig.POST_App_Id,appid);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    public static void get3random_ratings(Context context,String app_id,FragmentCallBack callBack){
            JSONArray local=null;
            if (Variables.rating3_array==null){
            Variables.rating3_array=new JSONArray();
        }
            try {
                local=Variables.rating3_array.getJSONArray(Integer.parseInt(app_id));
            }catch (Exception e){
                e.printStackTrace();
                local=null;
            }

        try {
            if (local!=null){
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,"local");
                callBack.onResponce(bundle);

            }else{
                StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("")){

                            Bundle bundle = new Bundle();
                            try {
                                JSONArray resp = new JSONArray(response);
                                Variables.rating3_array.put(Integer.parseInt(app_id),resp);
                                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                            }catch (Exception e){
                                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                                e.printStackTrace();
                            }

                            bundle.putString(ApiConfig.Request_response,response.toString());
                            callBack.onResponce(bundle);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                        bundle.putString(ApiConfig.Request_response, error.getMessage());
                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params= new HashMap<String, String>();
                        params.put(ApiConfig.POST_App_Id,app_id);
                        params.put(ApiConfig.Request,ApiConfig.Request_get3reviews);
                        return params;
                    }
                };

                RequestQueue q =Volley.newRequestQueue(context.getApplicationContext());
                q.add(request);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public static void getreviewscount(Context context,String appid,FragmentCallBack callBack){
        if (Variables.rating_count==null){
            Variables.rating_count=new JSONArray();
        }
        JSONObject local=null;
        try {
            local=Variables.rating_count.getJSONObject(Integer.parseInt(appid));
        } catch (JSONException e) {
            e.printStackTrace();
            local=null;
        }

        try {
            if (local!=null){
                //local
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,"local");
                callBack.onResponce(bundle);
            }else {

                StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.wtf("review_count",response);
                        Bundle bundle = new Bundle();
                        if (response!=null){
                            bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                            JSONObject obj = new JSONObject(response);
                            Variables.rating_count.put(Integer.parseInt(appid),obj);

                        }else{
                            bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);

                        }
                        bundle.putString(ApiConfig.Request_response,response);

                        callBack.onResponce(bundle);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                        bundle.putString(ApiConfig.Request_response,error.getMessage());
                        callBack.onResponce(bundle);
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConfig.Request,ApiConfig.Request_getreviewscount);
                        params.put(ApiConfig.POST_App_Id, appid);
                        return params;
                    }
                };


                RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
                queue.add(request);

            }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public static void getAllReviews(Context context,String app_id,int Starting_point, FragmentCallBack callBack){

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,response);
                callBack.onResponce(bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ApiConfig.Request,ApiConfig.Request_getAllReviews);
                params.put(ApiConfig.POST_App_Id,app_id);
                params.put("start_point",String.valueOf(Starting_point));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    public static void callApiForPhoneSignIn(Context context,String phone,FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,response);
                callBack.onResponce(bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                bundle.putString(ApiConfig.Request_response,error.getMessage());
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put(ApiConfig.Request,ApiConfig.Request_PhoneSignIn);
                params.put(ApiConfig.Request_PostPhone,phone);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);

    }

    public static void createPhoneUser(Context context,String phone,String f_name,String l_name, String auth_id,String img,FragmentCallBack callBack){
        Log.wtf("called","fdffkmfkfkmf");
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle bundle = new Bundle();
                try{
                    JSONObject resp = new JSONObject(response);
                    String code = resp.optString("code");
                    if (code.equals("200")){
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                        bundle.putString(ApiConfig.Request_response,resp.optString("pic"));

                    }else{
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                        bundle.putString(ApiConfig.Request_response,resp.optString("msg"));
                    }

                }catch (Exception e){
                 e.printStackTrace();
                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    bundle.putString(ApiConfig.Request_response,e.getMessage());
                }

                callBack.onResponce(bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                bundle.putString(ApiConfig.Request_response,error.getMessage());
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ApiConfig.Request,ApiConfig.Request_createPhoneUser);
                params.put(ApiConfig.Request_PostPhone,phone);
                params.put(ApiConfig.Request_PostF_Name,f_name);
                params.put(ApiConfig.Request_PostL_Name,l_name);
                params.put(ApiConfig.Request_PostPic,img);
                params.put(ApiConfig.AUTH_ID,auth_id);

                return params;
            }
        };

        RequestQueue q = Volley.newRequestQueue(context.getApplicationContext());
        q.add(request);
    }

    public static void update_profile(Context context,String f_name,String l_name,String pic_path,int update_type, FragmentCallBack callBack){

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("resp",response);
                Bundle bundle = new Bundle();

                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject data=object.getJSONObject("msg");
                    if (object.getString(ApiConfig.Request_code).equals("200")){
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                        bundle.putString(ApiConfig.Request_PostF_Name, data.getString("firstname"));
                        bundle.putString(ApiConfig.Request_PostL_Name, data.getString("lastname"));
                        try {
                            bundle.putString(ApiConfig.Request_PostPic, data.getString("pic"));

                        }catch (Exception E){
                            E.printStackTrace();
                        }

                    }else{
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);

                }
                callBack.onResponce(bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                callBack.onResponce(bundle);
                Log.wtf("error",error.getMessage());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ApiConfig.Request,Request_UpdateProfile);
                params.put(ShearedPrefs.U_ID,Functions.getSharedPreference(context).getString(ShearedPrefs.U_ID,""));
                params.put(ApiConfig.Request_PostF_Name,f_name);
                params.put(ApiConfig.Request_PostL_Name,l_name);
                params.put(ApiConfig.Request_PostPic,pic_path);
                params.put("update_type",String.valueOf(update_type));
                params.put(ApiConfig.Request_PostPhone,Functions.getSharedPreference(context).getString(ShearedPrefs.U_PHONE,""));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);

    }

    public static void updateShareUrl(Context context){
        SharedPreferences.Editor editor = Functions.getSharedPreference(context).edit();
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("error",response);
                //Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
                editor.putString(ShearedPrefs.AppShareUrl,response);
                editor.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("error",error.getMessage());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ApiConfig.Request,ApiConfig.Request_GetShareUrl);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    public static void RateApp(Context context,String app_id,String name,String star,String review,FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("resp",response);
                Bundle bundle = new Bundle();
                try{
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equals("200")){
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                        bundle.putString(ApiConfig.Request_response,response);
                    }else{
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                        bundle.putString(ApiConfig.Request_response,response);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    bundle.putString(ApiConfig.Request_response,response);

                }

                callBack.onResponce(bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                bundle.putString(ApiConfig.Request_response,error.getMessage());
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ApiConfig.Request,ApiConfig.Request_PostReview);
                params.put(ApiConfig.POST_App_Id,app_id);
                params.put(ShearedPrefs.U_ID,Functions.getSharedPreference(context).getString(ShearedPrefs.U_ID,""));
                params.put("star",star);
                params.put("review",review);
                params.put("name",name);
                params.put(ApiConfig.Request_PostPic,Functions.getSharedPreference(context).getString(ShearedPrefs.U_PIC,""));
                return params;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    public static void CheckEmailFromServer(Context context,String email,FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("response",response+"   email: "+email);
                Bundle bundle = new Bundle();
                try {
                    JSONObject object = new JSONObject(response);
                    String code= object.getString("code");
                    if (code.equals("1001")){
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                        bundle.putString("type","login");

                    }else{
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                        bundle.putString("type","signup");

                    }

                    callBack.onResponce(bundle);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("error",error.getMessage());
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(ApiConfig.Request,ApiConfig.CHECK_SERVER_EMAIL);
                params.put(ApiConfig.POST_Email,email);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    public static void EmailLogin(Context context,String email,String password,FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("response",response);

                Bundle bundle = new Bundle();

                try {
                    JSONObject resp = new JSONObject(response);
                    String code = resp.getString("code");

                    if (code.equals("200")){
                        //password matched
                        JSONObject user = resp.getJSONObject("resp");
                        if (Functions.Log_in_via_email(context,user)){
                            bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);

                        }else{
                            bundle.putString(ApiConfig.Request_code,"101");

                        }

                    }else {
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);

                }

                callBack.onResponce(bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("error",error.getMessage());
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(ApiConfig.Request,ApiConfig.EmailLogin);
                params.put(ApiConfig.POST_Email,email);
                params.put("password",password);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

    public static void createEmailUser(Context context,String email,String f_name,String l_name,String password,String img,FragmentCallBack callBack){

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("response",response);
                Bundle bundle = new Bundle();

                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equals("200")){
                        JSONObject user = resp.getJSONObject("msg");
                        Functions.Log_in_via_email(context,user);
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);


                    }else{
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                }

                callBack.onResponce(bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("error",error.getMessage());
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(ApiConfig.Request,ApiConfig.createEmailUser);
                params.put(ApiConfig.POST_Email,email);
                params.put("password",password);
                params.put(ApiConfig.Request_PostF_Name,f_name);
                params.put(ApiConfig.Request_PostL_Name,l_name);
                params.put(ApiConfig.Request_PostPic,img);
                params.put(ApiConfig.AUTH_ID,Functions.generateRandomU_Id());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);


    }

    public static void checkReviewForThisApp(Context context,String app_id,FragmentCallBack callBack){
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bundle bundle = new Bundle();
                try{
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equals("200")){
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                        bundle.putString(ApiConfig.Request_response,resp.getString("msg"));
                    }else
                    {
                        bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                        bundle.putString(ApiConfig.Request_response,resp.getString("msg"));
                    }


                }catch (Exception e){
                    e.printStackTrace();
                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    bundle.putString(ApiConfig.Request_response,response);
                }

                callBack.onResponce(bundle);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                bundle.putString(ApiConfig.Request_response,error.getMessage());

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(ApiConfig.Request,ApiConfig.check_myReview);
                params.put(ApiConfig.AUTH_ID,Functions.getSharedPreference(context).getString(ShearedPrefs.U_ID,""));
                params.put(ApiConfig.POST_App_Id,app_id);

                return params;

            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }


    public static void check_update(Context context,FragmentCallBack callBack){

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.Api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Bundle bundle = new Bundle();

                try{

                    JSONObject object = new JSONObject(response);

                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                    bundle.putString("version",object.getString("version"));
                    bundle.putString("url", object.getString("url"));


                }catch (Exception e){
                    e.printStackTrace();

                    bundle.putString(ApiConfig.Request_code,ApiConfig.RequestError);
                    bundle.putString(ApiConfig.Request_response,response);


                }
                callBack.onResponce(bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Bundle bundle = new Bundle();
                bundle.putString(ApiConfig.Request_code,ApiConfig.RequestSuccess);
                bundle.putString(ApiConfig.Request_response,error.getMessage());

                callBack.onResponce(bundle);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put(ApiConfig.Request,ApiConfig.check_review);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);

    }
}
