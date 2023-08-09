package com.afriappstore.global.Adepters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.Model.CatAppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryApplistAdapter extends RecyclerView.Adapter<CategoryApplistAdapter.ViewHolder> {

    Context context;
    ArrayList<CatAppModel> list;
    Picasso picasso;


    public CategoryApplistAdapter(Context context,ArrayList<CatAppModel> datalist){
        this.context=context;
        this.list=datalist;

    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_category_apps,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            if (picasso==null){
                picasso=Picasso.get();
            }
            CatAppModel item = new CatAppModel();
            item=list.get(position);

            holder.app_name.setText(item.app_name);
            holder.app_size.setText(item.size+"MB");
            holder.downloads_count.setText(Functions.Format_numbers(Integer.parseInt(item.downloads)));

            if (!item.app_icon.contains("http")){
                item.app_icon= ApiConfig.Base_url+item.app_icon;
            }

            String icon=item.app_icon;
            picasso.load(item.app_icon).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    picasso.load(icon).into(holder.app_image);
                }

                @Override
                public void onError(Exception e) {

                }
            });

            holder.category_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, AppDetail.class);
                    intent.putExtra("app_id",list.get(holder.getAdapterPosition()).app_id);
                    context.startActivity(intent);


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

