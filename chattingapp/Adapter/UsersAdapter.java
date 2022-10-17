package com.example.chattingapp.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattingapp.ChatActivity;
import com.example.chattingapp.Model.Message;
import com.example.chattingapp.Model.User;

import com.example.chattingapp.R;
import com.example.chattingapp.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    Context context;
    ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;

    }
    public void setFilteredList(ArrayList<User> filteredList){
        this.users = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_conversation,parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        // now binding data
        User user = users.get(position);

        holder.binding.username.setText(user.getName());

        Message message = new Message();

        String senderUid = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderUid + user.getUid();



        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(senderRoom)
                                .child("messages")
                                        .orderByChild("timestamp")
                                                .limitToLast(1)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                if(snapshot.exists()){
                                                                    for(DataSnapshot snapshot1: snapshot.getChildren()){


                                                                        String lastmessage = snapshot1.child("message").getValue().toString();
                                                                        String lastmessagetime = snapshot1.child("currentTime").getValue().toString();


                                                                        holder.binding.lastmessages.setText(lastmessage);
                                                                        holder.binding.lasttime.setVisibility(View.VISIBLE);
                                                                        holder.binding.lasttime.setText(lastmessagetime);






                                                                    }

                                                                }
                                                            }




                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });




        // image processing using glide
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.user)
                .into(holder.binding.profile);

        // click on user section navigate to the chat activity

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("profileimage",user.getProfileImage());
                intent.putExtra("uid", user.getUid());
                intent.putExtra("token", user.getToken());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
