package com.afriappstore.global.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afriappstore.global.Model.Review_Model;
import com.afriappstore.global.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class All_reviews_Adapter extends RecyclerView.Adapter<All_reviews_Adapter.ViewHolder>{
    ArrayList<Review_Model> list_data;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView pfp,star1,star2,star3,star4,star5;
        private TextView username,date,review;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pfp=itemView.findViewById(R.id.review_user_pfp);
            username=itemView.findViewById(R.id.review_username);
            date=itemView.findViewById(R.id.review_date);
            review=itemView.findViewById(R.id.detailed_review);
            star1=itemView.findViewById(R.id.review_star1);
            star2=itemView.findViewById(R.id.review_star2);
            star3=itemView.findViewById(R.id.review_star3);
            star4=itemView.findViewById(R.id.review_star4);
            star5=itemView.findViewById(R.id.review_star5);

        }
    }

    public All_reviews_Adapter(Context context, ArrayList<Review_Model> data){
        this.context=context;
        this.list_data=data;
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
        Review_Model item= list_data.get(position);

        try{
            if (item.image!=null && item.image!=""){
                Picasso picasso = Picasso.get();
                picasso.load(item.image).into(holder.pfp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        holder.username.setText(item.username);
        holder.date.setText(item.date);
        holder.review.setText(item.review);

        String st= item.stars;
        if (st.equals("1")){
            holder.star2.setColorFilter(R.color.ultra_light_grey);
            holder.star3.setColorFilter(R.color.ultra_light_grey);
            holder.star4.setColorFilter(R.color.ultra_light_grey);
            holder.star5.setColorFilter(R.color.ultra_light_grey);

        }else if (st.equals("2")){
            holder.star3.setColorFilter(R.color.ultra_light_grey);
            holder.star4.setColorFilter(R.color.ultra_light_grey);
            holder.star5.setColorFilter(R.color.ultra_light_grey);

        }else if (st.equals("3")){
            holder.star4.setColorFilter(R.color.ultra_light_grey);
            holder.star5.setColorFilter(R.color.ultra_light_grey);

        }else if (st.equals("4")){

            holder.star5.setColorFilter(R.color.ultra_light_grey);

        }else if (st.equals("5")){
           //

        }
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }


}
