package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chattingapp.databinding.ActivitySendOtpBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOtp extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ActivitySendOtpBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOtpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        getSupportActionBar().hide();

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(SendOtp.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String EnteredNumber = binding.sendnumberbox.getText().toString().trim();
                if(EnteredNumber.isEmpty()){
                    binding.sendnumberbox.setError("Phone number is required!");
                    binding.sendnumberbox.requestFocus();
                    return;


                }
                else if(EnteredNumber.length()!=10){
                    binding.sendnumberbox.setError("Enter 10 digit number");
                    binding.sendnumberbox.requestFocus();
                    return;
                }
                else{
                    otpSend();
                }
            }
        });


    }

    private void otpSend() {
//        progress.setVisibility(View.VISIBLE);
        dialog.show();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(SendOtp.this, "Verification failed!", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                dialog.dismiss();
                String EnteredNumber =  binding.sendnumberbox.getText().toString().trim();
//                progress.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SendOtp.this, VerifyOtpActivity.class);
                intent.putExtra("replaceText", EnteredNumber);
                intent.putExtra("VerificationId", verificationId);
                startActivity(intent);

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+91"+ binding.sendnumberbox.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}