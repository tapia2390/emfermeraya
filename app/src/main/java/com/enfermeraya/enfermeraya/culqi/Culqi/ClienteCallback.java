package com.enfermeraya.enfermeraya.culqi.Culqi;

import org.json.JSONObject;

/**
 * Created by culqi on 2/7/17.
 */

public interface ClienteCallback {

    public void onSuccess(JSONObject token);

    public void onError(Exception error);

}