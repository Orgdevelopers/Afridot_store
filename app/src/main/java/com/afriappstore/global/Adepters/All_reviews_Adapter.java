package com.afriappstore.global.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.Model.ReviewModel;
import com.afriappstore.global.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class All_reviews_Adapter extends RecyclerView.Adapter<All_reviews_Adapter.ViewHolder>{
    ArrayList<ReviewModel> dataList;
    Context context;
    Picasso picasso;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView pfp;
        private RatingBar ratingBar;
        private TextView username,date,review;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pfp=itemView.findViewById(R.id.review_user_pfp);
            username=itemView.findViewById(R.id.review_username);
            date=itemView.findViewById(R.id.review_date);
            review=itemView.findViewById(R.id.detailed_review);
            ratingBar = itemView.findViewById(R.id.review_user_rating_bar);

        }
    }

    public All_reviews_Adapter(Context context, ArrayList<ReviewModel> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_all_reviews,parent,false);
        All_reviews_Adapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel item = dataList.get(position);

        if (picasso == null){
            picasso = Picasso.get();
        }

        holder.username.setText(item.user.first_name+" "+ item.user.last_name);
        holder.date.setText(item.created_at);
        holder.review.setText(item.review);
        holder.ratingBar.setRating(Float.parseFloat(item.stars));

        picasso.load(item.user.profile_pic).fetch(new Callback() {
            @Override
            public void onSuccess() {
                picasso.load(item.user.profile_pic).into(holder.pfp);
            }

            @Override
            public void onError(Exception e) {
                holder.pfp.setImageDrawable(context.getDrawable(R.drawable.ic_user_icon));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
