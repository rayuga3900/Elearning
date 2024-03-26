package com.example.elearning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.widget.VideoView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class videouploadactivity extends AppCompatActivity {
    String username;
    String timestamp;
    String usertype;
    private ActivityResultLauncher<String> videoPickerLauncher;
    private VideoView videoView;
    private MediaController mediaController;
    private Uri selectedVideoUri; // New variable to store selected video URI
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videouploadactivity);
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        usertype=intent.getStringExtra("usertype");
        toolbar=findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Upload a video");
        // Initialize the ActivityResultLauncher
        videoPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                // Handle the selected video URI
                if (result != null) {
                    selectedVideoUri = result; // Store the selected video URI
                    // Set the selected video URI to the VideoView
                    videoView.setVideoURI(result);

                    // Set up MediaController to show controls
                    if (mediaController == null) {
                        mediaController = new MediaController(videouploadactivity.this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                    }

                    videoView.start();
                }
            }
        });

        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progress);

        // Example usage in a button click or any other event
        Button selectVideoButton = findViewById(R.id.choosebtn);
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the video picker
                videoPickerLauncher.launch("video/*");
            }
        });

        Button uploadVideoButton = findViewById(R.id.uploadbtn);
        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to upload video to Firebase and pass the Uri
                if (selectedVideoUri != null) {
                    uploadVideoToFirebase(selectedVideoUri);
                } else {
                    Toast.makeText(videouploadactivity.this, "Please select a video", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
       onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void uploadVideoToFirebase(Uri videoUri) {

        // Get subject, title, and generate a filename
        EditText subjectEditText = findViewById(R.id.editTextText);
        EditText titleEditText = findViewById(R.id.tbtitle);
        String subject = subjectEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        timestamp= String.valueOf(System.currentTimeMillis());
        String filename = "video_" + timestamp + ".mp4";

        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter subject and title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to "videos/filename.mp4"
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos/" + username +"/"+filename);

        // Show ProgressBar to indicate video upload is in progress
        progressBar.setVisibility(View.VISIBLE);

        // Upload the file to Firebase Storage
        storageRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded file
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Once you have the download URL, you can proceed to store data in Firestore
                        storeDataInFirestore(downloadUri.toString(), subject, title, filename);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(videouploadactivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    // Hide ProgressBar on upload failure
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void storeDataInFirestore(String videoUrl, String subject, String title, String filename) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Map<String, Object> videoData = new HashMap<>();
        videoData.put("videoUrl", videoUrl);
        videoData.put("subject", subject);
        videoData.put("title", title);
        videoData.put("Timestamp", timestamp);
        videoData.put("uploaded by",username);
        // Add the data to Firestore
        db.collection("videodetails")
                .add(videoData)
                .addOnSuccessListener(documentReference -> {

                    navigateToLectureFragment(videoUrl, subject, title, filename,username,usertype);

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(videouploadactivity.this, "Video details upload successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(videouploadactivity.this, "Failed to store video details in Firestore", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                });
    }

    private void navigateToLectureFragment(String videoUrl, String subject, String title, String filename,String username,String usertype) {
        // Start a new activity passing the video details
        Intent intent = new Intent(videouploadactivity.this, Teacher.class);
        intent.putExtra("videoUrl", videoUrl);
        intent.putExtra("subject", subject);
        intent.putExtra("title", title);
        intent.putExtra("filename", filename);
       //the username and usertype are passed back to teacher activity as it is loading profile picture and
        //and username without these it will not be able to load that after redirecting
        //from videouploadactivity.java to Teacher.java
        intent.putExtra("username",username);
        intent.putExtra("usertype",usertype);
        startActivity(intent);
    }
}
