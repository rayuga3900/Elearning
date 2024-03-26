package com.example.elearning;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class studentviewteacher extends AppCompatActivity {
    String teachername;
    String usertype;
    private ImageView imageView;
    private TextView titleusername;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentviewteacher);
        imageView=findViewById(R.id.profile_image);
        Intent intent=getIntent();
        teachername=intent.getStringExtra("username");//username of teacher is passed
        usertype=intent.getStringExtra("usertype");//usertype is passed as Student
        Toast.makeText(this, "username"+teachername+"usertype"+usertype+"", Toast.LENGTH_SHORT).show();

          toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
       getSupportActionBar().setTitle("");

        titleusername=findViewById(R.id.titleusername);
        titleusername.setText(teachername);
        setUpBottomNavigation();
        loadFragment(new lecturefrag(),teachername,usertype);
        loadProfilePicture(teachername);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back when the home button (back arrow) is clicked
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            if (item.getItemId() == R.id.menu_lectures) {
                loadFragment(new lecturefrag(),teachername,usertype);
                return true;
            } else if (item.getItemId() == R.id.menu_notes) {
                loadFragment(new NotesFrag(),teachername,usertype);
                return true;
            }
            return false;
        });
    }
    /*
    to get the back arrow on app actionbar which allow
    us to go back two methods are used onSupportNavigateUp() ,
    onOptionsItemSelected(MenuItem item) and you
    also need to have app actionbar
     */





    public void loadProfilePicture(String username ) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/"+"Teacher/" + username + ".jpg");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.default_profile_picture) // Placeholder image while loading
                    .error(R.drawable.default_profile_picture) // Error image if loading fails
                    .into(imageView);

        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("FirebaseStorage", "Failed to get download URL: " + exception.getMessage());
        });
    }

}