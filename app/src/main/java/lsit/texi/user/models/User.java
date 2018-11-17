package lsit.texi.user.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vikram on 1/4/18.
 */

public class User {
    private String name;
    private String email;
    public static String USER_DEVICE_TOKEN = "DeviceToken";

    public String getName() {
        return sharedpreferences.getString("name", name);
    }

    public void setName(String name) {
        //this.name = name;
        editor.putString("name", name);
        editor.apply();
    }

    public String getEmail() {
        return sharedpreferences.getString("email", email);
    }

    public void setEmail(String email) {
       // this.email = email;
        editor.putString("email", email);
        editor.apply();
    }

    public String getMobile() {

        return sharedpreferences.getString("mobile", mobile);
    }

    public void setMobile(String mobile) {
        //this.mobile = mobile;
        editor.putString("mobile", mobile);
        editor.apply();
    }

    private String mobile;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedpreferences;

    public User(Context context){
        String MyPREFERENCES = "TaxiUserPreference" ;

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void setUserDeviceToken(String token){
        editor.putString("DeviceToken", token);
        editor.apply();
    }

    public String getUserDeviceToken(){
        return sharedpreferences.getString("DeviceToken",null);
    }


}
