package com.afriappstore.global.ExtraActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.afriappstore.global.ApiClasses.ApiConfig;
import com.afriappstore.global.BuildConfig;
import com.afriappstore.global.R;

import java.util.ArrayList;

public class AboutUs_A extends AppCompatActivity {

    ImageView about_us_back;
    TextView version_name_txt,website_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().hide();

        about_us_back=findViewById(R.id.about_us_back);
        version_name_txt=findViewById(R.id.version_name_txt);

        version_name_txt.setText(BuildConfig.VERSION_NAME);
        website_text=findViewById(R.id.website_text);


        about_us_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        });


        try {
            Link link = new Link("Website");
            link.setTextColor(getResources().getColor(R.color.dark_grey));
            link.setTextColorOfHighlightedLink(getResources().getColor(R.color.Login_tab_txt_color));
            link.setUnderlined(true);
            link.setBold(false);
            link.setHighlightAlpha(.20f);
            link.setOnClickListener(new Link.OnClickListener() {
                @Override
                public void onClick(String clickedText) {
                    //Toast.makeText(AboutUs_A.this, ""+clickedText, Toast.LENGTH_SHORT).show();
                    //openWebUrl(view.getContext().getString(R.string.terms_of_use), ApiConfig.Terms_url);

                    Intent it = new Intent();
                    it.setAction(Intent.ACTION_VIEW);
                    it.setData(Uri.parse(ApiConfig.Base_url));
                    startActivity(it);
                }
            });

            ArrayList<Link> links = new ArrayList<>();
            links.add(link);
            CharSequence sequence = LinkBuilder.from(AboutUs_A.this, website_text.getText().toString())
                    .addLinks(links)
                    .build();
            website_text.setText(sequence);
            website_text.setMovementMethod(TouchableMovementMethod.getInstance());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
        //super.onBackPressed();

    }
}