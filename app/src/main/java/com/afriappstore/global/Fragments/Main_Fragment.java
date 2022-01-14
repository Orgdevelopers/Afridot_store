package com.afriappstore.global.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.afriappstore.global.Adepters.MainAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.AppDetail;
import com.afriappstore.global.MainActivity;
import com.afriappstore.global.R;


public class Main_Fragment extends Fragment {

    View view;
    GridView gridView;
    Context context;

    public Main_Fragment(Context context) {
        // Required empty public constructor
        this.context= context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_main_, container, false);


         gridView = view.findViewById(R.id.grid_view);
       MainAdapter adapter = new MainAdapter( context);
        gridView.setAdapter(adapter);
       gridView.setOnItemClickListener( new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               open_appDetails_byPosition(position);
            }
        });




        return view;
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
}