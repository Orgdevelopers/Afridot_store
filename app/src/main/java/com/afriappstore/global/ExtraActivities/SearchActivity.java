package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afriappstore.global.Adepters.SearchAdapter;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.ApiClasses.ApiRequests;
import com.afriappstore.global.ApiClasses.DataParsing;
import com.afriappstore.global.Interfaces.ApiCallback;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.Search_result_AppModel;
import com.afriappstore.global.R;
import com.afriappstore.global.SimpleClasses.Functions;
import com.afriappstore.global.SimpleClasses.Variables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    LinearLayout search_result_main, no_result_found;
    RecyclerView search_list;
    ProgressBar progressBar;
    ImageView search_button, back_button;
    EditText search_input;

    ArrayList<AppModel> dataList;
    SearchAdapter adapter;

    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().hide();


        //find views
        search_list = findViewById(R.id.search_list);
        progressBar = findViewById(R.id.progress_circular);
        search_result_main = findViewById(R.id.search_result_list_main_layout);
        no_result_found = findViewById(R.id.no_search_result_layout);
        search_button = findViewById(R.id.search_button);
        back_button = findViewById(R.id.search_back_button);
        search_input = findViewById(R.id.search_input);


        //init adapter
        dataList = new ArrayList<>();
        adapter = new SearchAdapter(this, dataList);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        search_list.setLayoutManager(manager);
        search_list.addItemDecoration(new DividerItemDecoration(search_list.getContext(), DividerItemDecoration.VERTICAL));

        search_list.setAdapter(adapter);

        search_input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search_button.performClick();
                    return true;
                }
                return false;
            }
        });

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                String keyword = text.toString().trim();
                if (keyword.equalsIgnoreCase("")) {
                    showSearchResults(true,false);
                    dataList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi(true);
                Functions.hideSoftKeyboard(SearchActivity.this);

            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            }
        });
    }

    private void callApi(boolean freshSearch) {
        if (freshSearch) {
            dataList.clear();
        }
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("keyword", search_input.getText().toString().trim());
            params.put("page", page + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequests.postRequest(this, ApiConfig.search, params, new ApiCallback() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject resp = new JSONObject(response);
                    if (resp.getString("code").equalsIgnoreCase("200")) {
                        JSONArray array = resp.getJSONArray("msg");
                        for (int i = 0; i < array.length(); i++) {
                            AppModel model = DataParsing.parseAppModel(array.getJSONObject(i));
                            if (model != null && !dataList.contains(model)) {
                                dataList.add(model);
                            }
                        }

                        if (dataList.size() > 0) {
                            showSearchResults(false, false);
                            adapter.notifyDataSetChanged();
                        } else {
                            showSearchResults(true, true);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                showSearchResults(true, true);

            }
        });

    }

    private void showSearchResults(boolean noResults,boolean toast){
        if (noResults){
            no_result_found.setVisibility(View.VISIBLE);
            search_result_main.setVisibility(View.GONE);
            if (toast){
                Toast.makeText(this, "no results found", Toast.LENGTH_SHORT).show();
            }
            //Functions.showSoftKeyboard(this,search_input);

        }else {
            no_result_found.setVisibility(View.GONE);
            search_result_main.setVisibility(View.VISIBLE);
        }
    }




}