package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import com.bumptech.glide.Glide;

import com.example.chattingapp.databinding.ActivityYourProfileBinding;


public class YourProfileActivity extends AppCompatActivity {
    ActivityYourProfileBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYourProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        String phonenumber = getIntent().getStringExtra("number");
        String name = getIntent().getStringExtra("profileusername");
        String imageurl = getIntent().getStringExtra("profileimage");
        String pdescription = getIntent().getStringExtra("description");

        binding.yourphonenumber.setText(phonenumber);
        binding.yourname.setText(name);
        binding.profiled.setText(pdescription);



        Glide.with(this).load(imageurl)
                .placeholder(R.drawable.user)
                .into(binding.yourprofileimage);

        getSupportActionBar().setTitle("Your Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



}