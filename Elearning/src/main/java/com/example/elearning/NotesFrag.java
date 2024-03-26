package com.example.elearning;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesFrag extends Fragment {
    String username;
    String usertype;
    private ActivityResultLauncher<String> filePickerLauncher;
    private RecyclerView recyclerView;
    private notesadapter notesAdapter;
    private List<notesitem> notesList;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout ;
    private TextView tvmsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        tvmsg=view.findViewById(R.id.tvmsg4);
        if (getArguments() != null) {
            username = getArguments().getString("username");
            usertype=getArguments().getString("usertype");

        }
        // Inside onCreateView method
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> results) {
                if (results != null && !results.isEmpty()) {
                    // Files have been selected, now update the adapter

                    for (Uri result : results) {
                        // Check if the file is not video or audio
                        if (!isVideoFile(result) && !isAudioFile(result)) {
                            // Add new notes items based on the uploaded files
                            notesitem notesItem = new notesitem();
                            notesItem.setFileurl(result.toString());
                            notesItem.setFilename(getOriginalFilename(result));
                            notesList.add(notesItem);
                            uploadFileToFirebase(result); // Upload the file to Firebase
                        } else {
                            // Notify the user that video or audio files are not allowed
                            Toast.makeText(requireContext(), "Video and audio files are not allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    notesAdapter.notifyDataSetChanged(); // Notify the adapter about the data change

                }
            }
        });

        recyclerView = view.findViewById(R.id.recyclernotes);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call your method to fetch data from Firebase or perform any other operation
                fetchFilesFromFirebaseStorage();
            }
        });
        notesList = new ArrayList<>();
        notesAdapter = new notesadapter(notesList, requireContext(),usertype);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch data from Firebase storage
        fetchFilesFromFirebaseStorage();

        if(usertype.equals("Teacher")) {
            FloatingActionButton fabAdd = getActivity().findViewById(R.id.fabAdd);
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch the file picker
                    filePickerLauncher.launch("*/*"); // You can specify the MIME type or use "*/*" for all file types
                }
            });

        }

        recyclerView.setAdapter(notesAdapter);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {


                BottomNavigationView bottomnav = getActivity().findViewById(R.id.bottomNavigation);
                if (bottomnav != null) {

                    bottomnav.setSelectedItemId(R.id.menu_lectures);
                }
                // requireActivity().finishAffinity(); if you wish to close the app
            }
        });



        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchFilesFromFirebaseStorage();
    }
    // Add these helper methods to check if the file is video or audio
    private boolean isVideoFile(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("video");
    }

    private boolean isAudioFile(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("audio");
    }

    private void fetchFilesFromFirebaseStorage() {
        // Create a Firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the filedetails collection and order by timestamp
        db.collection("filedetails")
                .whereEqualTo("uploaded by", username) // Filter documents by the username
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notesList.clear(); // Clear the list before adding new items

                    // Check if there are any documents in the collection
                    if (queryDocumentSnapshots.isEmpty()) {
                        tvmsg.setVisibility(View.VISIBLE);
                        tvmsg.setText("No notes available");
                        return;
                    }

                    tvmsg.setVisibility(View.INVISIBLE);

                    // Iterate through the documents
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String downloadUrl = document.getString("downloadUrl");
                        String filename = document.getString("filename");

                        // Add the file to the list
                        notesList.add(new notesitem(downloadUrl, filename));
                    }

                    // Notify the adapter that the dataset has changed
                    notesAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that may occur while fetching files from Firestore
                    Log.e("Firestore", "Error fetching files: " + e.getMessage());
                });
    }
    private void storeFileDetailsInFirestore(String downloadUrl, String filename) {
        // Create a Firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map to store the file details
        Map<String, Object> fileData = new HashMap<>();
        fileData.put("downloadUrl", downloadUrl);
        fileData.put("filename", filename);
        fileData.put("timestamp", System.currentTimeMillis());
        fileData.put("uploaded by",username);
        // Add the data to Firestore
        db.collection("filedetails")
                .add(fileData)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(e -> {
                    // Handle failures
                    Log.e("Firestore", "Failed to store file details in Firestore: " + e.getMessage());
                });
    }




    private void uploadFileToFirebase(Uri fileUri) {
        if (requireContext() == null) {
            // Handle the case where the context is null
            return;
        }
        if (isAdded()) {//isAdded() before accessing context it checks
            // whether the fragment is attached to activity or not

            String filename = getOriginalFilename(fileUri);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("files/" + username + "/" + filename);

            // Get MIME type from ContentResolver
            String mimeType = requireContext().getContentResolver().getType(fileUri);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(mimeType)
                    .setContentDisposition("inline; filename=\"" + filename + "\"")
                    .build();

            storageRef.putFile(fileUri, metadata)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Log metadata information
                        Log.d("Upload", "Metadata - Content Type: " + metadata.getContentType());
                        Log.d("Upload", "Metadata - Content Disposition: " + metadata.getContentDisposition());

                        // Get the download URL of the uploaded file
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Handle the download URL as needed
                            String downloadUrl = downloadUri.toString();
                            // For example, you can store the downloadUrl in Firestore
                            Log.d("Upload", "Filename: " + filename);
                            Log.d("Upload", "Download URL: " + downloadUrl);
                            storeFileDetailsInFirestore(downloadUrl, filename);
                            Toast.makeText(requireContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        // You may want to show a message or log an error
                        Toast.makeText(requireContext(), "File upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }



    private String getOriginalFilename(Uri fileUri) {
        String originalFilename = null;
        try {
            ContentResolver contentResolver = requireContext().getContentResolver();
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                originalFilename = cursor.getString(displayNameIndex);
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originalFilename;
    }
}
