package com.afriappstore.global.Adepters;

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
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Search_resultAdapter extends RecyclerView.Adapter<Search_resultAdapter.ViewHolder> {

    ArrayList<Search_result_AppModel> main_list;
    Context context;
    Picasso picasso = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView appname,dow_count_text,app_size_text;
        ImageView app_icon;
        LottieAnimationView loading;
        LinearLayout search_app;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appname=itemView.findViewById(R.id.app_name_search_result);
            app_icon=itemView.findViewById(R.id.search_result_app_icon);
            loading=itemView.findViewById(R.id.image_loading);
            search_app=itemView.findViewById(R.id.search_app);
            dow_count_text=itemView.findViewById(R.id.downloads_count_txt);
            app_size_text=itemView.findViewById(R.id.app_size_txt);

        }
    }

    public Search_resultAdapter(Context context, ArrayList<Search_result_AppModel> start_list){
        this.context=context;
        //this.main_list=new ArrayList<>();
        this.main_list=start_list;
    }

    public void Search_results(ArrayList<Search_result_AppModel> search_items){
        this.main_list=null;
        this.main_list=search_items;

        notifyDataSetChanged();
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
            Search_result_AppModel item = main_list.get(position);
            holder.appname.setText(item.app_name);

            holder.app_size_text.setText(item.size+" MB");
            holder.dow_count_text.setText(Functions.Format_numbers(Integer.parseInt(item.downloads)));

            picasso.load(Uri.parse(item.app_icon)).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    picasso.load(Uri.parse(item.app_icon)).into(holder.app_icon);
                    holder.loading.setVisibility(View.GONE);
                    holder.loading.cancelAnimation();

                }

                @Override
                public void onError(Exception e) {
                    holder.loading.setVisibility(View.GONE);
                    holder.loading.cancelAnimation();
                    holder.app_icon.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_android_24));
                }
            });

            try {
                holder.search_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Search_result_AppModel model =main_list.get(holder.getPosition());
                        open_appDetails_byApp_id(Integer.parseInt(model.app_id));

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
        return main_list.size();
    }
    public void open_appDetails_byApp_id(int id) {
        int pos = Functions.convert_appid_to_pos(id);
        if (pos!=101){
            open_appDetails_byPosition(pos);
        }
    }

    public void open_appDetails_byPosition(int position) {
        Intent intent = new Intent(context, AppDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApiConfig.Request_code,"pos");
        intent.putExtra("pos",position);
        context.startActivity(intent);

    }


}
