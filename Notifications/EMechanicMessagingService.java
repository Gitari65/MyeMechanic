package com.example.myemechanic.Notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class EMechanicMessagingService extends FirebaseMessagingService {
    private static final String TAG = "EMechanic";
    String title, remoteMessage, typepage;
    String refreshToken;
    String UserID;
    public static final String PREFS_APP ="com.example.myemechanic";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        String UserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    refreshToken=task.getResult();
                    if(UserID!=null){
                        FirebaseDatabase.getInstance().getReference("Tokens").child(UserID).child("token").setValue(refreshToken);
                    }else{
                        Log.d(TAG, "onComplete: UserId is null");
                    }
                }
            }
        });
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if(message.getData()!=null){
            title = message.getData().get("Title");
            remoteMessage = message.getData().get("Message");
            typepage = message.getData().get("Typepage");

            ShowNotifications.showNotification(getApplicationContext(),title,remoteMessage,typepage);
        }else{
            Log.d(TAG, "onMessageReceived: message is null");
        }
    }
}
