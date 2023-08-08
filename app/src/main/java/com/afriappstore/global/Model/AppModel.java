package com.afriappstore.global.Model;

import java.util.ArrayList;

public class AppModel {
    public  String id = "",app_name = "",app_icon = "",version = "",description = "",size = "",downloads = "",
            download_link = "",tags ="",rating = "",package_name = "",status = "",owner_id = "",created_at = "",
            category = "",long_description = "";

    public ArrayList<AppImageModel> app_images;
    public ArrayList<ReviewModel> Reviews;
    public RatingsModel Ratings;

}
