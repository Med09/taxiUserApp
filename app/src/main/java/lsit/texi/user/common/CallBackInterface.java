package lsit.texi.user.common;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Vikram on 30-10-2018.
 */
public interface CallBackInterface {

    public void onJsonObjectSuccess(JSONObject object);

    public void onJsonArrarSuccess(JSONArray array);

    public void onFailure(String str);
}
