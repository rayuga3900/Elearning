package com.example.elearning;

import static com.example.elearning.HttpClient.getClient;

import android.content.Intent;

 import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.elearning.databinding.LoginActivityBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class login extends AppCompatActivity {
    private LoginActivityBinding binding;
    private Button btnlogin;
    private EditText tbuname;
    private EditText tbpswd;
    private TextInputLayout tiluname;
    private TextInputLayout tilpswd;
    private TextView tvres;
    String usertype;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        binding=LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnlogin=findViewById(R.id.btnlogin);
        tbuname=findViewById(R.id.tbuname);
        tbpswd=findViewById(R.id.tbpswd1);
        tiluname=findViewById(R.id.tiluname);
        tilpswd=findViewById(R.id.tilpswd1);
        tvres=findViewById(R.id.tvres);
        SessionManager sm=new SessionManager(getApplicationContext());
        Intent intent=getIntent();

          usertype=intent.getStringExtra("usertype");
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
           Intent intent1=new Intent(login.this, register.class);
           intent1.putExtra("usertype",usertype);
           intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
           startActivity(intent1);
                setEnabled(false);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  uname=tbuname.getText().toString().trim();
                String pswd=tbpswd.getText().toString().trim();
                if(uname.isEmpty())
                {
                    tiluname.setError("Username cannot be empty");
                    return; //stop processing
                }
                else
                {
                    tiluname.setError(null);
                }
                if(pswd.isEmpty())
                {
                    tilpswd.setError("Password cannot be Empty");
                }
                else {
                    tilpswd.setError(null);

                    RetrofitClient rf=new RetrofitClient();
                    sqlinterface sql=rf.getClient(getApplicationContext());
                    if ("Admin".equals(usertype)) {
                        Call<String> call = sql.loginuser(usertype,uname, pswd);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String res = response.body();
                                    tvres.append(res);


                                    if (res.equals("Login success") ) {
                                        sm.createLoginSession(uname,usertype);
                                        loadProfilePicture(uname,usertype);
                                        Intent intent = new Intent(login.this, Admin.class);
                                       //Intent intent=new Intent(login.this, firebasedemo.class);
                                        intent.putExtra("usertype", "Admin");
                                        intent.putExtra("username",uname);
                                        startActivity(intent);
                                    }

                                    if (res.equals("invalid credentials")) {
                                        tvres.setText("");
                                        tvres.setTextColor(Color.RED);
                                        tvres.append("invalid credentials");
                                    }
                                    if (res.equals("username not found")) {
                                        tvres.setText("");
                                        tvres.setTextColor(Color.RED);
                                        tvres.append("username not found");
                                    }
                                }


                            }


                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                tvres.append("\n" + t.getMessage());
                            }
                        });
                    }
                    else if("Teacher".equals(usertype))
                    {
                        Call<String> call=sql.loginuser(usertype,uname,pswd);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String res = response.body();
                                    tvres.append(res);


                                    if (res.equals("Login success")) {
                                        sm.createLoginSession(uname,usertype);
                                        loadProfilePicture(uname,usertype);
                                        Intent intent = new Intent(login.this, Teacher.class);
                                        intent.putExtra("usertype", "Teacher");
                                        intent.putExtra("username",uname);
                                        startActivity(intent);
                                    }

                                    if (res.equals("Wait for admin  to accept your request")) {
                                        Toast.makeText(login.this, "Wait for admin  to accept your request", Toast.LENGTH_SHORT).show();
                                    }
                                    if (res.equals("invalid credentials")) {
                                        tvres.setText("");
                                        tvres.setTextColor(Color.RED);
                                        tvres.append("invalid credentials");
                                    }
                                    if (res.equals("username not found")) {
                                        tvres.setText("");
                                        tvres.setTextColor(Color.RED);
                                        tvres.append("username not found");
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                tvres.append("\n" + t.getMessage());
                            }
                        });
                    }
                    else if("Student".equals(usertype))
                    {
                        Call<String> call=sql.loginuser(usertype,uname,pswd);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String res = response.body();
                                    tvres.append(res);


                                    if (res != null) {

                                        String message = res;
                                        tvres.append("\n" + message);
                                    }
                                    if (res.equals("Login success")) {
                                        sm.createLoginSession(uname,usertype);
                                        loadProfilePicture(uname,usertype);
                                        Intent intent = new Intent(login.this, Student.class);
                                        intent.putExtra("usertype", "Student");
                                        intent.putExtra("username",uname);
                                        startActivity(intent);
                                    }
                                    if (res.equals("invalid credentials")) {
                                        tvres.setText("");
                                        tvres.setTextColor(Color.RED);
                                        tvres.append("invalid credentials");
                                    }
                                    if (res.equals("username not found")) {
                                        tvres.setText("");
                                        tvres.setTextColor(Color.RED);
                                        tvres.append("username not found");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                tvres.append("\n" + t.getMessage());
                            }
                        });
                    }
                }


            }
        });
    }
    //object doesnt exist failed to get downloadurl cause we are checking if profile image is there or not
    //then we are uploading default profile image and username in firebase

    private void loadProfilePicture(String username, String usertype) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + usertype + "/" + username + ".jpg");


        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            //profileimage exists dont do anything
        }).addOnFailureListener(exception -> {
            // Profile image doesn't exist, check if the username exists in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(usertype.equals("Admin")) {
                CollectionReference userDetailsRef = db.collection("admindetails");
                userDetailsRef.whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                // Username doesn't exist, upload default image and username
                                uploadDefaultProfile(username, usertype);
                            } else {

                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error querying document", e);
                        });
            }
            else if(usertype.equals("Teacher"))
            {
                CollectionReference userDetailsRef = db.collection("teacherdetails");
                userDetailsRef.whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                // Username doesn't exist, upload default image and username
                                uploadDefaultProfile(username, usertype);
                            } else {

                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error querying document", e);
                        });
            }
            else if(usertype.equals("Student"))
            {

                CollectionReference userDetailsRef = db.collection("studentdetails");
                userDetailsRef.whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                // Username doesn't exist, upload default image and username
                                uploadDefaultProfile(username, usertype);
                            } else {

                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error querying document", e);
                        });
            }


        });
    }
    private void uploadDefaultProfile(String username, String usertype) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + usertype + "/" + username + ".jpg");

        // Get reference to the default profile picture
        Uri defaultImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.default_profile_picture);

        // Upload the default profile picture to Firebase
        storageRef.putFile(defaultImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, load it

                })
                .addOnFailureListener(e -> {
                    // Handle errors during the upload
                    Log.e("FirebaseStorage", "Failed to upload default profile picture: " + e.getMessage());
                });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
      if(usertype.equals("Admin")) {

          CollectionReference userDetailsRef = db.collection("admindetails");
          Map<String, Object> userData = new HashMap<>();
          userData.put("username", username);
          userData.put("profileImageLink", defaultImageUri.toString());
          userDetailsRef
                  .add(userData)
                  .addOnSuccessListener(documentReference -> {
                      Log.d("Firestore", "Default profile picture uploaded and username stored with ID: " + documentReference.getId());
                  })
                  .addOnFailureListener(e -> {
                      Log.w("Firestore", "Error adding default profile picture and username to Firestore", e);
                  });
      }
      else if(usertype.equals("Teacher"))
      {

          CollectionReference userDetailsRef = db.collection("teacherdetails");
          Map<String, Object> userData = new HashMap<>();
          userData.put("username", username);
          userData.put("profileImageLink", defaultImageUri.toString());
          userDetailsRef
                  .add(userData)
                  .addOnSuccessListener(documentReference -> {
                      Log.d("Firestore", "Default profile picture uploaded and username stored with ID: " + documentReference.getId());
                  })
                  .addOnFailureListener(e -> {
                      Log.w("Firestore", "Error adding default profile picture and username to Firestore", e);
                  });
      }
      else if(usertype.equals("Student"))
      {

          CollectionReference userDetailsRef = db.collection("studentdetails");
          Map<String, Object> userData = new HashMap<>();
          userData.put("username", username);
          userData.put("profileImageLink", defaultImageUri.toString());
          userDetailsRef
                  .add(userData)
                  .addOnSuccessListener(documentReference -> {
                      Log.d("Firestore", "Default profile picture uploaded and username stored with ID: " + documentReference.getId());
                  })
                  .addOnFailureListener(e -> {
                      Log.w("Firestore", "Error adding default profile picture and username to Firestore", e);
                  });
      }
    }



}
