package com.example.elearning;

import static com.example.elearning.HttpClient.getClient;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.elearning.databinding.AdminActivityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Admin extends AppCompatActivity {

    private ImageView imageView;
    private  TextView tvusername;
    String uname;
    String usertype;
    private ArrayList<requestmodel> requestarr;
    private RecyclerView recyclerrequest;
    private TextView tverror;
    private BottomNavigationView bottomNavigationView;
    private AdminActivityBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public  SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);
        binding = AdminActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
      uname=intent.getStringExtra("username");
        usertype=intent.getStringExtra("usertype");
          sm=new SessionManager(getApplicationContext());
        tverror = findViewById(R.id.tvmsg3);
        recyclerrequest = findViewById(R.id.recyclerrequest);
        recyclerrequest.setLayoutManager(new LinearLayoutManager(this));
        requestarr = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setSelectedItemId(R.id.menu_admin);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_teacher) {
                    startActivity(new Intent(Admin.this, teacherrequest.class)
                            .putExtra("username",uname)
                            .putExtra("usertype",usertype)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    return true;
                } else if (item.getItemId() == R.id.menu_student) {
                    startActivity(new Intent(Admin.this, studentrequest.class)
                            .putExtra("username",uname)
                            .putExtra("usertype",usertype)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Fetch data when the user performs a swipe gesture
                fetchData();
            }
        });

        // Start fetching data initially
        fetchData();

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //set up the action bar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview=navigationView.getHeaderView(0);
        imageView=headerview.findViewById(R.id.imageView);
        loadProfilePicture(uname,usertype);




        headerview.setOnClickListener( v->{openProfile();});

        tvusername=headerview.findViewById(R.id.tvusername1);
        tvusername.setText(uname);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here
                int id = item.getItemId();
                if (id == R.id.nav_settings) {
                    // Open settings activity
                    openSettings();
                } else if (id == R.id.nav_logout) {
                   sm.logoutUser( );
                }
                /*else if (id == R.id.nav_profile) {
                    // Handle profile click (e.g., open profile activity)
                    openProfile();
                }*/
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        // Initialize ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open, // Description for accessibility
                R.string.navigation_drawer_close
        );

        // Set ActionBarDrawerToggle as the listener for DrawerLayout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Enable the hamburger icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //for handling backbtn events
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                finishAffinity(); // This closes the current activity and all parent activities
                setEnabled(false);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
        // Set the selected item in bottom navigation based on the current activity
        bottomNavigationView.setSelectedItemId(R.id.menu_admin);
        loadProfilePicture(uname, usertype);
    }



    public void openProfile()
    {
        Intent intent=new Intent(this, profile_information.class);
        intent.putExtra("username",uname);
        intent.putExtra("usertype",usertype);
        startActivity(intent);
    }
    private void loadProfilePicture(String username, String usertype) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + usertype + "/" + username + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.default_profile_picture) // Placeholder image while loading
                    .error(R.drawable.default_profile_picture) // Error image if loading fails
                    .into(imageView);
            writeUserData(username,uri.toString());
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("FirebaseStorage", "Failed to get download URL: " + exception.getMessage());
        });
    }
    private void writeUserData(String username, String imagelink) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userDetailsRef = db.collection("admindetails");

        // Check if the document already exists for the given username
        userDetailsRef.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Document already exists, change the imagelink only to avoid
                        //duplicate username and imagelink from being added
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        userDetailsRef.document(documentId).update("profileImageLink", imagelink)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document updated"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating document", e));
                    } else {
                        // Document doesn't exist, add a new document
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("username", username);
                        userData.put("profileImageLink", imagelink);

                        userDetailsRef.add(userData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Firestore", "DocumentSnapshot written with ID: " + documentReference.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error adding document", e);
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error querying document", e));
    }

    /*
        Show hamburger icon instead of an arrow:
        add actionBarDrawerToggle.syncState(); inside
         void onPostCreate(Bundle savedInstanceState) and
         actionBarDrawerToggle.onConfigurationChanged(newConfig);
         inside  void onConfigurationChanged(Configuration newConfig)
         or it will show arrow(child activity)
          instead of a hamburger icon
         */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the ActionBarDrawerToggle
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        // Implement logic to open settings activity
    }



    private void fetchData() {
        RetrofitClient rf=new RetrofitClient();
        sqlinterface sql=rf.getClient(getApplicationContext());
        Call<List<requestmodel>> call = sql.getrequest("Admin");
        call.enqueue(new Callback<List<requestmodel>>() {
            TableLayout tl = findViewById(R.id.tlhead);
            TextView tv=findViewById(R.id.tvreqmsg);
            @Override
            public void onResponse(Call<List<requestmodel>> call, Response<List<requestmodel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<requestmodel> requestModels = response.body();
                    requestarr.clear(); // Clear existing data
                    for (requestmodel model : requestModels) {
                        String name = model.getName();
                        String username = model.getUsername();
                        requestarr.add(new requestmodel(name, username,"Admin"));
                    }
                    tl.setVisibility(View.VISIBLE);
                    tv.setText(null);
                    // Update the RecyclerView adapter with new data
                    updateRecyclerView();
                }
                // Stop the swipe-to-refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<requestmodel>> call, Throwable t) {

                tl.setVisibility(View.GONE);
                recyclerrequest.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
                tv.setText("No New Requests");
                // tverror.setText("Error: " + t.getMessage());
                // Stop the swipe-to-refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateRecyclerView() {
        // Update the RecyclerView adapter with new data
        useradapter adapter = new useradapter(Admin.this, requestarr);
        recyclerrequest.setAdapter(adapter);
    }

}
