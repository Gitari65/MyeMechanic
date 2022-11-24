package com.example.myemechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MpesaPaymentActivity extends AppCompatActivity {
    String CONSUMER_KEY = "V20h0w0WUJUpZBLJFufR5XuScg6czdAQ";
    String CONSUMER_SECRET = "2k7901XCA24exJ6K";
    Daraja daraja;
    Button  btnpayment;
private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_payment);
        progressDialog = new ProgressDialog(this);
btnpayment=findViewById(R.id.buttonPay2);

        //for Mpesa
        daraja = Daraja.with(CONSUMER_KEY, CONSUMER_SECRET, new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i(MpesaPaymentActivity.this.getClass().getSimpleName(), accessToken.getAccess_token());
            }

            @Override
            public void onError(String error) {
                Log.e(MpesaPaymentActivity.this.getClass().getSimpleName(), error);
            }
        });
        String mobile="0725507616";
        String AMOUNT="20";
        btnpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMpesaPayment(mobile, AMOUNT);
            }
        });


    }
    private void makeMpesaPayment(String mobile, String  amount){
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Processing MPesa Request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LNMExpress lnmExpress = new LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                TransactionType.CustomerPayBillOnline,
                amount,
                mobile,
                "174379",
                mobile,
                "http://mpesa-requestbin.herokuapp.com/1od2was1",
                "Care It App",
                "Care It App"
        );

        //For both Sandbox and Production Mode
        daraja.requestMPESAExpress(lnmExpress,
                new DarajaListener<LNMResult>() {
                    @Override
                    public void onResult(@NonNull LNMResult lnmResult) {
                        Log.i(MpesaPaymentActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                        FancyToast.makeText(getApplicationContext(), lnmResult.ResponseDescription, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onError(String error) {
                        Log.i(MpesaPaymentActivity.this.getClass().getSimpleName(), error);
                        FancyToast.makeText(getApplicationContext(), error, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        progressDialog.dismiss();
                    }
                }
        );
    }

}