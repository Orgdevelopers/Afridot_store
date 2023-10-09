package com.afriappstore.global.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afriappstore.global.Adepters.Categories_adapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.CategoriesModel;
import com.afriappstore.global.R;
import com.airbnb.lottie.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Categories_fragment extends Fragment {

    View view;
    Context context;
    RecyclerView list;
    ArrayList<CategoriesModel> datalist;

    public Categories_fragment(Context context) {
        // Required empty public constructor
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_categories_fragment, container, false);
        initviews();

        return view;
    }

    private void initviews() {
        list=view.findViewById(R.id.categories_list);

        ApiRequests.getAllCategories(context, new FragmentCallBack() {
            @Override
            public void onResponse(Bundle bundle) {
                datalist=new ArrayList<>();
                try {
                    if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                        JSONObject resp = new JSONObject(bundle.getString(ApiConfig.Request_response));
                        JSONArray categories = resp.getJSONArray("msg");

                        for (int i=0;i< categories.length();i++){
                            JSONObject singlecat=categories.getJSONObject(i);

                            CategoriesModel model = new CategoriesModel();

                            model.id=singlecat.getString("id");
                            model.name=singlecat.getString("name");
                            model.pic=singlecat.getString("pic");

                            if (!model.pic.contains("http")) {
                                model.pic = ApiConfig.S3Url+model.pic;
                            }

                            datalist.add(model);

                        }
                        if (datalist!=null && !datalist.isEmpty()){
                            LinearLayoutManager manager = new LinearLayoutManager(context);
                            Categories_adapter adapter = new Categories_adapter(context,datalist);

                            list.setLayoutManager(manager);
                            list.setAdapter(adapter);
                        }else {
                            Toast.makeText(context, "no categories on server", Toast.LENGTH_SHORT).show();
                        }


                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }
}