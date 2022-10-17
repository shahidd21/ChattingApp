package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chattingapp.Adapter.MessagesAdapter;
import com.example.chattingapp.Adapter.UsersAdapter;
import com.example.chattingapp.Model.Message;
import com.example.chattingapp.Model.User;
import com.example.chattingapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;



    MessagesAdapter adapter;

    ArrayList<Message> messages;

    ArrayList<User> users;
    String senderRoom;
    String senderUsername;
    String receiverRoom;
    FirebaseDatabase database;
    Uri selectedImageUri;
    String senderUid;
    String receiverUid;
    FirebaseStorage storage;
    SimpleDateFormat simpleDateFormat;
    JsonObjectRequest JsonObjectRequestrequest;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        simpleDateFormat = new SimpleDateFormat("hh:mm a");/////////////

        messages = new ArrayList<>();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setHasFixedSize(true);
        // binding an adapter to show the message on screen
        binding.recyclerView.setLayoutManager(linearLayoutManager);


        adapter = new MessagesAdapter(this,messages);

        binding.recyclerView.setAdapter(adapter);



//        setSupportActionBar(binding.toolbar);

        getSupportActionBar().hide();




        name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("profileimage");
        receiverUid = getIntent().getStringExtra("uid");

        String token = getIntent().getStringExtra("token");


        senderUid = FirebaseAuth.getInstance().getUid();



        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.user)
                .into(binding.profiledp);

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        binding.chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, WallpaperChooser.class);
                startActivity(intent);

            }
        });




        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()) {
                        binding.onlinestatus.setText(status);
                        binding.onlinestatus.setVisibility(View.VISIBLE);
                    }
                    if (status.equals("offline")) {
                        binding.onlinestatus.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.name.setText(name);

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });












        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messages.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Message message = snapshot1.getValue(Message.class);
                    //////////////////////
                    messages.add(message);

                }

                adapter.notifyDataSetChanged();
                binding.recyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });













        binding.sendarrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(ChatActivity.this, "Button is clicked", Toast.LENGTH_SHORT).show();


                ///////////////////////////////////

                users = new ArrayList<>();


                database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // clear the user
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);

                            if (user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                senderUsername = user.getName();

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //////////////////////////////////////


                Date date = new Date();
                String currenttime = simpleDateFormat.format(date);
                String messageTxt = binding.messagebox.getText().toString();

                if (!messageTxt.equals("")) {

                    Message message = new Message(messageTxt, senderUid, date.getTime(), currenttime);
                    binding.messagebox.setText("");


                    // pushing the message in sender room
                    database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                            // pushing the message in receiver room
                            database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    sendNotification(senderUsername, message.getMessage(), token);

                                }
                            });


                        }
                    });
                }


            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageChooser();

            }
        });
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Handler handler = new Handler();


        binding.messagebox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                        if(bottom<oldBottom){
//                            binding.recyclerView.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    binding.recyclerView.smoothScrollToPosition(bottom);
//                                }
//                            }, 100);
//                        }
//                    }
//                });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("online");
                }
            };
        });


        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    public void ImageChooser() {


        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 25);
    }
    // this function is triggered when user
    // selects the image from the imageChooser

    void sendNotification(String name, String message, String token) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";
            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequestrequest = new JsonObjectRequest(url, notificationData
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

//                    Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAA6mkPmgY:APA91bFvs4Z7hO1nfYUPpEcg3JXbU3CVluhhZSJ2K3pv0tC2lwMZVb2Gjh1p2al26jOLdy8eHzlrEwTepEmVfrsOOp3j4ownrPk_Nww388OEGJHIEaThd1Jq1XhTrLTRIXU29x6kY7kU";
                    map.put("Authorization", key);
                    map.put("Content-Type", "application/json");


                    return map;
                }
            };

            queue.add(JsonObjectRequestrequest);


        } catch (Exception ex) {
            Toast.makeText(this, "something error happend", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 25) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout

                    Toast.makeText(this, "Sorry you can't send photos right now! It's in progress", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String CurrentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(CurrentId).setValue("online");

    }

    @Override
    protected void onPause() {
        String CurrentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(CurrentId).setValue("offline");
        super.onPause();
    }

//    @Override
//    protected void onStop() {
//        String CurrentId = FirebaseAuth.getInstance().getUid();
//        database.getReference().child("presence").child(CurrentId).setValue("offline");
//        super.onStop();
//
//
//
//    }


}