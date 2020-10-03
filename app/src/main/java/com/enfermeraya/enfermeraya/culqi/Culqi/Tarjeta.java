package com.enfermeraya.enfermeraya.culqi.Culqi;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.enfermeraya.enfermeraya.app.Modelo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Tarjeta {

    Config config = new Config();

    private static final String URL = "/cards/";
    Modelo modelo = Modelo.getInstance();
    private String api_key;

    private CargoCallback listener;

    public Tarjeta(String api_key){
        this.api_key = api_key;
        this.listener = null;
    }


    public void createCargo(Context context, ClassCargo cargo, final CargoCallback listener) {

        this.listener = listener;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody = new JSONObject();
            jsonBody.put("customer_id", modelo.apikeyculqi);
            jsonBody.put("token_id", modelo.qulqiId);


        } catch (Exception ex){
            Log.v("", "ERROR: "+ex.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, config.url_base+URL,jsonBody, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listener.onSuccess(response);
                } catch (Exception ex){
                    listener.onError(ex);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + Tarjeta.this.api_key);
                return headers;
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

    }
}
