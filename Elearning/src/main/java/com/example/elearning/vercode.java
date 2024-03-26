package com.example.elearning;

import static android.graphics.Color.RED;
import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.elearning.HttpClient.getClient;

import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.elearning.databinding.EmailVerificationBinding;
import com.example.elearning.databinding.RegisterActivityBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class vercode extends AppCompatActivity {
    private String receivedcode;
    public TextInputLayout tilvercode;
    public EditText tbvercode;
    public TextView tvresend;
    public Button btnsub;
    public TextView tvres1;
    public EmailVerificationBinding binding;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);
        binding= EmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String usertype=extras.getString("Usertype");
        String username=extras.getString("Username");
        String email=extras.getString("Email");
        btnsub=findViewById(R.id.btnsub1);
        tvresend=findViewById(R.id.tvresend);
        tilvercode=findViewById(R.id.tilvercode);
        tbvercode=findViewById(R.id.tbvercode);
       tvres1=findViewById(R.id.tvres1);

       toolbar=findViewById(R.id.toolbar7);
       setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setHomeButtonEnabled(true);

        addBackButtonCallback();
        RetrofitClient rf=new RetrofitClient();
        sqlinterface sql=rf.getClient(getApplicationContext());
//sending verification code to user(based on type) via email and storing that code in receivedcode
        //for checking if user entered correct verification code
        if ("Admin".equals(usertype)) {
            Call<String> call=sql.sendemail(email);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()&&response.body()!=null)
                    {
                      receivedcode= response.body();
                    /*    if(vercode.equals(receivedcode))
                        //check entered code equals to one send on email??
                        {
                            Intent intent1=new Intent(vercode.this, Admin.class);
                            intent1.putExtra("Usertype","Admin");
                        }*/
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    tvres1.setTextColor(RED);
                    tvres1.setText("Problem with receiving code");
                }
            });
        } else if ("Teacher".equals(usertype)) {
            Call<String> call=sql.sendemail(email);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()&&response.body()!=null)
                    {
                        receivedcode= response.body();
                     /*   if(vercode.equals(receivedcode))
                        //check entered code equals to one send on email??
                        {
                            Intent intent1=new Intent(vercode.this, Admin.class);
                            intent1.putExtra("Usertype","Teacher");
                        }*/
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    tvres1.setTextColor(RED);
                    tvres1.setText("Problem with receiving code");
                }
            });
        } else if ("Student".equals(usertype)) {
            Call<String> call=sql.sendemail(email);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()&&response.body()!=null)
                    {
                        receivedcode= response.body();
                       /* if(vercode.equals(receivedcode))
                        //check entered code equals to one send on email??
                        {
                            Intent intent1=new Intent(vercode.this, Admin.class);
                            intent1.putExtra("Usertype","Teacher");
                        }*/
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    tvres1.setTextColor(RED);
                    tvres1.setText("Problem with receiving code");
                }
            });
        }
        tvresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send email again and receivedcode changes
                Snackbar.make(findViewById(R.id.vercode1),
                        "Verification code has been resent",
                        Snackbar.LENGTH_SHORT)
                        .show();//snackber is directly used via code it doesnt appear in design
                if ("Admin".equals(usertype)) {
                    Call<String> call=sql.sendemail(email);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()&&response.body()!=null)
                            {
                                receivedcode= response.body();
                    /*    if(vercode.equals(receivedcode))
                        //check entered code equals to one send on email??
                        {
                            Intent intent1=new Intent(vercode.this, Admin.class);
                            intent1.putExtra("Usertype","Admin");
                        }*/
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            tvres1.setTextColor(RED);
                            tvres1.setText("Problem with receiving code");
                        }
                    });
                } else if ("Teacher".equals(usertype)) {
                    Call<String> call=sql.sendemail(email);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()&&response.body()!=null)
                            {
                                receivedcode= response.body();
                     /*   if(vercode.equals(receivedcode))
                        //check entered code equals to one send on email??
                        {
                            Intent intent1=new Intent(vercode.this, Admin.class);
                            intent1.putExtra("Usertype","Teacher");
                        }*/
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            tvres1.setTextColor(RED);
                            tvres1.setText("Problem with receiving code");
                        }
                    });
                } else if ("Student".equals(usertype)) {
                    Call<String> call = sql.sendemail(email);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                receivedcode = response.body();
                       /* if(vercode.equals(receivedcode))
                        //check entered code equals to one send on email??
                        {
                            Intent intent1=new Intent(vercode.this, Admin.class);
                            intent1.putExtra("Usertype","Teacher");
                        }*/
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            tvres1.setTextColor(RED);
                            tvres1.setText("Problem with receiving code");
                        }
                    });
                }
            }
        });

        btnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vercode = tbvercode.getText().toString().trim();

                if (TextUtils.isEmpty(vercode)) {
                    tilvercode.setError("Verification code can't be empty");
                    return;
                } else {

                    tilvercode.setError(null);
                    tilvercode.setErrorEnabled(false);
                    if("Admin".equals(usertype))
                    {
                       // Toast.makeText(vercode.this, receivedcode, Toast.LENGTH_SHORT).show();
                        if(vercode.equals(receivedcode))
                        {

                            Call<String>call=sql.verifyuser(username,usertype,"True");
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String rm = response.body();
                                        tvres1.append(rm);

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                        tvres1.append("\n"+t.getMessage());
                                }

                            });
                            Toast.makeText(vercode.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(vercode.this, login.class);
                            intent.putExtra("usertype", "Admin");
                            startActivity(intent);
                        }
                        else
                        {
                            tvres1.setTextColor(RED);
                            tvres1.setText("Incorrect verification code");
                        }
                    }
                    else if("Teacher".equals(usertype))
                    {
                        if(vercode.equals(receivedcode))
                        {
                            Call<String>call=sql.verifyuser(username,usertype,"True");
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String rm = response.body();
                                        tvres1.append(rm);
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    tvres1.append("\n"+t.getMessage());
                                }

                            });
                            Toast.makeText(vercode.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(vercode.this, login.class);
                            intent.putExtra("usertype", "Teacher");
                            startActivity(intent);
                        }
                        else
                        {
                            tvres1.setTextColor(RED);
                            tvres1.setText("Incorrect verification code");
                        }
                    }
                    else if("Student".equals(usertype))
                    {
                        if(vercode.equals(receivedcode))
                        {
                            Call<String>call=sql.verifyuser(username,usertype,"True");
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String rm = response.body();
                                        tvres1.append(rm);
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    tvres1.append("\n"+t.getMessage());
                                }

                            });
                            Toast.makeText(vercode.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(vercode.this, login.class);
                            intent.putExtra("usertype", "Student");
                            startActivity(intent);
                        }
                        else
                        {
                            tvres1.setTextColor(RED);
                            tvres1.setText("Incorrect verification code");
                        }
                    }




                }
              //  btnsub.setEnabled(false);//to make your button one time clickable
            }

        });

    }
    private void addBackButtonCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back button behavior is disabled
            }
        });
    }


}
