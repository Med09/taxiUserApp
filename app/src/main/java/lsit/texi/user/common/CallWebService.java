package lsit.texi.user.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lsit.texi.user.R;
import lsit.texi.user.interfaces.IOneBtnDialogListener;
import lsit.texi.user.utils.Constants;
import lsit.texi.user.utils.ApplicationGlobal;
import lsit.texi.user.views.CustomDialog;

/**
 * Class for hitting api with custom requirments
 */
public class CallWebService {
    private ApplicationGlobal globalInstance;

    private Context context = null;
    private static CallWebService instance = null;
    private UserPreference userPreference;
    private static boolean isProgressbarRequired;
    private static boolean isShowNetworkDialog;
    private CustomDialog busyDialogue;

    public static CallWebService getInstance(Context context, boolean showProgressBar) {

        if (instance == null) {
            instance = new CallWebService();
        }
        instance.context = context;
        isProgressbarRequired = showProgressBar;
        return instance;
    }
    /*public static CallWebService getInstance(Context context, boolean showProgressBar, boolean showNetworkdialog) {

        if (instance == null) {
            instance = new CallWebService();
        }
        instance.context = context;
        isProgressbarRequired = showProgressBar;
        isShowNetworkDialog = showNetworkdialog;
        return instance;
    }*/
    public void hitJSONObjectVolleyWebServiceForGet(int requestType, String url, HashMap<String, String> json, final CallBackInterface callBackinerface) {
        JSONObject jbj = null;
        JSONObject userObject = new JSONObject();
        try {
            userObject.put(Constants.RequestResponseKeys.users, new JSONObject(json));
            jbj = new JSONObject(json);
        }catch (JSONException e){
            Log.e("JSONExceprion:", e.getLocalizedMessage());
        }

      //  Log.d("jobj", jbj.toString());
        busyDialogue = new CustomDialog(context);
        if (isProgressbarRequired) {
            busyDialogue.displayUiBlockingDialog();
        }

        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : (jbj), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    busyDialogue.dismiss();
                    if (response.getBoolean(Constants.STATUS)== true) {
                        callBackinerface.onJsonObjectSuccess(response);
                    } else {

                            callBackinerface.onFailure(response.getJSONArray("error").getJSONObject(0).optString(Constants.MESSAGE));
                    }
                } catch (final JSONException e) {
                    busyDialogue.dismiss();
                    callBackinerface.onFailure(e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Log.e("IllegalArgumentExcep:",""+e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                busyDialogue.dismiss();
                    callBackinerface.onFailure("Network not available. Please try again !");

            }
        }

        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = ApplicationGlobal.getInstance().getPreference().getStringData("accesstoken");
                if(token!= null)
                    params.put("token", token);
                    params.put("ContentType", "Application/json");
                return params;
            }
        };

        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }

    public void loadImageVolleyApi(String url, final ImageView imageView, final int placeHolder){
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(placeHolder);
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }
    public void hitJSONObjectVolleyWebServiceforPost(int requestType, String url, HashMap<String, String> json, final CallBackInterface callBackinerface) {

        Log.i("params web", json.toString());
        Gson gson = new Gson();
        String json1 = gson.toJson(json.toString());

        JsonObject jsonObject = (new JsonParser()).parse(json.toString()).getAsJsonObject();

        String json2 = jsonObject.toString();


        JSONObject jobj  = null;
        try {
            jobj = new JSONObject();
            jobj.put(Constants.RequestResponseKeys.users, json2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        busyDialogue = new CustomDialog(context);
        if (isProgressbarRequired) {
            busyDialogue.displayUiBlockingDialog();
        }
//        if(jobj!=null)
//            Log.i("jobj", jobj.toString());

        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : jobj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    busyDialogue.dismiss();
                    if (response.getString(Constants.ERROR).equals("1")) {
                        callBackinerface.onJsonObjectSuccess(response);
                    } else {

                        callBackinerface.onFailure(response.optString(Constants.MESSAGE));
                    }
                } catch (final JSONException e) {
                    busyDialogue.dismiss();
                    callBackinerface.onFailure(e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Log.e("IllegalArgumentExcep:",""+e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                busyDialogue.dismiss();
                callBackinerface.onFailure(error.getMessage());

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String token = ApplicationGlobal.getInstance().getPreference().getStringData("accesstoken");
                if(token!= null)
                  params.put("token", token);
                params.put("ContentType", "Application/json");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }
    public void hitJSONObjectVolleyWebServiceforPost(int requestType, String url, JSONObject json, final CallBackInterface callBackinerface) {
        busyDialogue = new CustomDialog(context);
        if (isProgressbarRequired) {
            busyDialogue.displayUiBlockingDialog();
        }
        Log.i("params web", json.toString());
        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    busyDialogue.dismiss();
                    if(response.has(Constants.STATUS)) {
                        if (response.getBoolean(Constants.STATUS)) {
                            callBackinerface.onJsonObjectSuccess(response);
                        } else {
                            callBackinerface.onFailure(response.optString(Constants.MESSAGE));
                        }
                    }
                    if(response.has(Constants.SUCCESS)){
                        if (response.getBoolean(Constants.SUCCESS)) {
                            callBackinerface.onJsonObjectSuccess(response);
                        } else {
                            callBackinerface.onFailure(response.optString(Constants.MSG));
                        }
                    }
                } catch (final JSONException e) {
                    busyDialogue.dismiss();
                    callBackinerface.onFailure(e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Log.e("IllegalArgumentExcep:",""+e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                busyDialogue.dismiss();
                callBackinerface.onFailure(error.getMessage());
                busyDialogue.displaySingleButtonDailog(context.getResources().getString(R.string.network_error), "Ok", new IOneBtnDialogListener() {
                    @Override
                    public void onPositiveClickListener(CustomDialog dialogInstance) {
                        dialogInstance.dismiss();
                    }
                });

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //String token = ApplicationGlobal.getInstance().getPreference().getStringData("accesstoken");
                //if(token!= null)
                   // params.put("token", token);
               // params.put("Content-Type", "text/html");

                params.put("Content-Type", "Application/json");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }
    public void hitJSONObjectVolleyWebServiceforPost(int requestType, String url, JSONObject json, final Boolean isHeaderRequired, final CallBackInterface callBackinerface) {
        userPreference = ApplicationGlobal.getInstance().getPreference();
        Log.i("params web", json.toString());
        busyDialogue = new CustomDialog(context);
        if (isProgressbarRequired) {
            busyDialogue.displayUiBlockingDialog();
        }
        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    busyDialogue.dismiss();
                    Log.d("Api response: ",""+response.toString());
                    if(response.has(Constants.STATUS)) {
                        if (response.getBoolean(Constants.STATUS)) {
                            callBackinerface.onJsonObjectSuccess(response);
                        } else {
                            callBackinerface.onFailure(response.optString(Constants.MESSAGE));
                        }
                    }
                    if(response.has(Constants.SUCCESS)){
                        if (response.getBoolean(Constants.SUCCESS)) {
                            callBackinerface.onJsonObjectSuccess(response);
                        } else {
                            callBackinerface.onFailure(response.optString(Constants.MSG));
                        }
                    }
                } catch (final JSONException e) {
                    busyDialogue.dismiss();
                    callBackinerface.onFailure(e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Log.e("IllegalArgumentExcep:",""+e.getLocalizedMessage());
                }
            }
        },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                busyDialogue.dismiss();
                callBackinerface.onFailure(error.getMessage());

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                /*if(isHeaderRequired){
                   // params.put("X-User-Token", userPreference.getStringData(Config.KeyName.USER_AUTHTOKEN));
                    //params.put(Config.RequestResponseKeys.device_type, Config.StringConstants.deviceType);
                    params.put("X-User-Email", userPreference.getStringData(Config.KeyName.USER_EMAIL));
                }*/
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(4000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }
    public void hitJSONObjectVolleyWebServiceforGet(int requestType, String url, JSONObject json, final Boolean isHeaderRequired, final CallBackInterface callBackinerface) {
        userPreference = ApplicationGlobal.getInstance().getPreference();
      //  Log.i("params web", json.toString());
        busyDialogue = new CustomDialog(context);
        if (isProgressbarRequired) {
            busyDialogue.displayUiBlockingDialog();
        }
        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    busyDialogue.dismiss();
                    if(response.has(Constants.STATUS)) {
                        if (response.getBoolean(Constants.STATUS)) {
                            callBackinerface.onJsonObjectSuccess(response);
                        } else {
                            callBackinerface.onFailure(response.optString(Constants.MESSAGE));
                        }
                    }
                    if(response.has(Constants.SUCCESS)){
                        if (response.getBoolean(Constants.SUCCESS)) {
                            callBackinerface.onJsonObjectSuccess(response);
                        } else {
                            callBackinerface.onFailure(response.optString(Constants.MSG));
                        }
                    }
                } catch (final JSONException e) {
                    busyDialogue.dismiss();
                    callBackinerface.onFailure(e.getMessage());
                    busyDialogue.displaySingleButtonDailog(context.getResources().getString(R.string.network_error), "Ok", new IOneBtnDialogListener() {
                        @Override
                        public void onPositiveClickListener(CustomDialog dialogInstance) {
                            dialogInstance.dismiss();
                        }
                    });
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Log.e("IllegalArgumentExcep:",""+e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                busyDialogue.dismiss();
                callBackinerface.onFailure(error.getMessage());
                busyDialogue.displaySingleButtonDailog(context.getResources().getString(R.string.network_error), "Ok", new IOneBtnDialogListener() {
                    @Override
                    public void onPositiveClickListener(CustomDialog dialogInstance) {
                        dialogInstance.dismiss();
                    }
                });
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(isHeaderRequired){
                    params.put("X-User-Token", userPreference.getStringData(Constants.KeyName.USER_AUTHTOKEN));
                    //params.put(Config.RequestResponseKeys.device_type, Config.StringConstants.deviceType);
                    params.put("X-User-Email", userPreference.getStringData(Constants.KeyName.USER_EMAIL));
                }

              // params.put("Content-Type", "Application/json");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }


    public void hitJSONObjectVolleyWebService2(int requestType, String url, HashMap<String, String> json, final CallBackInterface callBackinerface) {
        busyDialogue = new CustomDialog(context);
        if (isProgressbarRequired) {
            busyDialogue.displayUiBlockingDialog();
        }
        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : (new JSONObject(json)), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    busyDialogue.dismiss();
                    if (response.getString(Constants.ERROR).equals("1")) {
                        callBackinerface.onJsonObjectSuccess(response);
                    } else {

                        callBackinerface.onFailure(response.optString(Constants.MESSAGE));
                    }
                } catch (final JSONException e) {
                    busyDialogue.dismiss();
                    callBackinerface.onFailure(e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Log.e("IllegalArgumentExcep:",""+e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                busyDialogue.dismiss();
                callBackinerface.onFailure(error.getMessage());

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationGlobal.getInstance().addToRequestQueue(request);
    }



}