package com.example.elearning;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elearning.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class firebasedemo extends AppCompatActivity {

    FirebaseFirestore firestore;
    private ActivityResultLauncher<String> pickVideoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebasedemo);

        // Initialize the FirebaseFirestore object
        firestore = FirebaseFirestore.getInstance();

        // Initialize the pickVideoLauncher
        pickVideoLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::onVideoPicked);

        // Start an intent to pick a video from the phone storage
        pickVideoLauncher.launch("video/*");
    }

    // Callback method called when a video is picked
    private void onVideoPicked(Uri videoUri) {
        if (videoUri != null) {
            // Upload the selected video to Firebase Storage
            uploadVideoToFirebaseStorage(videoUri);
        }
    }

    private void uploadVideoToFirebaseStorage(Uri videoUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference videoRef = storageRef.child("videos").child(videoUri.getLastPathSegment());

        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Video uploaded successfully
                    // Retrieve the download URL of the uploaded video
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Pass the download URL to a method to display the video player
                        displayVideo(uri.toString());
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors that occur during the upload process
                });
    }

    private void displayVideo(String videoUrl) {
        // You can use a VideoView or other video player component to display the video
        // For example, if using a VideoView:
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.start();
    }
}
