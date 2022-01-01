package com.orgdevelopers.hamzs;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.orgdevelopers.hamzs.Adepters.MainAdapter;
import com.orgdevelopers.hamzs.ApiClasses.ApiConfig;
import com.orgdevelopers.hamzs.ApiClasses.ApiRequests;
import com.orgdevelopers.hamzs.ExtraActivities.AboutUs_A;
import com.orgdevelopers.hamzs.ExtraActivities.SearchActivity;
import com.orgdevelopers.hamzs.Interfaces.FragmentCallBack;
import com.orgdevelopers.hamzs.Profile.LoginActivity;
import com.orgdevelopers.hamzs.Profile.ProfileA;
import com.orgdevelopers.hamzs.Profile.SignUp.SignupActivity;
import com.orgdevelopers.hamzs.SimpleClasses.Functions;
import com.orgdevelopers.hamzs.SimpleClasses.ShearedPrefs;
import com.orgdevelopers.hamzs.SimpleClasses.Variables;

import java.io.File;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public int backpress=0;
    public ImageView side_menu_btn,search_btn,about_us_button;
    CountDownTimer back_timer,login_timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Variables.array==null){
            try {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this,SplashActivity.class));
                        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },11);
                    }
                },100);
            }catch (Exception e){
                e.printStackTrace();


            }
            return;
        }

        gridView = findViewById(R.id.grid_view);
       MainAdapter adapter = new MainAdapter(MainActivity.this);
        gridView.setAdapter(adapter);
       gridView.setOnItemClickListener( new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open_appDetails_byPosition(position);
            }
        });

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
            MenuItem exit = menu.getItem(3);

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

            settings.setOnMenuItemClickListener(listener);
            exit.setOnMenuItemClickListener(listener);


        }catch (Exception e){
            e.printStackTrace();
        }

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
        drawerLayout.closeDrawer(Gravity.START,true);
    }
    private void Open_drawer(){
        drawerLayout.openDrawer(Gravity.START,true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setNavigation();
    }
}
