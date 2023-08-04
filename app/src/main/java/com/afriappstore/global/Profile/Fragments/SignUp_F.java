package com.afriappstore.global.Profile.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afriappstore.global.R;

public class SignUp_F extends Fragment {

    View view;
   Context context;
    public SignUp_F(Context context) {
        // Required empty public constructor
        this.context = context;
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        return view;

    }
}