package com.example.elearning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class videoviewactivity extends AppCompatActivity {

    private boolean isLiked = false; // Variable to track like state
    private int likeCount = 0; // Variable to track like count
    private String documentId; // Variable to store the document ID
    String title;
    String subject;
    private TextView tvdesc;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoviewactivity);

        // Get the video URL and document ID from the intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        documentId = getIntent().getStringExtra("documentId");
        subject=getIntent().getStringExtra("subject");
        title=getIntent().getStringExtra("title");
        tvdesc=findViewById(R.id.tvdesc);
        tvdesc.setText("Subject :  "+subject+"\nTitle :  "+title);
        toolbar=findViewById(R.id.toolbar5);
         setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Lecture");
        // Initialize VideoView
        VideoView videoView = findViewById(R.id.videoView2);

        // Set up MediaController to show controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Set the video URL to the VideoView
        videoView.setVideoURI(Uri.parse(videoUrl));

        // Start playing the video
        videoView.start();

        // Like button logic
        ImageButton likeBtn = findViewById(R.id.likebtn);
        TextView likeCountTextView = findViewById(R.id.tvlike);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle like state
                isLiked = !isLiked;

                // Update like count based on like state
                if (isLiked) {
                    likeCount++;
                 //   Toast.makeText(videoviewactivity.this, "Liked", Toast.LENGTH_SHORT).show();
                } else {
                    likeCount--;

                    //Toast.makeText(videoviewactivity.this, "Unliked", Toast.LENGTH_SHORT).show();
                }

                // Update like count in Firestore
                updateLikeCountInFirestore(documentId, likeCount);

                // Update UI based on like state and count
                updateLikeUI();
            }
        });

        // Share button logic
        ImageButton shareBtn = findViewById(R.id.sharebtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle share button click
                shareVideo();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // Method to share the video
    private void shareVideo() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video: " + getIntent().getStringExtra("videoUrl"));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // Method to update UI based on like state and count
    private void updateLikeUI() {
        // Update the like button drawable based on like state
        int likeBtnDrawable = isLiked ? R.drawable.ic_likebtn_filled : R.drawable.ic_likebtn_stroke;
        ImageButton likeBtn = findViewById(R.id.likebtn);
        likeBtn.setImageResource(likeBtnDrawable);

        // Update the like count TextView
        TextView likeCountTextView = findViewById(R.id.tvlike);
        likeCountTextView.setText(String.valueOf(likeCount));
        if((String.valueOf(likeCount).equals("0")))
        {
            likeCountTextView.setText("Like");
        }
    }

    private void updateLikeCountInFirestore(String documentId, int likeCount) {
        // Create a reference to the specific document using its ID
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("videodetails").document(documentId);

        // Update the like count field in Firestore
        documentReference
                .update("likeCount", likeCount)
                .addOnSuccessListener(aVoid -> {

                 //   Toast.makeText(videoviewactivity.this, "Like count updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                  //  Toast.makeText(videoviewactivity.this, "Failed to update like count", Toast.LENGTH_SHORT).show();
                });
    }
}
