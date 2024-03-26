package com.example.elearning;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class addedadapter extends RecyclerView.Adapter<addedadapter.ViewHolder> {

    private List<discoveritem> addedlist;
    private Context context;
    String studentname;



    public addedadapter(List<discoveritem> addedlist, Context context,String studentname) {
        this.addedlist = addedlist;
        this.context = context;
        this.studentname=studentname;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discoverrecyclercardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        discoveritem discoverItem = addedlist.get(position);
        String  teachername=discoverItem.getUsername();
        Glide.with(context)
                .load(discoverItem.getImageResource())
                .placeholder(R.drawable.default_profile_picture) // Placeholder image while loading
                .error(R.drawable.default_profile_picture) // Error image if loading fails
                .into(holder.profileImage);

        // Set the username
        holder.username.setText(discoverItem.getUsername());

        //we have to load teachers lecture and notes so passsing teacher name
        //passing usertype as student to restrict them from adding or removing
        //lectures and notes
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, studentviewteacher.class);
                intent.putExtra("username", teachername);
                intent.putExtra("usertype", "Student");
                context.startActivity(intent);
            }

        });

        // Set OnClickListener for the username
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, studentviewteacher.class);
                intent.putExtra("username", teachername);
                intent.putExtra("usertype", "Student");
                context.startActivity(intent);

            }
        });

        holder.addButton.setBackgroundColor(Color.GRAY);
        holder.addButton.setText("Remove");
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here we only want to remove user
                  SessionManager sm=new SessionManager(context.getApplicationContext());

                    removeStudentFromAddedTeachers(teachername, sm.getUsername());

                }

        });
    }

    @Override
    public int getItemCount() {
        return addedlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username;
        Button addButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);

            addButton = itemView.findViewById(R.id.add_button);
        }
    }

    private void removeStudentFromAddedTeachers(String teacherUsername, String studentUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentDetailsRef = db.collection("studentdetails");

        // Query for the document of the current student using the student's username
        studentDetailsRef.whereEqualTo("username", studentUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot studentDoc : queryDocumentSnapshots.getDocuments()) {
                        // Get the document ID
                        String studentDocId = studentDoc.getId();

                        // Update the addedteachers array for the current student
                        List<String> addedTeachers = (List<String>) studentDoc.get("addedteachers");
                        if (addedTeachers != null && addedTeachers.contains(teacherUsername)) {
                            // If the teacher is in the addedteachers list, remove them from the list
                            addedTeachers.remove(teacherUsername);

                            // Update the student's document with the modified addedteachers array
                            studentDetailsRef.document(studentDocId).update("addedteachers", addedTeachers)
                                    .addOnSuccessListener(aVoid -> {
                                        // Remove the teacher from the RecyclerView
                                        int position = findTeacherPosition(teacherUsername);
                                        if (position != -1) {
                                            addedlist.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, addedlist.size());
                                        }
                                        Toast.makeText(context, "Teacher removed successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("firebase", "Error updating student document: " + e.getMessage());
                                        Toast.makeText(context, "Failed to remove teacher", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // If the teacher is not in the addedteachers list, show a message indicating that
                            Toast.makeText(context, "Teacher not found in added list", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("firebase", "Error querying student document: " + e.getMessage());
                    Toast.makeText(context, "Failed to query student details", Toast.LENGTH_SHORT).show();
                });
    }

    private int findTeacherPosition(String teacherUsername) {
        for (int i = 0; i < addedlist.size(); i++) {
            if (addedlist.get(i).getUsername().equals(teacherUsername)) {
                return i;
            }
        }
        return -1; // Teacher not found
    }



}
