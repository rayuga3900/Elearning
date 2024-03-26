package com.example.elearning;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static android.graphics.Color.RED;
import static com.example.elearning.HttpClient.getClient;

import android.content.Intent;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.elearning.databinding.RegisterActivityBinding;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class register extends AppCompatActivity {
    private RegisterActivityBinding binding;
    private TextView tvresult;
    private TextView tvlog;
    private Button btnreg;
    private EditText tbname;
    private EditText tbemail;
    private EditText tbusername;
    private EditText tbpswd;
    private EditText tbcpswd;
    private TextInputLayout tilname;
    /*Textinputlayout is used for floating hint
     and also for validation message
    */
    private TextInputLayout tilemail;
    private TextInputLayout tilusername;
    private TextInputLayout tilpswd;
    private TextInputLayout tilcpswd;
    //String validpswd="^[!$";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        binding= RegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //all code should be after the setContentView(binding.getRoot()); or wont work
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        String usertype=extras.getString("usertype");
        tvlog=findViewById(R.id.tvlogin);
        tvlog.setClickable(true);
        tvlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(register.this, "Clicked login text", Toast.LENGTH_SHORT).show();
                if ("Admin".equals(usertype)) {

                    Intent intent1 = new Intent(register.this, login.class);
                    intent1.putExtra("usertype", "Admin");
                    startActivity(intent1);
                } else if ("Teacher".equals(usertype)) {

                    Intent intent1= new Intent(register.this, login.class);
                    intent1.putExtra("usertype", "Teacher");
                    startActivity(intent1);
                } else if ("Student".equals(usertype)) {

                    Intent intent1 = new Intent(register.this, login.class);
                    intent1.putExtra("usertype", "Student");
                    startActivity(intent1);
                }
                else {
                    tvresult.setText("Error cant redirect");
                }
            }});



        tvresult=findViewById(R.id.tvresult);


        /*Textinputlayout helps to give float hint
         ,validation message etc .it provides enhance user exprience
         for text input field
         */
        tilname=findViewById(R.id.tilname);
        tilemail=findViewById(R.id.tilemail);
        tilusername=findViewById(R.id.tilusername);
        tilpswd=findViewById(R.id.tilpswd);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent=new Intent(register.this, MainActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                setEnabled(false);
            }
        });
        tilcpswd=findViewById(R.id.tilcpswd);
        btnreg=findViewById(R.id.btnreg);
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        /*
            1)Take input on button click only not on onCreate() because on
            onCreate the screen is getting ready user doesnt interact at that time
            eg you will ask question to someone when they have entered room not
            before entering
            2)when we call getText() for input field we get CharSequence which is
            readable sequence of characters .so you need to convert
            it to string
            using toString() method and also use trim() to remove whitespaces
            below is the example:
        */
                tbname=findViewById(R.id.tbname);
                String name=tbname.getText().toString().trim();

                tbemail=findViewById(R.id.tbemail);
                String email=tbemail.getText().toString().trim();
                tbusername=findViewById(R.id.tbusername);
                String username=tbusername.getText().toString().trim();
                tbpswd=findViewById(R.id.tbpswd);
                String pswd=tbpswd.getText().toString().trim();
                tbcpswd=findViewById(R.id.tbcpswd);
                String cpswd=tbcpswd.getText().toString().trim();

                if(name.isEmpty())
                {

                    tilname.setErrorEnabled(true);
                    tilname.setError("Name cannot be empty");

                  //  Toast.makeText(register.this, "name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
                }
               else
                {
                    tilname.setError(null);
                    tilname.setErrorEnabled(false);
                }
               // Toast.makeText(getApplicationContext(),name, Toast.LENGTH_SHORT).show();
               // tvregister.setText(name);
                if(email.isEmpty())
                {
                   // tilname.setError(null);
                    tilemail.setError("Email should not be empty");
                    Toast.makeText(register.this, "email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                   // tilname.setError(null);
                    tilemail.setError("Invalid email format");
                    Toast.makeText(register.this, "email is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                     tilemail.setError(null);
                     tilemail.setErrorEnabled(false);
                }

                if(username.isEmpty())
                {
                    //tilemail.setError(null);
                   // tilemail.setErrorEnabled(false);
                    tilusername.setError("Username cannot be empty");
                    return;
                }
                else
                {
                    tilusername.setError(null);
                    tilusername.setErrorEnabled(false);
                }
               if(pswd.isEmpty())
                {
                   // tilusername.setError(null);
                    tilpswd.setError("Password cannot be empty");
                    return;
                }
                else if(pswd.length()<=6)
                {
                   // tilusername.setError(null);
                    tilpswd.setError("password length must be greater than 6 character");
                    return;
                }
                else
               {
                   tilpswd.setError(null);
                   tilpswd.setErrorEnabled(false);
               }
              if(cpswd.isEmpty())
              {
                 // tilpswd.setError(null);
                  tilcpswd.setError("Confirm Password cannot be empty");
                  return;
              }
                else if(!pswd.equals(cpswd))
               {
                   //tilpswd.setError(null);
                   tilcpswd.setError("password didnt match");
                   return;
               }
               else
               {
                   tilcpswd.setError(null);
                   tilcpswd.setErrorEnabled(false);

                    RetrofitClient rf=new RetrofitClient();
                    sqlinterface sql=rf.getClient(getApplicationContext());
                   tvresult.append(usertype);
                   /*sending email to respective users and then on other page(vercode)
                   inserting data in database
                    */
                   if("Admin".equals(usertype)) {

                      tvresult.append("Came from admin");
                       //sending data to admin table in the database
                       Call<String> call=sql.registeruser(usertype,name,email,username,pswd);
                       call.enqueue(new Callback<String>() {
                           @Override
                           public void onResponse(Call<String> call, Response<String> response) {
                               if (response.isSuccessful() && response.body() != null) {
                                   String rm = response.body();
                                   tvresult.append(rm);
                                   if(rm.contains("Record Inserted"))
                                   //str.contains("parameter")->checks if the string parameter is
                                       // there anywhere in the given string str
                                   {
                                       Intent intent1=new Intent(register.this, vercode.class);
                                       intent1.putExtra("Usertype","Admin");
                                       intent1.putExtra("Username",username);
                                       intent1.putExtra("Email",email);
                                       startActivity(intent1);
                                   }
                                   else if(rm.contains("username already taken"))
                                   {
                                       tvresult.setTextColor(RED);
                                       tvresult.setText("username already taken");
                                   }
                               }
                           }

                           @Override
                           public void onFailure(Call<String> call, Throwable t) {
                               tvresult.append("\n"+t.getMessage());
                           }
                       });

                   }
                   else if("Teacher".equals(usertype)) {
                       Call<String> call=sql.registeruser(usertype,name,email,username,pswd);
                       call.enqueue(new Callback<String>() {
                           @Override
                           public void onResponse(Call<String> call, Response<String> response) {
                               if (response.isSuccessful() && response.body() != null) {
                                   String rm = response.body();
                                    //tvresult.append(rm);
                                   if(rm.contains("Record Inserted"))
                                   {
                                       Intent intent1=new Intent(register.this, vercode.class);
                                       intent1.putExtra("Usertype","Teacher");
                                       intent1.putExtra("Username",username);
                                       intent1.putExtra("Email",email);
                                       startActivity(intent1);
                                   }
                                   else if(rm.contains("username already taken"))
                                   {
                                       tvresult.setTextColor(RED);
                                       tvresult.setText("username already taken");
                                   }
                               }
                           }

                           @Override
                           public void onFailure(Call<String> call, Throwable t) {
                               tvresult.append("\n"+t.getMessage());

                           }
                       });


                   }
                   else if("Student".equals(usertype)) {
                       Call<String> call=sql.registeruser(usertype,name,email,username,pswd);
                       call.enqueue(new Callback<String>() {
                           @Override
                           public void onResponse(Call<String> call, Response<String> response) {
                               if (response.isSuccessful() && response.body() != null) {
                                   String rm = response.body();
                                   tvresult.append(rm);
                                   if(rm.contains("Record Inserted"))
                                   {
                                       Intent intent1=new Intent(register.this, vercode.class);
                                       intent1.putExtra("Usertype","Student");
                                       intent1.putExtra("Username",username);
                                       intent1.putExtra("Email",email);
                                       startActivity(intent1);
                                   }
                                   else if(rm.contains("username already taken"))
                                   {
                                       tvresult.setTextColor(RED);
                                       tvresult.setText("username already taken");
                                   }
                               }
                           }

                           @Override
                           public void onFailure(Call<String> call, Throwable t) {
                               tvresult.append("\n"+t.getMessage());

                           }
                       });


                   }
                   else {
                       tvresult.append("run into problem");
                   }



               }


            }


        });



            }



}
