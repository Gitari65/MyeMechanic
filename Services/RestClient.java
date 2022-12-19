package com.example.myemechanic.Services;



import com.androidstudy.daraja.model.AccessToken;
import com.example.myemechanic.Model.StkPushResponse;
import com.example.myemechanic.Notifications.NotificationResponse;
import com.example.myemechanic.Notifications.NotificationSender;
import com.example.myemechanic.STKPush;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestClient {
    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPush> sendPush(@Body STKPushs stkPush);

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("stkPush")
    Call<StkPushResponse> pushSTK(
            @Query("Amount") int amount,
            @Query("MSISDN") String msisdn,
            @Query("ID") String request_ID
            //            @Query("ACCOUNT_REFERENCE") String account_Reference,


    );

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key= AAAARAohCBA:APA91bF9jGqs0RIAkH4HeVHTH9qJxL5S7tk-s2GySPQbowafv_wFpFJlZoIg-acjwKWz2tvtZGXZQBAqalKWOxkCmQKh-YizGOZLLjDVk6_SbrqoHyPxuENP11bQz2nSMaLYerYhn4VN"
            }
    )

    @POST("fcm/send")
    Call<NotificationResponse> sendNotification(@Body NotificationSender body);
}
