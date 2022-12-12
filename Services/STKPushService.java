package com.example.myemechanic.Services;



import com.androidstudy.daraja.model.AccessToken;
import com.example.myemechanic.Model.StkPushResponse;
import com.example.myemechanic.STKPush;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface STKPushService {
    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPush> sendPush(@Body STKPushs stkPush);

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("stkPush")
    Call<StkPushResponse> pushSTK(
            @Query("Amount") int amount,
            @Query("MSISDN") String msisdn
//            @Query("ACCOUNT_REFERENCE") String account_Reference,
//            @Query("ID") String request_ID


    );
}
