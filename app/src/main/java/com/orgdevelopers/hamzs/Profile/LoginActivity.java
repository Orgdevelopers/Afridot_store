package com.orgdevelopers.hamzs.Profile;


import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.orgdevelopers.hamzs.Interfaces.FragmentCallBack;
import com.orgdevelopers.hamzs.Profile.Fragments.Email_F;
import com.orgdevelopers.hamzs.Profile.Fragments.Phone_F;
import com.orgdevelopers.hamzs.Profile.SignUp.SignupActivity;
import com.orgdevelopers.hamzs.R;
import com.orgdevelopers.hamzs.SimpleClasses.Functions;

public class LoginActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager pager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        otp_layout=findViewById(R.id.otp_layout);
        phone_box=findViewById(R.id.phone_no_box);
        countryCodePicker=findViewById(R.id.country_code_picker);
        send_phone_otp_btn=findViewById(R.id.send_otp);
        phone_sign_in_layout=findViewById(R.id.phone_login_layout);
        otp_box=findViewById(R.id.otp_txt);
        check_otp=findViewById(R.id.check_otp);*/

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        pager = findViewById(R.id.login_pager);
        pager.setOffscreenPageLimit(3);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        setupTabIcons();


        /*check_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp= otp_box.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });*/

    }
    // this method will change the text and style tabs
    private void setupTabIcons() {

        View view1 = LayoutInflater.from(this).inflate(R.layout.item_tabs_signup, null);
        TextView text_history = view1.findViewById(R.id.text_history);
        text_history.setText("phone");
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(this).inflate(R.layout.item_tabs_signup, null);
        TextView text_history1 = view2.findViewById(R.id.text_history);
        text_history1.setText("email");
        tabLayout.getTabAt(1).setCustomView(view2);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView text_history = v.findViewById(R.id.text_history);

                switch (tab.getPosition()) {
                    case 0:
                        text_history.setTextColor(getResources().getColor(R.color.Login_tab_txt_color));
                        break;

                    case 1:
                        text_history.setTextColor(getResources().getColor(R.color.Login_tab_txt_color));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView text_history = v.findViewById(R.id.text_history);

                switch (tab.getPosition()) {
                    case 0:
                        text_history.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case 1:
                        text_history.setTextColor(getResources().getColor(R.color.black));
                        break;

                }

                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ViewPagerAdapter( FragmentManager fm) {
            super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new Phone_F(LoginActivity.this);
                    break;
                case 1:
                    result = new Email_F(LoginActivity.this);
                    break;

                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(final int position) {
            return null;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


    }

    @Override
    public void onBackPressed() {
        Functions.Showdouble_btn_alert(LoginActivity.this, "Do you really want to exit", "Your current login progress will be lost", "Go back", "Exit", true, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                String action = bundle.getString("action");
                if (action.equals("ok")){
                    finish();
                    overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

                }
            }
        });
    }
}
