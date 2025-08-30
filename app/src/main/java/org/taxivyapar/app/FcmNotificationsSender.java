package org.taxivyapar.app;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {
    JSONObject mainObj;
    Context mContext;
    Activity mActivity;
    private RequestQueue requestQueue;

    public FcmNotificationsSender(JSONObject notificationJson, Context mContext, Activity mActivity) {
        this.mainObj = notificationJson;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {

        new Thread(() -> {
            try {
                requestQueue = Volley.newRequestQueue(mActivity);
                try {
                    String postUrl = "https://onesignal.com/api/v1/notifications";
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> header = new HashMap<>();
                            header.put("content-type", "application/json");
                            header.put("Cache-Control", "no-cache");
                            header.put("Authorization", "Basic " + profileContainer.oneSignalToken);
                            return header;
                        }
                    };
                    requestQueue.add(request);
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
        }).start();

    }
}
