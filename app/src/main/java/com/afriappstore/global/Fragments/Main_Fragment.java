package com.afriappstore.global.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.afriappstore.global.Adepters.MainAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.MainActivity;
import com.afriappstore.global.Model.SliderModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Main_Fragment extends Fragment {

    View view;
    GridView gridView;
    LinearLayout slider_layout;
    ShimmerFrameLayout slider_shimmer;
    ViewPager slider_pager;
    Context context;
    CountDownTimer slider_autochange_timer;
    ArrayList<SliderModel> slider_list;
    SliderAdapter slider_adapter;
    int item = 0;

    public Main_Fragment(Context context) {
        // Required empty public constructor
        this.context= context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_main_, container, false);

        slider_layout=view.findViewById(R.id.slider_layout);
        slider_shimmer=view.findViewById(R.id.slider_shimmer);

        slider_layout.setVisibility(View.GONE);
        slider_shimmer.setVisibility(View.VISIBLE);

        slider_pager=view.findViewById(R.id.main_slider_pager);
         gridView = view.findViewById(R.id.grid_view);
       MainAdapter adapter = new MainAdapter(context);
        gridView.setAdapter(adapter);
       gridView.setOnItemClickListener( new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               open_appDetails_byPosition(position);
            }
        });


       setDynamicHeight(gridView);
       setUpSlider();

        return view;
    }

    private void setDynamicHeight(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > 3 ){
            x = items/3;
            rows = (int) (x);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    private void setUpSlider() {
        ApiRequests.getSlider(context, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                //
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                    try {
                        JSONArray array = new JSONArray(bundle.getString(ApiConfig.Request_response));
                        slider_list=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            JSONObject row = array.getJSONObject(i);
                            SliderModel model =null;
                            model=new SliderModel();

                            model.img=row.getString("img");
                            model.title=row.getString("title");
                            model.url=row.getString("url");

                            slider_list.add(model);

                        }

                        //Toast.makeText(context, ""+slider_list.size(), Toast.LENGTH_SHORT).show();
                        slider_adapter = new SliderAdapter(context,slider_list);
                        slider_pager.setAdapter(slider_adapter);

                        close_shimer();
                        start_automatic_changing();

                    }catch (Exception e){
                        e.printStackTrace();
                        close_slider();
                    }

                }else{
                    close_slider();
                }

            }
        });



    }

    private void start_automatic_changing() {

        slider_pager.setCurrentItem(0,true);
        slider_autochange_timer=new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                slider_autochange_timer.cancel();
                slider_autochange_timer.start();

                try {
                    if (item<slider_adapter.getCount()-1){
                        item++;
                        slider_pager.setCurrentItem(item);

                    }else{
                        item=0;
                        slider_pager.setCurrentItem(item);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        slider_autochange_timer.start();
    }

    private void close_shimer() {
        slider_shimmer.setVisibility(View.GONE);
        slider_layout.setVisibility(View.VISIBLE);
    }

    private void close_slider() {
        slider_shimmer.setVisibility(View.GONE);
        slider_layout.setVisibility(View.GONE);
    }

    public void open_appDetails_byPosition(int position) {
        Intent intent = new Intent(context, AppDetail.class);
        intent.putExtra(ApiConfig.Request_code,"pos");
        intent.putExtra("pos",position);
        startActivity(intent);
        try {
            getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    //adapter class

    class SliderAdapter extends PagerAdapter {

        Context context;
        ArrayList<SliderModel> list;
        Picasso picasso;

        public SliderAdapter(Context context,ArrayList<SliderModel> data){
            this.context=context;
            this.list=data;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup root, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_main_slider,root,false);

            SliderModel item = list.get(position);

            final ImageView image=view.findViewById(R.id.main_slider_img);
            if (picasso==null){
                picasso=Picasso.get();
            }
            picasso.load(item.img).into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent it = new Intent();
                        it.setAction(Intent.ACTION_VIEW);
                        it.setData(Uri.parse(item.url));
                        startActivity(it);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            root.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)  object);
        }

    }

}