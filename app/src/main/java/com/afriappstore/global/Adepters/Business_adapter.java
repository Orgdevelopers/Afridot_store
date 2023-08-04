package com.afriappstore.global.Adepters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Business_adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    ArrayList<AppModel> main_list;
    Picasso picasso = Picasso.get();

    /* renamed from: pm */
    private PackageManager f465pm;

    public Business_adapter(Context c, ArrayList<AppModel> main_list2) {
        this.main_list = main_list2;
        this.context = c;
    }

    public void update_data(ArrayList<AppModel> load_more_list) {
        this.main_list.addAll(load_more_list);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.main_list.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View convertView2;
        String image_uri;
        final int i = position;
        if (this.inflater == null) {
            this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView2 = this.inflater.inflate(R.layout.row_item, parent, false);
        } else {
            ViewGroup viewGroup = parent;
            convertView2 = convertView;
        }
        final ImageView imageView = (ImageView) convertView2.findViewById(R.id.image_view);
        TextView app_name = (TextView) convertView2.findViewById(R.id.text_view);
        TextView app_size = (TextView) convertView2.findViewById(R.id.app_size_text);
        TextView app_free = (TextView) convertView2.findViewById(R.id.app_free_text);
        final LottieAnimationView loading = (LottieAnimationView) convertView2.findViewById(R.id.image_loading);
        TextView main_downloads = (TextView) convertView2.findViewById(R.id.main_downloads);
        try {
            ((ConstraintLayout) convertView2.findViewById(R.id.download_layout)).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppModel item = this.main_list.get(i);
        try {
            String image_uri2 = item.app_icon;
            if (!image_uri2.contains("http")) {
                image_uri = ApiConfig.Base_url + image_uri2;
            } else {
                image_uri = image_uri2;
            }
            Uri uri = Uri.parse(image_uri);
            if (this.picasso == null) {
                this.picasso = Picasso.get();
            }
            try {
                imageView.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            final Uri finalUri = uri;
            String str = image_uri;
            this.picasso.load(uri).fetch(new Callback() {
                public void onSuccess() {
                    Business_adapter.this.picasso.load(finalUri).into(imageView);
                    loading.pauseAnimation();
                    loading.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                public void onError(Exception e) {
                }
            });
            String app_names = item.app_name;
            app_name.setText(app_names);
            String appsize_s = item.size;
            StringBuilder sb = new StringBuilder();
            String str2 = app_names;
            String app_names2 = appsize_s;
            sb.append(app_names2);
            String str3 = app_names2;
            sb.append(" Mb");
            app_size.setText(sb.toString());
            try {
                main_downloads.setText(Functions.Format_numbers(Integer.valueOf(Integer.parseInt(item.downloads))));
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            try {
                if (this.f465pm == null) {
                    this.f465pm = this.context.getPackageManager();
                }
                boolean installed = isPackageInstalled(item.package_name.trim(), this.f465pm);
                if (installed) {
                    boolean z = installed;
                    app_free.setText("INSTALLED");
                } else {
                    app_free.setText("INSTALL");
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        } catch (Exception e5) {
            e5.printStackTrace();
        }
        convertView2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Business_adapter business_adapter = Business_adapter.this;
                business_adapter.open_appDetails_byApp_id(business_adapter.main_list.get(i).id);
            }
        });
        return convertView2;
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void open_appDetails_byApp_id(String id) {
        Intent intent = new Intent(this.context, AppDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApiConfig.Request_code, "business");
        intent.putExtra(ApiConfig.POST_App_Id, id);
        this.context.startActivity(intent);
    }
}