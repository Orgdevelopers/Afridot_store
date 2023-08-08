package com.afriappstore.global.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afriappstore.global.Adepters.Business_adapter;
import com.afriappstore.global.Adepters.MainAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.measurement.api.AppMeasurementSdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessFragment extends Fragment {
    MainAdapter adapter;
    GridView app_list;
    Context context;
    boolean is_api_running = false;
    ArrayList<Search_result_AppModel> load_more_list;
    ProgressBar loading_bar_progress;
    LinearLayout main_layout;
    ArrayList<Search_result_AppModel> main_list;
    LinearLayout no_apps_found_layout;
    LinearLayout shimmer_layout;

    int page_count = 0;
    ArrayList<AppModel> datalist = new ArrayList<>();
    View view;


    public BusinessFragment(Context context2) {
        this.context = context2;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_buisness, container, false);
        init_views();

        getAllapps();


        return this.view;
    }

    private void init_views() {
        this.main_layout = (LinearLayout) this.view.findViewById(R.id.main_layout);
        this.shimmer_layout = (LinearLayout) this.view.findViewById(R.id.shimmer_layout);
        this.no_apps_found_layout = (LinearLayout) this.view.findViewById(R.id.no_apps_found_layout);
        this.app_list = (GridView) this.view.findViewById(R.id.business_app_list);
        this.loading_bar_progress = (ProgressBar) this.view.findViewById(R.id.loading_progress_bar);
        this.app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView listView, int scrollState) {
                if (scrollState == 0 && listView.getLastVisiblePosition() >= listView.getCount() - 1) {
                    if (!BusinessFragment.this.is_api_running) {
                        BusinessFragment.this.loading_bar_progress.setVisibility(View.VISIBLE);
                        BusinessFragment.this.loadMore();
                        return;
                    }
                    Toast.makeText(BusinessFragment.this.context, "loading already in progress please wait", Toast.LENGTH_SHORT).show();
                }
            }
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        BusinessFragment.this.adapter = new MainAdapter(BusinessFragment.this.context, BusinessFragment.this.datalist);
        BusinessFragment.this.app_list.setAdapter(BusinessFragment.this.adapter);
        BusinessFragment.this.main_layout.setVisibility(View.VISIBLE);
        BusinessFragment.this.shimmer_layout.setVisibility(View.GONE);
        BusinessFragment.this.no_apps_found_layout.setVisibility(View.GONE);

        app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                openAppDetails(datalist.get(pos).id);
            }
        });
    }

    private void loadMore() {
        getAllapps();
    }

    public void openAppDetails(String app_id){
        Intent intent = new Intent(context, AppDetail.class);
        intent.putExtra("app_id",app_id);
        startActivity(intent);
        try {
            getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
//    public void load_more() {
//        ApiRequests.getallbuisnessapps(this.context, this.starting_point, new FragmentCallBack() {
//            public void onResponce(Bundle bundle) {
//                BusinessFragment.this.is_api_running = false;
//                BusinessFragment.this.loading_bar_progress.setVisibility(View.GONE);
//                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)) {
//                    BusinessFragment.this.load_more_list = null;
//                    BusinessFragment.this.load_more_list = new ArrayList<>();
//                    try {
//                        JSONArray all_apps_json = new JSONArray(bundle.getString(ApiConfig.Request_response));
//                        for (int i = 0; i < all_apps_json.length(); i++) {
//                            JSONObject app = all_apps_json.getJSONObject(i);
//                            Search_result_AppModel item = new Search_result_AppModel();
//                            item.app_id = app.getString("id");
//                            item.app_name = app.getString(AppMeasurementSdk.ConditionalUserProperty.NAME);
//                            item.app_icon = app.getString("icon");
//                            item.version = app.getString("version");
//                            item.description = app.getString("desc");
//                            item.size = app.getString("size");
//                            item.downloads = app.getString("downloads");
//                            item.download_link = app.getString("download_link");
//                            item.tags = app.getString("tags");
//                            item.rating = app.getString("rating");
//                            item.package_name = app.getString("package");
//                            BusinessFragment.this.load_more_list.add(item);
//                            BusinessFragment.this.starting_point++;
//                        }
//                        BusinessFragment.this.adapter.update_data(BusinessFragment.this.datalist);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        BusinessFragment.this.loading_bar_progress.setVisibility(View.GONE);
//                    }
//                } else {
//                    Toast.makeText(BusinessFragment.this.context, "no more apps", Toast.LENGTH_SHORT).show();
//                    BusinessFragment.this.loading_bar_progress.setVisibility(View.GONE);
//                }
//            }
//        });
//    }





//    private void call_api_data() {
//        ApiRequests.getallbuisnessapps(this.context, this.starting_point, new FragmentCallBack() {
//            public void onResponce(Bundle bundle) {
//                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)) {
//                    BusinessFragment.this.main_list = new ArrayList<>();
//                    try {
//                        JSONArray all_apps_json = new JSONArray(bundle.getString(ApiConfig.Request_response));
//                        for (int i = 0; i < all_apps_json.length(); i++) {
//                            JSONObject app = all_apps_json.getJSONObject(i);
//                            Search_result_AppModel item = new Search_result_AppModel();
//                            item.app_id = app.getString("id");
//                            item.app_name = app.getString(AppMeasurementSdk.ConditionalUserProperty.NAME);
//                            item.app_icon = app.getString("icon");
//                            item.version = app.getString("version");
//                            item.description = app.getString("desc");
//                            item.size = app.getString("size");
//                            item.downloads = app.getString("downloads");
//                            item.download_link = app.getString("download_link");
//                            item.tags = app.getString("tags");
//                            item.rating = app.getString("rating");
//                            item.package_name = app.getString("package");
//                            BusinessFragment.this.main_list.add(item);
//                            BusinessFragment.this.starting_point++;
//                        }
//                        if (!BusinessFragment.this.main_list.isEmpty()) {
//                            BusinessFragment.this.adapter = new Business_adapter(BusinessFragment.this.context, BusinessFragment.this.main_list);
////                            BusinessFragment.this.app_list.setAdapter(BusinessFragment.this.adapter);
////                            BusinessFragment.this.main_layout.setVisibility(View.VISIBLE);
////                            BusinessFragment.this.shimmer_layout.setVisibility(View.GONE);
////                            BusinessFragment.this.no_apps_found_layout.setVisibility(View.GONE);
//                            return;
//                        }
//                        BusinessFragment.this.no_apps_found();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        BusinessFragment.this.no_apps_found();
//                    }
//                } else {
//                    BusinessFragment.this.no_apps_found();
//                }
//            }
//        });
//    }

    public void getAllapps() {
        is_api_running = true;
        shimmer_layout.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.getAllAfriApps, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                shimmer_layout.setVisibility(View.GONE);
                is_api_running = false;

                Log.wtf("ollllllllllllllllllllllllllllllllllll", response + "end");
                Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
                if (!response.equals("")) {
                    try {

                        JSONObject resp = new JSONObject(response);
                        if (resp.getString("code").equalsIgnoreCase("200")){
                            JSONArray array = resp.getJSONArray("msg");
                            if (datalist==null){
                                datalist=new ArrayList<>();
                            }
                            for (int i  = 0; i <array.length() ; i++) {
                                AppModel item = DataParsing.parseAppModel(array.getJSONObject(i));
                                datalist.add(item);
                            }
                            page_count++;
                            updateData();

                        }else{
                            //no records found

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Functions.ShowToast(context, error.toString());
                is_api_running = false;
                shimmer_layout.setVisibility(View.GONE);
                Log.wtf("ollllllllllllllllllllllll okkkkkkk",error);

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("api_key","0");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }

    private void updateData() {
        if (!BusinessFragment.this.datalist.isEmpty()) {
            adapter.notifyDataSetChanged();
        }else{
            no_apps_found();
        }

    }


    /* access modifiers changed from: private */
    public void no_apps_found() {
        this.no_apps_found_layout.setVisibility(View.VISIBLE);
        this.main_layout.setVisibility(View.GONE);
        this.shimmer_layout.setVisibility(View.GONE);
    }
}