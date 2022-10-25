package com.example.myemechanic;

import static android.content.ContentValues.TAG;

import android.app.DownloadManager;
import android.content.Context;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ServerValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FCmSendd {



    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    static String base64="-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDcLkylAscfnSBd\nj2nvPaG8U6pZQIxqSCJga7XJn8ZnQgt5gi/e8VgYWCv8NrLFRsiq9XdqLI9kJB36\nUzWK5q2saPo4/JsH1fUa0Cfuf589nioB8JLsrYDv11Gz8RqKUoi0tL8Gqet7ctsF\nBdCv1BS1ytQbE+tcotnDvqmIzcZL6HOgKmQ+TAsi015I3FMUv0OqTux9LptRtKXN\n6/4PcgcjRNL3v9HeIvSfVA7OygQEXxHn75AFojjSVOC0YfZu73RduVxFvCPT7nzy\nRVjDaNJ48+rowqtQlgQu2bAaX85nPDMEKXKCfl25sVd7IsYx0/XMHqlq/sW3ycU0\nEqM5WS7RAgMBAAECggEAGy6ZFjb/U7MPc5V4r8klTi1rBp0+KIHSwqIRfXx83zML\nXRsaI1Uaudiqx3aOP+YS8G4g6anqIuQFkmmS3tzHHJWSLJZc4k0C9yYzA9HcHByK\nVZZ4YQEzfYiQxNzzWJfDWkHFIoK4OFOP3/EdmBNf4iiR4mzlXdiZ9z0yy1mSDT1A\n2n0iBjxwN0c/E82i1pFgfpIFt5m72NQI4DfLNju0DNg/QUnI05pWv2kEx7BP+Y9w\nzXOesVJzhHs54B5ReQVDDkkVpaqKnwsj0fs044ySLSzk8r8U1BCJZTfFdSGM0/ER\nYcQ43FWzUrVx/l6QWfYgMHGxXP/kXVjqcJpf2eZ4lQKBgQD3yqz+nFe2yAifWhzo\n5NVwbj5Wr4d5bCTFOdqSik98jJeSztYI7OGv6EiuMNTOZXLc1QPgcUcr8pFiDF4Z\n/FREX6/TLJeG6CXD4tWNV3YH2YXb+MZwpGRu4RzWjEDqwZSi5MCiULMOM3/XpwRn\nZFOksqgBvmWttKj9+ENbVHb8tQKBgQDjeXpf9xHwbOBELfOwdJ17Ld+MWL+h1d8t\n3JN7m9MCrkWHLVV0s5zn2z3Gjtin973psUVWFacAeRCZxQwsA6Y8VrkE4MjID5n2\nMeagc/JrpPDuCMBaoCB7hBuLxs4unOrAHplq9wrtjOymXYizeTPFs9gRb7JbB0GY\nxqocp+CXLQKBgQD1720gD+BnA8a7El9bL7RAMMsiknjtpM4iCjfL9gbKYWgXR77b\ngPDG/M8WRFUSvVHxqGJ+oy+2iLyPCiNjuzOP4UDLYZjLRyIkd8Do1gPJ6rWXDCU4\nq5EbE8bhkrbjyVpTCqO+9kWhNLAQRC50x/jUoJVrlaOpcoO//mYsAXtPeQKBgH25\nYHe12MZnhrRoKW33piq0uf+Z9aB+0rFIXoNNJp0usOL7nWW4+Bn1xPj/bqO3e9Gz\n1ryzju/l+dTjYczZ34Et9WjKzG42oUO/ZNZ9/xibu0X0tEvigNGDvK3OnZqJw/2X\na/gAvpt47jstKyb28DRvKBbLA2roYcsM0mywJKOpAoGBAJiOmXpcVpN02Jl/5pOP\nJs9x2AYbHJqaFfLbOix0xonCgUWrsJeGsWkziq01d7aZpc6qc/W4Xuhic4D5FkdK\n57tL1qsuCsbPkZZ1mUSoRi8WoS39wUzdRjaTYSv8WqbGrzUuXX7q2BwMSNypOV+H\nHmhVKUg+wQF1QE0woex03mlx\n-----END PRIVATE KEY-----\n";
    // Sending side

    static String SERVER_KEY = base64.trim();
  //  Base64.encodeToString(base64.getBytes(), Base64.NO_WRAP);

    public static void pushNotification(Context context,String title,String token,String message){
    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    RequestQueue Queue= Volley.newRequestQueue(context);
    try{
        JSONObject json= new JSONObject();
        json.put("to",token);
        JSONObject notification= new JSONObject();
        notification.put("title",title);
        notification.put("body",message);
        json.put("notification",notification);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("FCM"+response);
                Log.d(TAG, "onResponse: notification start");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse: notification error");


            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("Content-Type","myemechanic_5d22dc2320da/json");
                params.put("Authorization",SERVER_KEY);
                return  params;
            }
        }  ;
Queue.add(jsonObjectRequest);
    } catch (JSONException e) {
        e.printStackTrace();
    }
}
}
