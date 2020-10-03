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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Cliente {

    Config config = new Config();

    private static final String URL = "/customers/";


    private String api_key;

    private ClienteCallback listener;

    public Cliente(String api_key){
        this.api_key = api_key;
        this.listener = null;
    }


    public void createCliente(Context context, Customer customer, final ClienteCallback listener) {

        this.listener = listener;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody = new JSONObject();

            jsonBody.put("first_name", customer.getFirst_name());
            jsonBody.put("last_name", customer.getLast_name());
            jsonBody.put("email", customer.getEmail());
            jsonBody.put("address", customer.getAddress());
            jsonBody.put("address_city", customer.getAddress_city());
            jsonBody.put("country_code", "ER");
            jsonBody.put("phone_number", customer.getPhone_number());

        } catch (JSONException ex){
            Log.v("", "ERROR: "+ex.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, config.url_base+URL, jsonBody, new Response.Listener<JSONObject>(){
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
                headers.put("Authorization",  "Bearer "+Cliente.this.api_key);
                return headers;
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

    }
}
