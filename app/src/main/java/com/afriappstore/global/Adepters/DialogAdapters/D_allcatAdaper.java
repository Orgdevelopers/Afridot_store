package com.afriappstore.global.Adepters.DialogAdapters;

import android.content.Context;
import android.os.Bundle;
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
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.CategoriesModel;
import com.afriappstore.global.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class D_allcatAdaper extends RecyclerView.Adapter<D_allcatAdaper.DViewHolder> {

    Context context;
    ArrayList<CategoriesModel> main_list;
    FragmentCallBack callBack;

    public D_allcatAdaper(Context context, ArrayList<CategoriesModel> datalist,FragmentCallBack callBack){
        this.context=context;
        this.main_list=datalist;
        this.callBack=callBack;

    }

    @NonNull
    @Override
    public DViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_categorylist,parent,false);
        DViewHolder holder =new DViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DViewHolder holder, int position) {

        Picasso picasso = Picasso.get();
        CategoriesModel item = main_list.get(position);

        try{
            holder.cat_name.setText(item.name);

            if (!item.pic.contains("http")){
                item.pic=(ApiConfig.Base_url+item.pic);
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
                    Bundle bundle = new Bundle();
                    bundle.putString("id",main_list.get(holder.getAdapterPosition()).id);
                    bundle.putString("name",main_list.get(holder.getAdapterPosition()).name);
                    callBack.onResponce(bundle);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return main_list.size();
    }

    public class DViewHolder extends RecyclerView.ViewHolder{
        ImageView cat_img;
        TextView cat_name;
        LinearLayout cat_bg;
        public DViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_img=itemView.findViewById(R.id.cat_image);
            cat_name=itemView.findViewById(R.id.cat_name);
            cat_bg=itemView.findViewById(R.id.cat_bg);
        }
    }
}
