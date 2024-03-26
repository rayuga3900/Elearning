package com.example.elearning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class profile_information extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    String username;
    String usertype;
    private ImageView imageView;
    private Uri imageUri;
    private TextView tvusername;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        imageView.setImageBitmap(bitmap);
                        // Upload the image to Firebase with the user type
                        uploadImageToFirebase(imageUri, username, usertype);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_information);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        usertype = intent.getStringExtra("usertype");

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        // Enable the back button arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set the title of the toolbar
        getSupportActionBar().setTitle("Profile");

        // Initialize UI components
        imageView = findViewById(R.id.imageView);
        tvusername = findViewById(R.id.tvusername1);
        tvusername.setText(username);

        // Load the user's profile picture using Glide or display default placeholder
        loadProfilePicture(username,usertype);

        // Set click listener for the camera button
        findViewById(R.id.cameraButtonImageView).setOnClickListener(this::onCameraButtonClick);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Handle the back button click
        return true;
    }

    public void onCameraButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToFirebase(Uri imageUri, String username, String usertype) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get reference to the profile picture
        StorageReference profileImageRef = storageRef.child("profile_images/" + usertype + "/" + username + ".jpg");

        // Upload the new profile picture
        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Load the new profile picture
                    loadProfilePicture(username, usertype);
                })
                .addOnFailureListener(e -> {
                    // Handle errors during the upload
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }


    private void loadProfilePicture(String username, String usertype) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + usertype + "/" + username + ".jpg");

        // Check if the profile image exists in Firebase
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Profile image exists, load it
            if (!isFinishing()) { // Check if the activity is not finishing
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.default_profile_picture) // Placeholder image while loading
                        .error(R.drawable.default_profile_picture) // Error image if loading fails
                        .into(imageView);
            }
        }).addOnFailureListener(exception -> {
            // Profile image doesn't exist, upload default image and username

        });
    }







}
