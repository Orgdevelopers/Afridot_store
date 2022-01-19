package com.afriappstore.global.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.Model.MyappModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

public class MyappsAdapter extends RecyclerView.Adapter<MyappsAdapter.MyHolder> {

    Context context;
    ArrayList<MyappModel> list;
    Picasso picasso;

    public MyappsAdapter(Context context, ArrayList<MyappModel> datalist){

        this.context =context;
        this.list=null ;
        this.list = datalist;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_myapps_layout,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        try {
            MyappModel item = null;
            item= list.get(position);

            holder.app_name.setText(item.app_name);
            if (item.status.equals("0")){
                //in review
                holder.app_status.setText("in review..");
                holder.app_status.setTextColor(context.getResources().getColor(R.color.dark_grey));

            }else if (item.status.equals("1")){
                //live
                holder.app_status.setText("live");
                holder.app_status.setTextColor(context.getResources().getColor(R.color.green));

            }else if (item.status.equals("2")){
                //unpublished
                holder.app_status.setText("unpublished");
                holder.app_status.setTextColor(context.getResources().getColor(R.color.gainsbro));


            }else if (item.status.equals("3")){
                //suspended
                holder.app_status.setText("suspended");
                holder.app_status.setTextColor(context.getResources().getColor(R.color.warningred));

            }else {
                //error
                holder.app_status.setText("error"+item.status);
            }

            if (picasso==null){
                picasso=Picasso.get();
            }


            if (!item.app_icon.contains("http")){
                item.app_icon= ApiConfig.Base_url+item.app_icon;
            }
            String appicon=item.app_icon;

            holder.image_loading.playAnimation();
            picasso.load(appicon).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    picasso.load(appicon).into(holder.app_icon);
                    holder.image_loading.cancelAnimation();
                    holder.image_loading.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.image_loading.cancelAnimation();
                    holder.image_loading.setVisibility(View.GONE);
                }
            });

            holder.download_count.setText(Functions.Format_numbers(Integer.parseInt(item.downloads)));


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        ImageView app_icon;
        TextView app_name,download_count,app_status;
        LottieAnimationView image_loading;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            app_icon=itemView.findViewById(R.id.app_icon_myapps);
            app_name=itemView.findViewById(R.id.app_name_myapps);
            download_count=itemView.findViewById(R.id.downloads_count_txt);
            app_status=itemView.findViewById(R.id.app_status_txt);
            image_loading=itemView.findViewById(R.id.image_loading);

        }
    }
}
