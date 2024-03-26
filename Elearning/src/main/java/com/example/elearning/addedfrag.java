package com.example.elearning;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class addedfrag extends Fragment {

    private RecyclerView recyclerviewadded;
    private addedadapter adapter;
    private List<discoveritem> addedlist;
    String username;


    private FirebaseFirestore db;
    private TextView tvmsg;
    public addedfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_addedfrag, container, false);
        db = FirebaseFirestore.getInstance();
        addedlist = new ArrayList<>();
        Intent intent=getActivity().getIntent();
        username=intent.getStringExtra("username");//username of student
        adapter = new addedadapter(addedlist,getContext(),username);
        tvmsg=view.findViewById(R.id.tvaddmsg);
        tvmsg.setVisibility(View.INVISIBLE);
        fetchDataFromFirebase();
        recyclerviewadded = view.findViewById(R.id.recycleradded);
        recyclerviewadded.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewadded.setAdapter(adapter);
        return view;
    }



    private void fetchDataFromFirebase() {

        db.collection("studentdetails")
                .whereEqualTo("username", username) // Query for the current student's document
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                // Retrieve the addedteachers array from the current student's document
                                List<String> addedTeachers = (List<String>) documentChange.getDocument().get("addedteachers");
                                if( addedTeachers.isEmpty())
                                {
                                    tvmsg.setVisibility(View.VISIBLE);
                                    tvmsg.setText("No teacher added ");

                                }
                                // Iterate through the addedteachers array
                                else if (addedTeachers != null) {

                                    for (String teacherUsername : addedTeachers) {

                                        // Fetch the details of each teacher from the teacherdetails collection
                                        db.collection("teacherdetails")
                                                .whereEqualTo("username", teacherUsername)
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {

                                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                                        // Get the teacher's details
                                                        String username = documentSnapshot.getString("username");
                                                        String profileImageLink = documentSnapshot.getString("profileImageLink");

                                                        // Add the teacher to the addedlist
                                                        addedlist.add(new discoveritem(profileImageLink, username));
                                                        adapter.notifyDataSetChanged();

                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle any errors

                                                });
                                    }
                                }
                            }
                        }
                    }
                });
    }

}