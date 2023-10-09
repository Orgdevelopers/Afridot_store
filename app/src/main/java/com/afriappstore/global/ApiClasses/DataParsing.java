package com.afriappstore.global.ApiClasses;

import com.afriappstore.global.Model.AppImageModel;
import com.afriappstore.global.Model.AppModel;
import com.afriappstore.global.Model.CatAppModel;
import com.afriappstore.global.Model.RatingsModel;
import com.afriappstore.global.Model.ReviewModel;
import com.afriappstore.global.Model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataParsing {

    public static AppModel parseAppModel (JSONObject object){
        AppModel item = new AppModel();

        try {
            item.id = object.getString("id");
            item.app_name = object.getString("app_name");
            item.app_icon = object.getString("app_icon");

            //
            if (!item.app_icon.contains("http")){
                item.app_icon = ApiConfig.S3Url+item.app_icon;
            }
            //

            item.version = object.getString("version");
            item.description = object.getString("description");
            item.size = object.getString("size");
            item.downloads = object.getString("downloads");
            item.download_link = object.getString("download_link");

            if (!item.download_link.contains("http")){
                item.download_link = ApiConfig.S3Url+item.download_link;
            }

            item.tags = object.getString("tags");
            item.rating = object.getString("rating");
            item.package_name = object.getString("package_name");
            item.status = object.getString("status");
            item.owner_id = object.getString("owner_id");
            item.created_at = object.getString("created_at");
            item.category = object.getString("category");

            if (object.has("long_description")){
                item.long_description = object.getJSONObject("long_description").getString("description");
            }else{
                item.long_description = "";
            }

            JSONArray app_images_array = object.optJSONArray("app_images");
            ArrayList<AppImageModel> app_images = new ArrayList<>();

            if (app_images_array!=null){
                for (int i = 0; i < app_images_array.length(); i++) {

                    JSONObject app_image_object = app_images_array.getJSONObject(i);
                    AppImageModel model = DataParsing.parseAppImageModel(app_image_object);

                    if (model!=null){
                        app_images.add(model);

                    }

                }
            }

            item.app_images = app_images;


            JSONArray reviews_array = object.optJSONArray("Reviews");
            ArrayList<ReviewModel> reviews = new ArrayList<>();

            if (reviews_array!=null){
                for (int i = 0; i < reviews_array.length(); i++) {
                    ReviewModel model = DataParsing.parseReviewModel(reviews_array.getJSONObject(i));
                    if (model!=null){
                        reviews.add(model);
                    }
                }
            }
            item.Reviews = reviews;

            JSONObject ratings_object = object.optJSONObject("reviews_count");
            RatingsModel model = new RatingsModel();

            if (ratings_object!=null){

                model.star1 = ratings_object.getInt("1star");
                model.star2 = ratings_object.getInt("2star");
                model.star3 = ratings_object.getInt("3star");
                model.star4 = ratings_object.getInt("4star");
                model.star5 = ratings_object.getInt("5star");

                model.total = ratings_object.getInt("total");
                model.rating = ratings_object.getString("actual_rating");

            }

            item.Ratings = model;


            //user review
            if (object.has("user_review")){
                item.user_review = DataParsing.parseReviewModel(object.getJSONObject("user_review"));
            }

        } catch (Exception e) {
            item = null;
            e.printStackTrace();
        }


        return item;
    }

    public static ReviewModel parseReviewModel(JSONObject review){
        ReviewModel item  = new ReviewModel();

        try {
            item.id = review.getString("id");
            item.app_id = review.getString("app_id");
            item.stars = review.getString("stars");
            item.review = review.getString("review");
            item.created_at = review.getString("created_at");
            item.user_id = review.getString("user_id");

            UserModel user = DataParsing.parseUserModel(review.getJSONObject("user"));

            item.user = user;

        } catch (Exception e) {
            e.printStackTrace();
            item = null;
        }

        return item;
    }

    public static AppImageModel parseAppImageModel(JSONObject app_image){
        AppImageModel item = new AppImageModel();
        try {
            item.id = app_image.getString("id");
            item.app_id = app_image.getString("app_id");
            item.url = app_image.getString("url");

            if (!item.url.contains("http")){
                item.url = ApiConfig.S3Url+item.url;
            }

        } catch (Exception e) {
            e.printStackTrace();
            item = null;
        }

        return item;
    }

    public static UserModel parseUserModel (JSONObject object){
        UserModel item = new UserModel();

        try {
            item.id = object.optString("id");
            item.first_name = object.optString("first_name");
            item.last_name = object.optString("last_name");
            item.email = object.optString("email");
            item.phone = object.optString("phone");
            item.password = object.optString("password");
            item.verified = object.optString("verified");
            item.profile_pic = object.optString("profile_pic");

            if (!item.profile_pic.contains("http")){
                item.profile_pic = ApiConfig.S3Url+item.profile_pic;
            }

            item.type = object.optString("type");
            item.auth_id = object.optString("auth_id");
            item.created_at = object.optString("created_at");

        } catch (Exception e) {
            item = null;
            e.printStackTrace();
        }


        return item;
    }
    public static CatAppModel parseCatApp(JSONObject app){
        CatAppModel item = new CatAppModel();

        try {
            item=new CatAppModel();
            item.app_id=app.getString("id");
            item.app_name=app.getString("app_name");
            item.app_icon=app.getString("app_icon");
            item.version=app.getString("version");
            item.description=app.getString("description");
            item.size=app.getString("size");
            item.downloads=app.getString("downloads");
            item.download_link=app.getString("download_link");
            item.tags=app.getString("tags");
            item.rating=app.getString("rating");
            item.package_name=app.getString("package_name");
            item.created = app.getString("created_at");
            item.category = app.getString("category");
            item.is_afri_app = app.getString("is_afri_app");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }


}
