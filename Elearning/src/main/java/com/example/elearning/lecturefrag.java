package com.example.elearning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class lecturefrag extends Fragment {
    String username;
    String usertype;
    private TextView tvmsg;
    private FloatingActionButton fabbtn;
    private RecyclerView recyclerView;
    private videoadapter adapter;
    private List<videoitem> videoList;

    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    public lecturefrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturefrag, container, false);


        recyclerView = view.findViewById(R.id.recyclervideo);
        tvmsg=view.findViewById(R.id.tvmsg3);
        tvmsg.setVisibility(View.INVISIBLE);
        if (getArguments() != null) {
            username = getArguments().getString("username");
            usertype=getArguments().getString("usertype");

        }

        videoList = new ArrayList<>();
        adapter = new videoadapter(videoList, requireContext(),usertype);
        int spanCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                 fetchDataFromFirebase();
            }
        });

        db = FirebaseFirestore.getInstance();
        if(usertype.equals("Teacher")) {
            fabbtn = getActivity().findViewById(R.id.fabAdd);
            fabbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redirect to videouploadactivity to upload a new video
                    startActivity(new Intent(getActivity(), videouploadactivity.class)
                            .putExtra("username", username)
                            .putExtra("usertype", usertype));
                    //these values are send so videouploadactivity can send it to
                    //Teacher.java to load profile else it will show not show name and profile picture
                }
            });
        }
        // Fetch data from Firebase
        fetchDataFromFirebase();

        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        db.collection("videodetails")
                .whereEqualTo("uploaded by", username) // Filter documents by the username
                .orderBy("Timestamp") // Order documents by the "timestamp" field
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the existing videoList before adding new items
                        videoList.clear();

                        if (task.getResult().isEmpty()) {
                            tvmsg.setVisibility(View.VISIBLE);
                            tvmsg.setText("No lectures available");
                            return;
                        }
                        tvmsg.setVisibility(View.INVISIBLE);

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String videoUrl = document.getString("videoUrl");
                            String subject = document.getString("subject");
                            String title = document.getString("title");
                            String documentId = document.getId(); // Retrieve document ID

                            Log.d("FirestoreData", "Video URL: " + videoUrl + ", Subject: " + subject + ", Title: " + title);

                            videoList.add(new videoitem(documentId, videoUrl, subject, title));
                        }

                        // Notify the adapter that the dataset has changed
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Log.e("FirestoreData", "Error fetching documents: ", task.getException());
                    }
                });
    }


       /* videoList.add(new videoitem("https://demo.com","chemistry","periodic table"));
        videoList.add(new videoitem("https://demo2.com","Biology","Cell unit of life"));
        videoList.add(new videoitem("https://demo2.com","Biology","Cell unit of life"));
        videoList.add(new videoitem("https://demo2.com","Biology","Cell unit of life"));
        videoList.add(new videoitem("https://demo2.com","Biology","Cell unit of life"));
        videoList.add(new videoitem("https://demo2.com","Biology","Cell unit of life"));
        adapter.notifyDataSetChanged();*/
    }


