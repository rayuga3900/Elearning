package com.example.elearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class discoverfrag extends Fragment {
    private RecyclerView recyclerViewDiscover;
    private discoveradapter adapter;
    private List<discoveritem> discoverItemList;
    String username;

    private FirebaseFirestore db;

    public discoverfrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discoverfrag, container, false);
        db = FirebaseFirestore.getInstance();
        discoverItemList = new ArrayList<>();
        Intent intent=getActivity().getIntent();
        username=intent.getStringExtra("username");//username of student
        adapter = new discoveradapter(discoverItemList,getContext(),username);

        fetchDataFromFirebase();
        recyclerViewDiscover = view.findViewById(R.id.recyclerdiscover);
        recyclerViewDiscover.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDiscover.setAdapter(adapter);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        db.collection("teacherdetails")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        discoverItemList.clear();
                        for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String username = documentChange.getDocument().getString("username");
                                String profileImageLink = documentChange.getDocument().getString("profileImageLink");

                                discoverItemList.add(new discoveritem( profileImageLink,username));
                                adapter.notifyDataSetChanged();

                            }
                        }
                    }
                });
    }
}
