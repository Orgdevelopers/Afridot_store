package com.afriappstore.global.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afriappstore.global.Adepters.Search_resultAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuisnessFragment extends Fragment {

    Context context;
    ArrayList<Search_result_AppModel> main_list,load_more_list;

    View view;
    int starting_point=0;
    Search_resultAdapter adapter;
    //here are some components
    LinearLayout main_layout,shimmer_layout,no_apps_found_layout;
    NestedScrollView scroll_layout;
    ProgressBar loading_bar_progress;
    RecyclerView app_list;

    public BuisnessFragment(Context context) {
        this.context=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_buisness, container, false);

        init_views();
        call_api_data();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init_views() {
        //
        main_layout=view.findViewById(R.id.main_layout);
        shimmer_layout=view.findViewById(R.id.shimmer_layout);
        no_apps_found_layout=view.findViewById(R.id.no_apps_found_layout);
        app_list=view.findViewById(R.id.business_app_list);
        scroll_layout=view.findViewById(R.id.idNestedSV);
        loading_bar_progress=view.findViewById(R.id.loading_progress_bar);

        scroll_layout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    loading_bar_progress.setVisibility(View.VISIBLE);
                    load_more();

                }
            }
        });

    }

    private void load_more() {
        ApiRequests.getallbuisnessapps(context,starting_point, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                //check response
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                    //data fetched successfully
                    load_more_list=null;
                    load_more_list=new ArrayList<>();
                    try {
                        JSONArray all_apps_json=new JSONArray(bundle.getString(ApiConfig.Request_response));

                        for (int i=0; i<all_apps_json.length();i++){
                            //get the json object from array
                            JSONObject app = all_apps_json.getJSONObject(i);

                            //initialize the model
                            Search_result_AppModel item = new Search_result_AppModel();

                            item.app_id=app.getString("id");
                            item.app_name=app.getString("name");
                            item.app_icon=app.getString("icon");
                            item.version=app.getString("version");
                            item.description=app.getString("desc");
                            item.size=app.getString("size");
                            item.downloads=app.getString("downloads");
                            item.download_link=app.getString("download_link");
                            item.tags=app.getString("tags");
                            item.rating=app.getString("rating");
                            item.package_name=app.getString("package");

                            load_more_list.add(item);
                            starting_point++;

                        }

                        adapter.Add_more(load_more_list);

                    }catch (Exception e){
                        e.printStackTrace();
                        //no_apps_found();
                        loading_bar_progress.setVisibility(View.GONE);
                        //Toast.makeText(context, "no more apps found", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    //no_apps_found();
                    Toast.makeText(context, "no more apps", Toast.LENGTH_SHORT).show();
                    loading_bar_progress.setVisibility(View.GONE);
                }

            }
        });
    }

    private void call_api_data() {
        ApiRequests.getallbuisnessapps(context,starting_point, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                //check response
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){
                 //data fetched successfully
                    main_list=new ArrayList<>();
                    try {
                        JSONArray all_apps_json=new JSONArray(bundle.getString(ApiConfig.Request_response));

                        for (int i=0; i<all_apps_json.length();i++){
                            //get the json object from array
                            JSONObject app = all_apps_json.getJSONObject(i);
                            //initialize the model
                            Search_result_AppModel item = new Search_result_AppModel();

                            item.app_id=app.getString("id");
                            item.app_name=app.getString("name");
                            item.app_icon=app.getString("icon");
                            item.version=app.getString("version");
                            item.description=app.getString("desc");
                            item.size=app.getString("size");
                            item.downloads=app.getString("downloads");
                            item.download_link=app.getString("download_link");
                            item.tags=app.getString("tags");
                            item.rating=app.getString("rating");
                            item.package_name=app.getString("package");

                            main_list.add(item);
                            starting_point++;
                        }

                        if (!main_list.isEmpty()){
                            //initialize the adapter
                            adapter = new Search_resultAdapter(context,main_list);
                            LinearLayoutManager manager = new LinearLayoutManager(context);

                            app_list.setLayoutManager(manager);
                            app_list.setAdapter(adapter);

                            main_layout.setVisibility(View.VISIBLE);
                            shimmer_layout.setVisibility(View.GONE);
                            no_apps_found_layout.setVisibility(View.GONE);

                        }else{
                            no_apps_found();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        no_apps_found();
                    }

                }else{
                    no_apps_found();
                }

            }
        });

    }

    private void no_apps_found() {
        no_apps_found_layout.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.GONE);
        shimmer_layout.setVisibility(View.GONE);

    }
}