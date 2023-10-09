package com.afriappstore.global.Adepters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Model.AppModel;
import com.airbnb.lottie.LottieAnimationView;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    ArrayList<AppModel> dataList;
    Activity context;
    Picasso picasso = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView app_image;
        TextView app_name,app_size,downloads_count;
        CardView category_app;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_app=itemView.findViewById(R.id.category_app);
            app_image=itemView.findViewById(R.id.app_image);
            app_name=itemView.findViewById(R.id.app_name_txt);
            app_size=itemView.findViewById(R.id.app_size);
            downloads_count=itemView.findViewById(R.id.app_downloads);

        }
    }

    public SearchAdapter(Activity context, ArrayList<AppModel> dataList){
        this.context=context;
        //this.main_list=new ArrayList<>();
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_search_result,null);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        picasso=Picasso.get();
        try {
            AppModel item = dataList.get(position);
            holder.app_name.setText(item.app_name);

            holder.app_size.setText(item.size+" MB");
            holder.downloads_count.setText(Functions.Format_numbers(Integer.parseInt(item.downloads)));

            if (!item.app_icon.contains("http")){
                item.app_icon=ApiConfig.Base_url+item.app_icon;
            }

            picasso.load(Uri.parse(item.app_icon)).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    picasso.load(Uri.parse(item.app_icon)).into(holder.app_image);

                }

                @Override
                public void onError(Exception e) {
                    holder.app_image.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_android_24));
                }
            });

            try {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppModel model = dataList.get(holder.getPosition());
                        openAppDetails(model.id);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public void openAppDetails(String app_id){
        Intent intent = new Intent(context, AppDetail.class);
        intent.putExtra("app_id",app_id);
        context.startActivity(intent);
        try {
            context.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
