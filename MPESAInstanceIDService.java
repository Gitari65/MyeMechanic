package com.example.myemechanic;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Created by miles on 20/11/2017.
 */

public class MPESAInstanceIDService{
    Context context;



    public void onTokenRefresh() {

        //
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: token not generated", task.getException());
                            return;
                        }
                        Log.d(TAG, "onComplete: token  generated", task.getException());


                        // Get new FCM registration token
                        String refreshedToken =  Objects.requireNonNull(task.getResult());
                        // Get updated InstanceID token.

                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(MpesaMainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("InstanceID", refreshedToken);
                        editor.commit();
                    }
                });


    }

}