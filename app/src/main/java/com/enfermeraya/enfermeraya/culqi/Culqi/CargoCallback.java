package com.enfermeraya.enfermeraya.culqi.Culqi;

import org.json.JSONObject;

/**
 * Created by culqi on 2/7/17.
 */

public interface CargoCallback {

    public void onSuccess(JSONObject cargo);

    public void onError(Exception error);

}
