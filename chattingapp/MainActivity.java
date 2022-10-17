package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chattingapp.Adapter.NodataAdapter;
import com.example.chattingapp.Model.NoData;
import com.example.chattingapp.databinding.ActivityMainBinding;
import com.example.chattingapp.Adapter.UsersAdapter;
import com.example.chattingapp.Model.User;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // binding

    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;

    ArrayList<User> users;

    UsersAdapter usersAdapter;


    String username;
    String profileid;
    String profileDescription;

    NodataAdapter nodataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();


        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.chats:


                        break;

                    case R.id.status:


                        break;
                }
                return true;
            }
        });



        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token",token);
                        database.getReference()
                                .child("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, users);

        // setting the adapter on the recycler view

        binding.recyclerview.setAdapter(usersAdapter);



        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // clear the user
                users.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);

                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        users.add(user);
                        if(user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        {
                            username = user.getName();
                            profileid = user.getProfileImage();
                            profileDescription = user.getDescription();

                        }
                    } else {
                        if(user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        {
                            username = user.getName();
                            profileid = user.getProfileImage();
                            profileDescription = user.getDescription();

                        }
                    }

                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
//
//            case R.id.search:
//                Toast.makeText(this, "Search is clicked", Toast.LENGTH_SHORT).show();
//
//
//
//                break;


            case R.id.yourprofile:
//                Toast.makeText(this, "Your Profile is clicked", Toast.LENGTH_SHORT).show();
                String phonenumb = auth.getCurrentUser().getPhoneNumber();




                Intent intent = new Intent(MainActivity.this, YourProfileActivity.class);
                intent.putExtra("number", phonenumb);
                intent.putExtra("profileusername",username);
                intent.putExtra("profileimage", profileid);
                intent.putExtra("description", profileDescription);

//                intent.putExtra("profileimage", profileid);
                startActivity(intent);




                break;


            case R.id.groups:
                Toast.makeText(this, "Groups is clicked", Toast.LENGTH_SHORT).show();


                break;

            case R.id.settings:
                Toast.makeText(this, "Settings is clicked", Toast.LENGTH_SHORT).show();


                break;
            case R.id.logout:
                userLogOut();






                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private void userLogOut() {

        try{

            Toast.makeText(this, "Something is bad! try after some time", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(MainActivity.this, "Search is expanded", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Toast.makeText(MainActivity.this, "Search is collapsed", Toast.LENGTH_SHORT).show();
                return true;
            }


        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search Data here..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterList(newText);

                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void filterList(String newText) {
        ArrayList<User> filteredList = new ArrayList<>();
        if(newText.isEmpty() || newText=="" || newText==null){
            binding.recyclerview.setAdapter(usersAdapter);
            usersAdapter.setFilteredList(users);
        }else{

            for(User user:users){

                if(user.getName().toLowerCase().contains(newText.toLowerCase())){

                    filteredList.add(user);
//                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }

            if(filteredList.isEmpty()){
                ArrayList<NoData> list = new ArrayList<>();
                list.add(new NoData("No User found!",R.drawable.usernotfound));
                binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
                nodataAdapter = new NodataAdapter(this,list);
                binding.recyclerview.setAdapter(nodataAdapter);


            }else{

                usersAdapter.setFilteredList(filteredList);
                binding.recyclerview.setAdapter(usersAdapter);
                usersAdapter.notifyDataSetChanged();
            }


        }


    }
}