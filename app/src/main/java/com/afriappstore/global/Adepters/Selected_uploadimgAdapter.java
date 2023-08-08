package com.afriappstore.global.Adepters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.SelectedImageModel;
import com.afriappstore.global.R;

import java.util.ArrayList;

public class Selected_uploadimgAdapter extends RecyclerView.Adapter<Selected_uploadimgAdapter.MyViewHolder> {

    Context context;
    ArrayList<SelectedImageModel> datalist;
    FragmentCallBack callBack;

    public Selected_uploadimgAdapter(Context context, ArrayList<SelectedImageModel> datalist, FragmentCallBack callBack){
        this.context=context;
        this.datalist=datalist;
        this.callBack=callBack;

    }

    public void AddImage(SelectedImageModel model){
        this.datalist.add(model);
        notifyDataSetChanged();

    }

    public void UpdateImage(ArrayList<SelectedImageModel> datalist){
        this.datalist=null;
        this.datalist=datalist;
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_select_publishappimg,parent,false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        int count=1;
        if (!datalist.isEmpty()){
            count=count+datalist.size();
        }
        return count;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!datalist.isEmpty() && position<=(datalist.size()-1)){
            Bitmap bitmap = null;
            bitmap= BitmapFactory.decodeFile(datalist.get(position).path);
            holder.selected_img.setImageBitmap(bitmap);

        }else {
            holder.selected_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pos",String.valueOf(holder.getAdapterPosition()));
                    //Toast.makeText(context, ""+datalist.size()+"   "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    callBack.onResponse(bundle);
                }
            });
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView selected_img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            selected_img=itemView.findViewById(R.id.selected_img);
        }
    }

}
