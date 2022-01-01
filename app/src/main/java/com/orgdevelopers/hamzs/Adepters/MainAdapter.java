package com.orgdevelopers.hamzs.Adepters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.orgdevelopers.hamzs.R;
import com.orgdevelopers.hamzs.SimpleClasses.Variables;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class MainAdapter extends BaseAdapter {

    private  Context context;
    private LayoutInflater inflater;
    private PackageManager pm;

    public MainAdapter(Context c){
        context = c;
    }


    @Override
    public int getCount() {
        return Variables.array.length();
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

        TextView app_name,app_size,app_free;
        LottieAnimationView loading;
        ImageView imageView = convertView.findViewById(R.id.image_view);
        app_name = convertView.findViewById(R.id.text_view);
        app_size=convertView.findViewById(R.id.app_size_text);
        app_free=convertView.findViewById(R.id.app_free_text);
        loading=convertView.findViewById(R.id.image_loading);

        try {
            //app icon
            String image_uri=null;
            image_uri=Variables.array.getJSONObject(position).getString("icon");
            Uri uri = null;
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
            String app_names=Variables.array.getJSONObject(position).getString("name");
            app_name.setText(app_names);

            //app size
            String appsize_s=Variables.array.getJSONObject(position).getString("size");
            app_size.setText(appsize_s+" Mb");

            String package_name = null;
            try {
                if (pm==null){
                    pm=context.getPackageManager();
                }

                package_name = Variables.array.getJSONObject(position).getString("package").trim();
                boolean installed = isPackageInstalled(package_name,pm);
                if (installed){
                    app_free.setText("INSTALLED");
                }else {
                    app_free.setText("INSTALL");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



        } catch (JSONException e) {
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

