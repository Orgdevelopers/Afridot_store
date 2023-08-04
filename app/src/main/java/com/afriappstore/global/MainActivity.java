package com.afriappstore.global;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.ExtraActivities.PublishApps;
import com.afriappstore.global.ExtraActivities.VerifyEmail;
import com.afriappstore.global.Fragments.BusinessFragment;
import com.afriappstore.global.Fragments.Categories_fragment;
import com.afriappstore.global.Fragments.Main_Fragment;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.SliderModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ExtraActivities.AboutUs_A;
import com.afriappstore.global.ExtraActivities.SearchActivity;
import com.afriappstore.global.Interfaces.FragmentCallBack;
import com.afriappstore.global.Profile.LoginActivity;
import com.afriappstore.global.Profile.ProfileA;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.ShearedPrefs;
import com.afriappstore.global.SimpleClasses.Variables;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public int backpress=0,banner_count=0;
    public ImageView side_menu_btn,search_btn,about_us_button;
    CountDownTimer back_timer,main_pager_timer;
    ViewPager pager,slider_pager;
    ViewPagerAdapter adapter;
    TabLayout tabs;


    Picasso picasso;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Functions.make_app_dirs();
        picasso = Picasso.get();




        /*gridView = findViewById(R.id.grid_view);
       MainAdapter adapter = new MainAdapter(MainActivity.this);
        gridView.setAdapter(adapter);
       gridView.setOnItemClickListener( new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open_appDetails_byPosition(position);
            }
        });

         */

        //init the paper db
        Paper.init(this);

        //check permission
        //check_permission();

        //slider pager
        slider_pager=findViewById(R.id.main_slider_pager);
        getAllbigsliders();

        tabs=findViewById(R.id.tabs);
        pager=findViewById(R.id.main_pager);
        pager.setOffscreenPageLimit(3);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
        setupTabIcons();



        drawerLayout = findViewById(R.id.my_drawer_layout);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        navigationView=findViewById(R.id.navigation_view);
        setNavigation();

        // to make the Navigation drawer icon always appear on the action bar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        side_menu_btn=findViewById(R.id.side_menu_button);
        search_btn=findViewById(R.id.search_button);
        about_us_button=findViewById(R.id.about_us_button);

        about_us_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent about_us=new Intent(MainActivity.this, AboutUs_A.class);
                startActivity(about_us);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                //Toast.makeText(MainActivity.this, "This Feature is still in development", Toast.LENGTH_SHORT).show();

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(search);
                //Toast.makeText(MainActivity.this, "This Feature is still in development", Toast.LENGTH_SHORT).show();
            }
        });
        side_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Open_drawer();
            }
        });

        //check intent deeplink
        try {
            Intent it = getIntent();
            if (it!=null && !it.getStringExtra(ApiConfig.POST_App_Id).equals(""))
            {
                String data=it.getStringExtra(ApiConfig.POST_App_Id);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        open_appDetails_byApp_id(Integer.parseInt(data));
                    }

                },200);
            }

        }catch (Exception e )
        {
            e.printStackTrace();
        }

        check_update();

    }

    private void getAllbigsliders() {
        ApiRequests.getAllbigslider(MainActivity.this, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getString(ApiConfig.Request_code).equals(ApiConfig.RequestSuccess)){

                    try {
                    JSONArray array = new JSONArray(bundle.getString(ApiConfig.Request_response));
                    ArrayList<SliderModel> dataList = new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        JSONObject single = array.getJSONObject(i);
                        SliderModel item = new SliderModel();
                        item.img=single.getString("image");
                        item.url=single.getString("url");

                        dataList.add(item);

                    }

                    SliderPagerAdapter adapter = new SliderPagerAdapter(MainActivity.this,dataList);
                    slider_pager.setAdapter(adapter);

                    main_pager_timer=new CountDownTimer(3000,1000) {
                        @Override
                        public void onTick(long l) {


                        }

                        @Override
                        public void onFinish() {
                            main_pager_timer.cancel();
                            main_pager_timer.start();
                            if (banner_count<adapter.getCount()-1){
                                banner_count++;
                                slider_pager.setCurrentItem(banner_count,true);
                            }else{
                                banner_count=0;
                                slider_pager.setCurrentItem(banner_count,true);

                            }

                        }
                    };

                    main_pager_timer.start();

                }catch (Exception e){
                    e.printStackTrace();
                }

                }

            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void check_permission() {
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Functions.Showdouble_btn_alert(MainActivity.this, "Permission alert",
                        "these permissions required for App's functionality",
                        "Cancel", "Continue", true, new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getString("action").equals("ok")){
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);

                        }

                    }
                });

            }
        },1000);
        }
    }

    private void check_update() {
        ApiRequests.check_update(this, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                String code = bundle.getString(ApiConfig.Request_code);
                if (code.equals(ApiConfig.RequestSuccess)){

                    try {
                        Float server_v=Float.valueOf(bundle.getString("version"));
                        Float my_ver = Float.valueOf(BuildConfig.VERSION_NAME);

                        String subheader = " Current version: "+my_ver+"\n Latest version: "+server_v;

                        if (server_v>my_ver){
                            Functions.Showdouble_btn_alert(MainActivity.this, "New update available", subheader, "cancel", "Update", false, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    if (bundle.getString("action").equals("ok")){

                                        Toast.makeText(MainActivity.this, "update", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }else{
                    //
                    // Toast.makeText(MainActivity.this, ""+bundle.getString(ApiConfig.Request_response), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void open_appDetails_byApp_id(int id) {
        int pos = Functions.convert_appid_to_pos(id);
        if (pos!=101){
            open_appDetails_byPosition(pos);
        }
    }

    public void open_appDetails_byPosition(int position) {
        Intent intent = new Intent(MainActivity.this,AppDetail.class);
        intent.putExtra(ApiConfig.Request_code,"pos");
        intent.putExtra("pos",position);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Toast.makeText(MainActivity.this,item.getItemId()+"b",Toast.LENGTH_SHORT).show();
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (backpress==0){
            backpress=1;
            Toast.makeText(MainActivity.this,"press again to exit",Toast.LENGTH_SHORT).show();
             back_timer= new CountDownTimer(3000,500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    backpress=0;
                }
            };
            back_timer.start();
        }else if (backpress==1){
            try {
                if (back_timer!=null){
                    back_timer.cancel();
                    back_timer=null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finish();
            super.onBackPressed();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (back_timer!=null){
                back_timer.cancel();
                back_timer=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setNavigation(){
        try {
            Menu menu = navigationView.getMenu();
            MenuItem prof=menu.getItem(0);
            MenuItem logout_in = menu.getItem(1);
            MenuItem settings = menu.getItem(2);
            MenuItem publish_your_app=menu.getItem(3);
            MenuItem exit = menu.getItem(4);

            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem==prof){
                    open_profile();
                    close_drawer();

                }else if (menuItem==logout_in){
                    close_drawer();
                    if (!Functions.is_Login(MainActivity.this)){
                        logIn();
                    }else{
                        logOut();
                    }
                }else if (menuItem==settings){ //settings is share button
                    close_drawer();
                    String link = Functions.getSharedPreference(MainActivity.this).getString(ShearedPrefs.AppShareUrl,"");
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_TEXT,link);
                    startActivity(i);
                    //Toast.makeText(MainActivity.this, "this function is still in development", Toast.LENGTH_SHORT).show();
                }else if (menuItem==exit){
                    close_drawer();
                    Functions.Showdouble_btn_alert(MainActivity.this, "Do you really want to Exit","", "Cancel", "Exit", true, new FragmentCallBack() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if (bundle.getString("action").equals("ok")){
                                finish();
                            }
                        }
                    });
                }else if (menuItem==publish_your_app){
                    if (Functions.is_Login(MainActivity.this)){
                        if (Functions.getSharedPreference(MainActivity.this).getString(ShearedPrefs.SIGN_IN_TYPE,"not").equals(ShearedPrefs.SIGN_IN_TYPE_EMAIL)){
                            close_drawer();
                            Functions.showLoader(MainActivity.this);
                            Functions.isVerified(MainActivity.this, new FragmentCallBack() {
                                @Override
                                public void onResponce(Bundle bundle) {
                                    Functions.cancelLoader();
                                    if (Variables.is_verify){
                                        Intent intent = new Intent(MainActivity.this, PublishApps.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

                                    }else{
                                        Functions.Showdouble_btn_alert(MainActivity.this, "It seems your email is not verified", "you need to verify your email first", "cancel", "Verify", true, new FragmentCallBack() {
                                            @Override
                                            public void onResponce(Bundle bundle) {
                                                if (bundle.getString("action").equals("ok")){
                                                    Intent intent = new Intent(MainActivity.this, VerifyEmail.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

                                                }

                                            }
                                        });

                                    }

                                }
                            });

                        }else {
                            Toast.makeText(MainActivity.this, "you need to log in via email in order to use this feature", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        close_drawer();
                        Toast.makeText(MainActivity.this, "you need to log in via email in order to use this feature", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        };

            prof.setOnMenuItemClickListener(listener);

            if (Functions.is_Login(MainActivity.this)){
                logout_in.setTitle("Log Out");
                logout_in.setIcon(MainActivity.this.getResources().getDrawable(R.drawable.ic_logout));
            }else{
                logout_in.setTitle("Log In");
                logout_in.setIcon(MainActivity.this.getResources().getDrawable(R.drawable.ic_login));
            }
            logout_in.setOnMenuItemClickListener(listener);

            publish_your_app.setOnMenuItemClickListener(listener);
            settings.setOnMenuItemClickListener(listener);
            exit.setOnMenuItemClickListener(listener);


        }catch (Exception e){
            e.printStackTrace();
        }

        Functions.isVerified(this, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {

            }
        });

    }

    private void logOut(){
        Functions.Showdouble_btn_alert(MainActivity.this, "Are you sure you want to logout?", "", "Cancel", "Logout", true, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                String act= bundle.getString("action");
                if (act.equals("ok")){
                    Functions.Log_Out(MainActivity.this);

                    try {

                        setNavigation();
                        Toast.makeText(MainActivity.this, "successfully Logged Out", Toast.LENGTH_SHORT).show();
                        /*
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this,SplashActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },50);
                            }
                        },10);*/
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void logIn(){
        Intent i= new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }
    private void open_profile() {
        if (Functions.is_Login(MainActivity.this)){
            Intent profile_intent = new Intent(MainActivity.this, ProfileA.class);
            startActivity(profile_intent);
            overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        }else {
            Functions.Showdouble_btn_alert(MainActivity.this, "You are not Logged in", "log in to see your profile and enjoy other benefits", "Cancel", "Log in", true, new FragmentCallBack() {
                @Override
                public void onResponce(Bundle bundle) {
                    if (bundle.getString("action").equals("ok")){
                        logIn();
                    }
                }
            });
        }

    }

    private void close_drawer(){
        drawerLayout.closeDrawer(GravityCompat.START,true);
    }
    private void Open_drawer(){
        drawerLayout.openDrawer(GravityCompat.START,true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setNavigation();
    }


    private void setupTabIcons() {

        View view1 = LayoutInflater.from(this).inflate(R.layout.item_tabs_signup, null);
        TextView text_history = view1.findViewById(R.id.text_history);
        text_history.setText("Categories");
        tabs.getTabAt(2).setCustomView(view1);

        View view2 = LayoutInflater.from(this).inflate(R.layout.item_tabs_signup, null);
        TextView text_history1 = view2.findViewById(R.id.text_history);
        text_history1.setText("Afri Apps");
        tabs.getTabAt(1).setCustomView(view2);

        View view3 = LayoutInflater.from(this).inflate(R.layout.item_tabs_signup, null);
        TextView text_history2 = view3.findViewById(R.id.text_history);
        text_history2.setText("Business Reach");
        tabs.getTabAt(0).setCustomView(view3);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView text_history = v.findViewById(R.id.text_history);

                switch (tab.getPosition()) {
                    case 2:
                        text_history.setTextColor(getResources().getColor(R.color.Login_tab_txt_color));
                        break;

                    case 1:
                        text_history.setTextColor(getResources().getColor(R.color.Login_tab_txt_color));
                        break;

                    case 0:
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
                    case 2:
                        text_history.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case 1:
                        text_history.setTextColor(getResources().getColor(R.color.black));
                        break;

                    case 0:
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
                case 2:
                    result = new Categories_fragment(MainActivity.this);
                    break;
                case 1:
                    result = new Main_Fragment(MainActivity.this);
                    break;

                case 0:
                    result = new BusinessFragment(MainActivity.this);
                    break;

                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 3;
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







    class SliderPagerAdapter extends PagerAdapter {

        Context context;
        ArrayList<SliderModel> main_list;

        public SliderPagerAdapter(Context context, ArrayList<SliderModel> dataList){
            this.context=context;
            this.main_list=dataList;

        }


        @Override
        public int getCount() {
            return main_list.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_big_slider,container,false);
            ImageView img = view.findViewById(R.id.big_slider_img);

            if(picasso==null){
                picasso=Picasso.get();
            }

            picasso.load(main_list.get(position).img).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    picasso.load(main_list.get(position).img).into(img);
                }

                @Override
                public void onError(Exception e) {
                    Log.wtf("picasso error: ",e);
                    img.setImageDrawable(getResources().getDrawable(R.drawable.ic_banner));
                }
            });

            container.addView(view);

            return view;

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }
    }

}
