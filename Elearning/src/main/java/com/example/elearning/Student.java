package com.example.elearning;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.example.elearning.databinding.StudActivityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Student extends AppCompatActivity {
    String username;
    String usertype;
    private ImageView imageView;
    private TextView tvusername;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public SessionManager sm;
    private StudActivityBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stud_activity);
        binding = StudActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        usertype=intent.getStringExtra("usertype");
        drawerLayout = findViewById(R.id.drawerLayout);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpDrawer();
        setUpBottomNavigation();
        sm=new SessionManager(getApplicationContext());
        loadFragment(new addedfrag(),username,usertype);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //when back button is press close app
                finishAffinity();
                setEnabled(false);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadProfilePicture(username,usertype);
    }
    private void loadFragment(Fragment fragment, String username, String usertype) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("usertype",usertype);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void setUpBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_added) {
                loadFragment(new addedfrag(),username,usertype);
                return true;
            } else if (item.getItemId() == R.id.menu_discover) {
                loadFragment(new discoverfrag(),username,usertype);
                return true;
            }
            return false;
        });
    }
    private void setUpDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        imageView = headerview.findViewById(R.id.imageView);
        loadProfilePicture(username, usertype);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        headerview.setOnClickListener(v -> {
            openProfile();
        });

        tvusername = headerview.findViewById(R.id.tvusername1);
        tvusername.setText(username);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle sidebar menu item click
            selectMenuItem(item.getItemId());
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
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

    private void selectMenuItem(int itemId) {
        try {
            if (itemId == R.id.nav_settings) {
                // settings in sidebar menu
            } else if (itemId == R.id.nav_logout) {

                sm.logoutUser();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            e.printStackTrace();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
    }
    public void loadProfilePicture(String username, String usertype ) {
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
        CollectionReference userDetailsRef = db.collection("studentdetails");

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

    public void openProfile()
    {
        Intent intent=new Intent(this, profile_information.class);
        intent.putExtra("username",username);
        intent.putExtra("usertype",usertype);
        startActivity(intent);
    }

}
