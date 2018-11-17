package lsit.texi.user.utils;

import android.app.Application;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import lsit.texi.user.common.CallBackInterface;
import lsit.texi.user.common.UserPreference;

/**
 * Created by Vikram on 08/07/18.
 */
public class ApplicationGlobal extends Application {
    static ApplicationGlobal singletonInstance;
    public static final String TAG = ApplicationGlobal.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static Context context;
    private UserPreference userPreference;

    CallBackInterface callBackinerface;

    @Override
    public void onCreate() {

        super.onCreate();
        TwitterAuthConfig authConfig =  new TwitterAuthConfig("qARyLKqVjnbJ69aDKuCsiOBfo", "bJGtMyN2v0DXqBJPt5AHQkdSUglsRLwBhxhv2Nto4aiP5hY1VD");
        Fabric.with(this, new Twitter(authConfig));

        singletonInstance = this;
        context = getApplicationContext();

        callBackinerface = new CallBackInterface() {
            @Override
            public void onJsonObjectSuccess(JSONObject object) {

            }

            @Override
            public void onJsonArrarSuccess(JSONArray array) {

            }

            @Override
            public void onFailure(String str) {

            }
        };
        userPreference = new UserPreference(getApplicationContext());


    }




    synchronized public static ApplicationGlobal getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new ApplicationGlobal();
        }

        return singletonInstance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public UserPreference getPreference() {
        return userPreference;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

}
