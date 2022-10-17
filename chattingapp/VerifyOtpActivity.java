package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.chattingapp.databinding.ActivityVerifyOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    ActivityVerifyOtpBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;

    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Verifying OTP...");
        dialog.setCancelable(false);
        getSupportActionBar().hide();
        String number = getIntent().getStringExtra("replaceText");
        binding.textView.setText("Verify +91-" + number);

        verificationId = getIntent().getStringExtra("VerificationId");


        binding.verifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = binding.otpbox.getText().toString().trim();

                if (code.isEmpty() || code.length() != 6) {
                    binding.otpbox.setError("Enter 6 digit code!");
                    binding.otpbox.requestFocus();
                } else {
                    if (verificationId != null) {
                        dialog.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(VerifyOtpActivity.this, ProfileSetup.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(VerifyOtpActivity.this, "Otp is not valid", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });          ///////////////////// build();
                    }
                }
            }
        });

    }

}

