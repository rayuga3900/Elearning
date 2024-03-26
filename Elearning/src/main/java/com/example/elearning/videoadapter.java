package com.example.elearning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class videoadapter extends RecyclerView.Adapter<videoadapter.ViewHolder> {

    private List<videoitem> videoList;
    private Context context;
    private String usertype;

    public videoadapter(List<videoitem> videoList, Context context, String usertype) {
        this.videoList = videoList;
        this.context = context;
        this.usertype = usertype;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videorecyclercardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        videoitem videoItem = videoList.get(position);

        // Load thumbnail image using Glide (or your preferred image loading library)
        Glide.with(context)
                .load(videoItem.getThumbnailUrl())
                .placeholder(R.drawable.video_placeholder) // Placeholder for loading state
                .into(holder.ivlec);
        // itemView this is for entire row in recyclerview
        holder.tvsub.setText(videoItem.getSubject());
        holder.tvtitle.setText(videoItem.getTitle());
        holder.ivlec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, videoviewactivity.class);
                intent.putExtra("documentId", videoItem.getDocumentId());
                intent.putExtra("title", videoItem.getTitle());
                intent.putExtra("subject", videoItem.getSubject());
                intent.putExtra("videoUrl", videoItem.getThumbnailUrl()); // Use thumbnail URL as video URL
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ImageView ivlec;
        TextView tvsub;
        TextView tvtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivlec = itemView.findViewById(R.id.ivlec);
            tvsub = itemView.findViewById(R.id.tvsub);
            tvtitle = itemView.findViewById(R.id.tvtitle);
            itemView.setOnLongClickListener(this);
        }


            @Override
        public boolean onLongClick(View v) {
            // Handle long press event
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // Get the clicked video item
                videoitem video = videoList.get(position);
                // Show delete   dialog for Teacher only
                if(usertype.equals("Teacher")){ showDeleteDialog(video); }
                return true;
            }
            return false;
        }


        private void showDeleteDialog(final videoitem video) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Video")
                    .setMessage("Are you sure you want to delete this video?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Delete the video from Firestore and Storage
                        deleteVideo(video);
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

        private void deleteVideo(videoitem video) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(video.getThumbnailUrl());

            // Delete video document from Firestore
            firestore.collection("videodetails")
                    .document(video.getDocumentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Delete video file from Storage
                        storageReference.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    // Video successfully deleted from Firestore and Storage
                                    // Now remove the video item from the RecyclerView
                                    videoList.remove(video);
                                    notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure to delete video from Storage
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to delete video document from Firestore
                    });
        }

}
}
