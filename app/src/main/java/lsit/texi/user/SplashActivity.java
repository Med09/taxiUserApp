package lsit.texi.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.android.volley.Request;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lsit.texi.user.common.CallBackInterface;
import lsit.texi.user.common.CallWebService;
import lsit.texi.user.gpsLocation.GPSTracker;
import lsit.texi.user.utils.Common;

import lsit.texi.user.utils.RegistrationIntentService;
import lsit.texi.user.utils.Url;

import static lsit.texi.user.utils.Common.isNetworkAvailable;
import static lsit.texi.user.utils.Common.showInternetInfo;

public class SplashActivity extends AppCompatActivity {

    ImageView img_splash_screen;
    ImageView img_location;

    double PickupLongtude;
    double PickupLatitude;
    SharedPreferences userPref;

    Common common = new Common();

    TranslateAnimation translation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img_splash_screen = (ImageView)findViewById(R.id.img_splash_screen);
        img_location = (ImageView)findViewById(R.id.img_location);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        GPSTracker gpsTracker = new GPSTracker(this);
        PickupLatitude = gpsTracker.getLatitude();
        PickupLongtude = gpsTracker.getLongitude();
        userPref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);

        Picasso.with(SplashActivity.this)
                .load(R.drawable.logo)
                .into(img_splash_screen);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        //boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        Log.d("gps_enabled", "gps_enabled = " + gps_enabled);
        if(!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setTitle("Improve location accurancy?");
            dialog.setMessage("This app wants to change your device setting:");
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, 1);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }else{


            translation = new TranslateAnimation(0f, 0F, 0f, getDisplayHeight()*0.50f);
            translation.setStartOffset(500);
            translation.setDuration(2000);
            translation.setFillAfter(true);

            translation.setInterpolator(new BounceInterpolator());
            img_location.startAnimation(translation);

                    if(userPref.getString("isLogin","").equals("1")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (isNetworkAvailable(SplashActivity.this)) {

                                    if(!userPref.getString("facebook_id", "").equals("") || !userPref.getString("twitter_id", "").equals("")){
                                        Log.d("facebook id","facebook id = "+userPref.getString("facebook_id", ""));

                                        String SocialUrl = "";
                                        if(!userPref.getString("facebook_id", "").equals("")) {
                                                SocialUrl = Url.facebookLoginUrl;
                                        }else if(!userPref.getString("twitter_id", "").equals("")) {
                                            SocialUrl = Url.twitterLoginUrl;
                                        }

                                        new Common.LoginSocialUserHttp(SocialUrl,userPref.getString("facebook_id", ""),userPref.getString("twitter_id", ""),SplashActivity.this).execute();
                                    }else {
                                        String loginUrl = null;
                                        try {
                                            loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(userPref.getString("email", ""), "UTF-8") + "&password=" + userPref.getString("password", "");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                        Log.d("loginUrl", "loginUrl " + loginUrl);
                                        checkLogin();
                                       // new Common.LoginCallHttp(SplashActivity.this, null, null, userPref.getString("password", ""), "SplashScreen", loginUrl).execute();
                                    }
                                } else {
                                    showInternetInfo(SplashActivity.this, "");
                               }

                            }
                        }, 100);

                    }else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, LoginOptionActivity.class));
                                finish();
                            }
                        }, 2500);
                    }
                }
            }

    public int getDisplayHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if (isNetworkAvailable(SplashActivity.this)) {

                String loginUrl = null;
                try {
                    loginUrl = Url.loginUrl+"?email="+ URLEncoder.encode(userPref.getString("email", ""), "UTF-8")+"&password="+userPref.getString("password", "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("loginUrl", "loginUrl " + loginUrl);
                new Common.LoginCallHttp(SplashActivity.this, null, null, userPref.getString("password", ""), "SplashScreen", loginUrl).execute();
            } else {
                showInternetInfo(SplashActivity.this, "");
            }
        }
    }


    void checkLogin(){
        CallWebService.getInstance(SplashActivity.this,true).hitJSONObjectVolleyWebServiceforPost(Request.Method.POST, Url.loginUrl, addJsonObjects(), false, new CallBackInterface() {
            @Override
            public void onJsonObjectSuccess(JSONObject object) {
                Log.d("Registration Response: ",""+object.toString());
                try {
                    onSignupSuccess(object);
                } catch (NullPointerException e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onJsonArrarSuccess(JSONArray array) {
                Log.d("Contacts List: ",""+array.toString());
            }

            @Override
            public void onFailure(String str) {

                Log.e("failure: ",""+str);
            }
        });
    }
    public void onSignupSuccess(JSONObject resObj) {

        boolean isStatus = true;//Common.ShowHttpErrorMessage(activity,result);
        Log.d("LoginUrl", "LoginUrl result= " + resObj+"=="+isStatus);
        if(isStatus) {
            try {
                Log.d("loginUrl", "loginUrl two= " + resObj);


                if (resObj.getString("status").equals("success")) {

                    JSONArray cabDtlAry = new JSONArray(resObj.getString("cabDetails"));
                    Common.CabDetail = cabDtlAry;

                    /*set Start Currency*/

                    JSONArray currencyArray = new JSONArray(resObj.getString("country_detail"));
                    for (int ci = 0; ci < currencyArray.length(); ci++) {
                        JSONObject startEndTimeObj = currencyArray.getJSONObject(ci);
                        Common.Currency = startEndTimeObj.getString("currency");
                        Common.Country = startEndTimeObj.getString("country");
                    }

                    /*set Start And End Time*/
                    JSONArray startEndTimeArray = new JSONArray(resObj.getString("time_detail"));
                    for (int si = 0; si < startEndTimeArray.length(); si++) {
                        JSONObject startEndTimeObj = startEndTimeArray.getJSONObject(si);
                        Common.StartDayTime = startEndTimeObj.getString("day_start_time");
                        Common.EndDayTime = startEndTimeObj.getString("day_end_time");
                    }

                    /*User Detail*/
                    JSONObject userDetilObj = new JSONObject(resObj.getString("userdetail"));

                    SharedPreferences.Editor id = userPref.edit();
                    id.putString("id", userDetilObj.getString("id").toString());
                    id.commit();

                    SharedPreferences.Editor name = userPref.edit();
                    name.putString("name", userDetilObj.getString("name").toString());
                    name.commit();

                    SharedPreferences.Editor passwordPre = userPref.edit();
                    passwordPre.putString("password", userPref.getString("password",""));
                    passwordPre.commit();

                    SharedPreferences.Editor username = userPref.edit();
                    username.putString("username", userDetilObj.getString("username").toString());
                    username.commit();

                    SharedPreferences.Editor mobile = userPref.edit();
                    mobile.putString("mobile", userDetilObj.getString("mobile").toString());
                    mobile.commit();

                    SharedPreferences.Editor email = userPref.edit();
                    email.putString("email", userDetilObj.getString("email").toString());
                    email.commit();

                    SharedPreferences.Editor isLogin = userPref.edit();
                    isLogin.putString("isLogin", "1");
                    isLogin.commit();

                    SharedPreferences.Editor userImage = userPref.edit();
                    userImage.putString("userImage", userDetilObj.getString("image").toString());
                    userImage.commit();

                    SharedPreferences.Editor dob = userPref.edit();
                    dob.putString("date_of_birth", userDetilObj.getString("dob").toString());
                    dob.commit();

                    SharedPreferences.Editor gender = userPref.edit();
                    gender.putString("gender", userDetilObj.getString("gender").toString());
                    gender.commit();

//                        if (!activityName.equals("SplashScreen")) {
//                            Common.showMkSucess(activity, resObj.getString("message"),"no");
//                        }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent hi = new Intent(SplashActivity.this, HomeActivity.class);
                            hi.putExtra("PickupLatitude", PickupLatitude);
                            hi.putExtra("PickupLongtude", PickupLongtude);
                            startActivity(hi);
                            finish();
                        }
                    }, 2000);
                } else if (resObj.getString("status").equals("failed")) {
                    Common.LoginMkError(this, resObj.getString("error code"), resObj.getString("code"));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent hi = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(hi);
                                finish();
                            }
                        }, 2000);

                } else if (resObj.getString("status").equals("false")) {
                    Log.d("Result", "Result failed" + resObj.getString("status"));
                    if (resObj.getString("Isactive").equals("Inactive")) {

                        //Common.showLoginRegisterMkError(activity, resObj.getString("message"));
                        Common.user_InActive = 1;
                        Common.InActive_msg = resObj.getString("message");
                        //if (activityName.equals("SplashScreen")) {
                        SharedPreferences.Editor editor = userPref.edit();
                        editor.clear();
                        editor.commit();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent logInt = new Intent(SplashActivity.this, LoginOptionActivity.class);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(logInt);
                            }
                        }, 500);
                        //}
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // loginRequest(userdata.getEmail(),"1234",userdata.getLoginType());

    }
    private JSONObject addJsonObjects() {
        try {
/*"email":"krs123@gmail.com",
	"password":"123456",
	"username" :"krs123"*/
            JSONObject packet = new JSONObject();
            packet.put("email", "krs123@gmail.com");//userPref.getString("email",""));
            packet.put("password", "123456");//userPref.getString("password",""));
            packet.put("username","krs123");



            return packet;
        } catch (Exception e) {
            Log.e("Exception: ",""+e.getLocalizedMessage());
            return null;
        }
    }





}
