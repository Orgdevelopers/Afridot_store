package com.afriappstore.global.Adepters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Model.AppImageModel;
import com.airbnb.lottie.LottieAnimationView;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AppSlideradapter extends RecyclerView.Adapter<AppSlideradapter.ViewHolder> {
    Context context;
    ArrayList<AppImageModel> dataList;
    public Picasso picasso = Picasso.get();


    public AppSlideradapter(Context context,ArrayList<AppImageModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private LottieAnimationView loading;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            loading=itemView.findViewById(R.id.image_loading);
            imageView=itemView.findViewById(R.id.app_slider_img);
        }
    }


    @NonNull
    @Override
    public AppSlideradapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_app_image,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppSlideradapter.ViewHolder holder, int position) {
        if (picasso==null){
            picasso = Picasso.get();
        }

        try{
            picasso.load(Uri.parse(dataList.get(position).url)).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    holder.loading.cancelAnimation();
                    holder.loading.setVisibility(View.GONE);
                    picasso.load(Uri.parse(dataList.get(holder.getAdapterPosition()).url)).into(holder.imageView);
                }

                @Override
                public void onError(Exception e) {
                    holder.loading.cancelAnimation();
                    holder.loading.setVisibility(View.GONE);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.showBigImage(context,holder.imageView.getDrawable(),"");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}