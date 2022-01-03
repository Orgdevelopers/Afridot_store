package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afriappstore.global.Adepters.Search_resultAdapter;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Variables;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    LinearLayout search_result_main,no_result_found;
    RecyclerView search_list;
    ImageView search_button,back_button;
    EditText search_input;

    ArrayList<Search_result_AppModel> main_list,search_array;
    Search_resultAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();



        //find views
        search_list=findViewById(R.id.search_list);
        search_result_main=findViewById(R.id.search_result_list_main_layout);
        no_result_found=findViewById(R.id.no_search_result_layout);
        search_button=findViewById(R.id.search_button);
        back_button=findViewById(R.id.search_back_button);
        search_input=findViewById(R.id.search_input);


        try {
            main_list=new ArrayList<>();
            for (int i=0;i< Variables.array.length();i++){
                Search_result_AppModel item = null;
                item=new Search_result_AppModel();
                JSONObject app = Variables.array.getJSONObject(i);
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
            }
            adapter = new Search_resultAdapter(this,main_list);
            create_list();

        }catch (Exception e){
            e.printStackTrace();
        }

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                //Toast.makeText(SearchActivity.this, ""+text, Toast.LENGTH_SHORT).show();
                search_array=null;
                search_array=new ArrayList<>();
                for (int p=0;p<main_list.size();p++){
                    Search_result_AppModel item=null;
                    item = main_list.get(p);

                    if (item.app_name.toLowerCase().contains(text.toString().toLowerCase())){
                        search_array.add(item);
                    }
                }

                if (search_array.isEmpty()){
                    search_result_main.setVisibility(View.INVISIBLE);
                    no_result_found.setVisibility(View.VISIBLE);
                }else {
                    adapter.Search_results(search_array);

                    if (!(search_result_main.getVisibility()==View.VISIBLE)){
                        search_result_main.setVisibility(View.VISIBLE);
                        no_result_found.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);

            }
        });
    }

    private void create_list(){
        //Toast.makeText(this, "list "+main_list.size(), Toast.LENGTH_SHORT).show();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        search_list.setLayoutManager(manager);
        search_list.setAdapter(adapter);

    }


}