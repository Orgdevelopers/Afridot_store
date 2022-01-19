package com.afriappstore.global.Adepters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ExtraActivities.CategoryApps;
import com.afriappstore.global.Model.CategoriesModel;
import com.afriappstore.global.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Categories_adapter extends RecyclerView.Adapter<Categories_adapter.MyViewholder> {

    Context context;
    ArrayList<CategoriesModel> list;

    public Categories_adapter(Context context,ArrayList<CategoriesModel> datalist){
        this.context=context;
        this.list=datalist;

    }

    public class MyViewholder extends RecyclerView.ViewHolder{
        ImageView cat_img;
        TextView cat_name;
        LinearLayout cat_bg;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            cat_img=itemView.findViewById(R.id.cat_image);
            cat_name=itemView.findViewById(R.id.cat_name);
            cat_bg=itemView.findViewById(R.id.cat_bg);

        }
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_categorylist,parent,false);
        MyViewholder holder = new MyViewholder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {

        Picasso picasso = Picasso.get();
        CategoriesModel item = list.get(position);

        holder.cat_name.setText(item.name);
        if (!item.pic.contains("http")){
            item.pic= ApiConfig.Base_url+item.pic;
        }

        picasso.load(item.pic).fetch(new Callback() {
            @Override
            public void onSuccess() {

                picasso.load(item.pic).into(holder.cat_img);
            }

            @Override
            public void onError(Exception e) {
                Log.wtf("eror",e.getMessage());

            }
        });

        holder.cat_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryApps.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id",list.get(holder.getAdapterPosition()).id);
                intent.putExtra("name",list.get(holder.getAdapterPosition()).name);
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    

}
