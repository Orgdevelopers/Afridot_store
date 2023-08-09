package com.afriappstore.global.ApiClasses;

import android.os.health.PackageHealthStats;

import com.google.android.gms.common.api.Api;

public class ApiConfig {

    public static final String Base_url="https://afriappstore.com/";
    public static final String Api_url=Base_url+"api/";
    public static final String Terms_url="";
    public static final String Privacy_url="";
    public static final String APPUPLOADURI=Base_url+"uploads/apks/file_recive.php";
    public static final String APPIMAGEUPLOADURI=Base_url+"uploads/file_recive.php";
    public static final String APPICONUPLOADURI=Base_url+"uploads/logos/file_recive.php";

    //
    public static final String getAllApps = Api_url+"showAllApps";
    public static final String getAllAfriApps = Api_url+"showAllAfriApps";
    public static final  String getALlSliders = Api_url+"showAllSliders";
    public static final String showALlCategories = Api_url+"showAllCategories";
    public static final String showAppDetails = Api_url+"showAppDetails";
    public static final String Signup = Api_url+"Signup";
    public static final String Login=Api_url+"login";
    public static final String showAllReviews = Api_url+"showAllReviews";
    public static final String getCategoryapps=Api_url+"showCategoryApps";

    public static final String forgetPassword = Api_url+"forgetPassword";
    public static final String updatePassword = Api_url+"updatePassword";


    //
    public static final String Request="request";
    public static final String RequestSuccess="ok";
    public static final String RequestError="err";
    public static final String Request_code="code";
    public static final String Request_response="resp";
    public static final String Request_Phone_respType="type";
    public static final String Request_Phone_respTypeLogin="log_in";
    public static final String Request_Phone_respTypeSignup="sign_up";
    public static final String Request_parse_user="user";
    public static final String Request_PostPhone="phone";
    public static final String Request_PostF_Name="firstname";
    public static final String Request_PostL_Name="lastname";
    public static final String Request_UpdateProfile="Request_UpdateProfile";
    public static final String Request_GetShareUrl="Request_GetShareUrl";
    public static final String Request_PostReview="Request_PostReview";
    public static final String CHECK_SERVER_EMAIL="CHECK_SERVER_EMAIL";

    public static final String createEmailUser="createEmailUser";
    public static final String check_myReview="check_myReview";
    public static final String check_review="check_review";
    public static final String getallcategories="getallcategories";

    public static final String isverified="isverified";
    public static final String verifyEmail="verifyEmail";
    public static final String getallmyapps="getallmyapps";
    public static final String CHECK_PACKAGE_AVAILABILITY="checkpackageavailability";
    public static final String Rollout_my_app="rolloutmyapp";
    public static final String sendemailotp="sendemailotp";
    public static final String UPDATEPASSWORD="updatepassword";
    public static final String GETALLSLIDERS="getallsliders";
    public static final String GETALLBUISNESSAPPS="getallbuisnessapps";
    public  static  final String GETALLBIGSLIDERS="getallbigsliderssss";


    public static final String AUTH_ID="auth_id";

    public static final String Request_PostPic="pic";

    public static final String Request_createPhoneUser="createPhoneUser";




    public static final String POST_App_Id="app_id";
    public static final String POST_Email="email";


    //
    public static final String Request_getallapps="getallapps";
    public static final String Request_getappimages="getappimage";
    public static final String Request_getlongdescription="getappdescription";
    public static final String Request_get3reviews="get3views";
    public static final String Request_getreviewscount="getreviewscount";
    public static final String Request_getAllReviews="getAllReviews";

    public static final String Request_PhoneSignIn="PhoneSignIn";

}
