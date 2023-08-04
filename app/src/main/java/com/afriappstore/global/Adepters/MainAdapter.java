package com.afriappstore.global.Adepters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.SimpleClasses.Functions;
import com.airbnb.lottie.LottieAnimationView;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Variables;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {

    private  Context context;
    private LayoutInflater inflater;
    private PackageManager pm;
    ArrayList<AppModel> datalist = new ArrayList<>();

    public MainAdapter(Context c, ArrayList<AppModel> datalist ){
        context = c;
        this.datalist = datalist;
    }


    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (inflater==null) {
          inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item, null);

        }

        TextView app_name,app_size,app_free,main_downloads;
        LottieAnimationView loading;
        ImageView imageView = convertView.findViewById(R.id.image_view);
        app_name = convertView.findViewById(R.id.text_view);
        app_size=convertView.findViewById(R.id.app_size_text);
        app_free=convertView.findViewById(R.id.app_free_text);
        loading=convertView.findViewById(R.id.image_loading);
        main_downloads=convertView.findViewById(R.id.main_downloads);


        AppModel item = datalist.get(position);
        //app icon
        String image_uri=null;
        image_uri=item.app_icon;
        Uri uri = null;
        if (!image_uri.contains("http")){
            image_uri= ApiConfig.Base_url+image_uri;
        }
        uri=Uri.parse(image_uri);

        Picasso picasso=Picasso.get();
        assert picasso != null;
        //load image
        Uri finalUri = uri;
        picasso.load(uri).fetch(new Callback() {
            @Override
            public void onSuccess() {
                //imageView.setVisibility(View.VISIBLE);
                picasso.load(finalUri).into(imageView);
                loading.cancelAnimation();
                loading.setVisibility(View.GONE);

            }

            @Override
            public void onError(Exception e) {

            }
        });


        //app name
        String app_names=item.app_name;
        app_name.setText(app_names);

        //app size
        String appsize_s=item.size;
        app_size.setText(appsize_s+" Mb");

        //downloads
        try {
            main_downloads.setText(Functions.Format_numbers(Integer.parseInt(item.downloads)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String package_name = null;
        try {
            if (pm==null){
                pm=context.getPackageManager();
            }

            package_name = item.package_name.trim();
            boolean installed = isPackageInstalled(package_name,pm);
            if (installed){
                app_free.setText("INSTALLED");
            }else {
                app_free.setText("INSTALL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
        }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}


