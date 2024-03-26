package com.example.elearning;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class notesadapter extends RecyclerView.Adapter<notesadapter.viewholder> {
    List<notesitem> noteslist;
    Context context;
    String usertype;

    public notesadapter(List<notesitem> noteslist, Context context,String usertype) {
        this.noteslist = noteslist;
        this.context = context;
        this.usertype=usertype;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notesrecyclercardview, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull notesadapter.viewholder holder, int position) {
        notesitem notes = noteslist.get(position);

        // Set filename
        holder.tvfilename.setText(notes.getFilename());

        // Determine file type and set corresponding icon
        int fileTypeIcon = getFileTypeIcon(notes.getFilename());
        holder.tvfilename.setCompoundDrawablesWithIntrinsicBounds(fileTypeIcon, 0, 0, 0);
        //itemView this is for entire row in recyclerview

        // Set click listener to open the file
        holder.tvfilename.setOnClickListener(v -> openFile(notes.getFileurl(), getMimeType(notes.getFilename())));
        holder.tvfilename.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (usertype.equals("Teacher")) {
                    showDeleteDialog(notes);
                    return true;
                }
                return false;
            }
        });

    }

    private int getFileTypeIcon(String filename) {
        if (isPDF(filename)) {
            return R.drawable.ic_pdf;  // Replace with your PDF icon resource
        } else if (isPPT(filename)) {
            return R.drawable.ic_ppt;  // Replace with your PPT icon resource
        } else if (isDOCX(filename)) {
            return R.drawable.ic_docx;  // Replace with your DOCX icon resource
        } else if(isXLSX(filename))
        {
            return R.drawable.ic_xlsx;
        }
        else {
            return R.drawable.ic_defaultfiletype;  // Replace with your default icon resource
        }
    }

    @Override
    public int getItemCount() {
        return noteslist.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView tvfilename;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            tvfilename = itemView.findViewById(R.id.tvfilename);
        }
    }

    private void showDeleteDialog(final notesitem notes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the note
                    deleteNote(notes);
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void deleteNote(notesitem notes) {
        // Remove the note from the list and notify the adapter
        noteslist.remove(notes);
        notifyDataSetChanged();

        // Delete the note data from Firebase Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("filesdata")
                .whereEqualTo("filename", notes.getFilename()) // Assuming filename is unique and can be used as a unique identifier
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the document ID of the note to be deleted
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Delete the note document from Firestore
                        firestore.collection("filesdata")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Note data successfully deleted from Firestore
                                    Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure to delete note data from Firestore
                                    Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "Note not found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to query note data from Firestore
                    Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show();
                });
    }


    private void openFile(String fileUrl, String mimeType) {
        if (isPDF(mimeType) || isPPT(mimeType) || isDOCX(mimeType) || isXLSX(mimeType)) {
            openInGoogleDriveViewer(fileUrl);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), mimeType);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Use createChooser to allow the user to select the app
                Intent chooser = Intent.createChooser(intent, "Open File With");
                context.startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                // Handle the case where no viewer is installed
                Toast.makeText(context, "No viewer installed for this file type", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openInGoogleDriveViewer(String fileUrl) {
        String googleDriveViewerUrl = "https://drive.google.com/viewerng/viewer";

        // Set the zoom level to standard (change the zoom parameter as needed)
        String fullUrl = googleDriveViewerUrl + "?embedded=true&url=" + Uri.encode(fileUrl) + "&zoom=standard";

        // Build an AlertDialog to allow the user to choose between viewing and downloading
        new AlertDialog.Builder(context)
                .setTitle("Choose Action")
                .setMessage("Do you want to view or download the file?")
                .setPositiveButton("View", (dialog, which) -> {
                    // Open the file in Google Drive Viewer
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No viewer installed for this file type", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Download", (dialog, which) -> {
                    // Trigger download
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    context.startActivity(browserIntent);
                })
                .show();
    }


    private String getMimeType(String filename) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        // If mimeType is still null, check for specific PPT and DOCX extensions
        if (mimeType == null || mimeType.equals("*/*")) {
            if (isPPT(filename)) {
                mimeType = "application/vnd.ms-powerpoint";
            } else if (isDOCX(filename)) {
                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else {
                mimeType = "application/pdf"; // Set a default MIME type for other file types (e.g., PDF)
            }
        }

        Toast.makeText(context, "mimetype: " + mimeType, Toast.LENGTH_SHORT).show();
        return mimeType;
    }


    // Add these methods to check file types
    private boolean isPDF(String mimeType) {
        return mimeType != null && (mimeType.startsWith("application/pdf") || mimeType.endsWith(".pdf"));
    }

    private boolean isPPT(String mimeType) {
        return mimeType != null && (mimeType.startsWith("application/vnd.ms-powerpoint") || mimeType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.presentation") || mimeType.endsWith(".ppt"));
    }

    private boolean isDOCX(String mimeType) {
        return mimeType != null && (mimeType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || mimeType.endsWith(".docx"));
    }
    private boolean isXLSX(String mimeType) {
        return mimeType != null && (mimeType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || mimeType.endsWith(".xlsx"));
    }
}
