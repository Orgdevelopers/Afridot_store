package com.afriappstore.global.ApiClasses;

import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.UserModel;

import org.json.JSONObject;

public class DataParsing {

    public static AppModel parseAppModel (JSONObject object){
        AppModel item = new AppModel();

        try {
            item.id = object.getString("id");
            item.app_name = object.getString("app_name");
            item.app_icon = object.getString("app_icon");
            item.version = object.getString("version");
            item.description = object.getString("description");
            item.size = object.getString("size");
            item.downloads = object.getString("downloads");
            item.download_link = object.getString("download_link");
            item.tags = object.getString("tags");
            item.rating = object.getString("rating");
            item.package_name = object.getString("package_name");
            item.status = object.getString("status");
            item.owner_id = object.getString("owner_id");
            item.created_at = object.getString("created_at");
            item.category = object.getString("category");
            item.long_description = object.optString("long_description");
            item.App_Images = object.optString("app_images");
            item.Reviews = object.optString("Reviews");

        } catch (Exception e) {
            item = null;
            e.printStackTrace();
        }


        return item;
    }

    public static UserModel parseUserModel (JSONObject object){
        UserModel item = new UserModel();

        try {
            item.id = object.getString("id");
            item.first_name = object.getString("app_name");
            item.last_name = object.getString("app_icon");
            item.email = object.getString("version");
            item.phone = object.getString("description");
            item.password = object.getString("size");
            item.verified = object.getString("downloads");
            item.profile_pic = object.getString("download_link");
            item.type = object.getString("tags");
            item.auth_id = object.getString("rating");
            item.created_at = object.getString("package_name");

        } catch (Exception e) {
            item = null;
            e.printStackTrace();
        }


        return item;
    }


}
