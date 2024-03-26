package com.example.elearning;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.elearning.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ImageButton adminbtn;
    private ImageButton teachbtn;
    private ImageButton studbtn;
    private TextView admintxt;
    private TextView teachtxt;
    private TextView studtxt;
    public SessionManager sm;
    @Override
    protected void onCreate(Bundle savedInstanceState)
            /*
            1.onCreate:this method is where you do basic setup like inflating the layout
            ,initializing variables or restoring the activity's state
            2.(Bundle savedInstanceState) :This parameter is used to receive previous state
            of the activity,if it was saved .it allows you to restore the state of ui or other data
            after the activity has been destroyed and recreated
             */
    {
        super.onCreate(savedInstanceState);
/*inflate converts the xml layout into object(UI) that app can use and display
        without inflating you cannot use the layout
        layoutinflator object converts xml layout into actual UI.
        when you have layout and you want to converted it to view
        there are 2 ways to get  layoutinflater instance
        1)getLayoutInflater() METHOD is called within an activity or fragment.
        .since it is called from the activity or fragment there is no need to pass
        context.it allows you to dynamically create and manipulate views defined in ml layouts
         in your android app
         2)Layoutinflater.from(context).inflate(int resourcepath,viewgroup
         parent,attachtoroot) -when you dont have access to context
         directly you need to pass it explicitly(when you wish to
         inflate a layout outside its code)
  why we need context
  1)access resources eg strings,dimensions,drawable etc
  2)access to system services eg notificationmanager,layoutinflater,audiomanager etc
  3)launching activities and services
  4)access aplication specific information
  5)creating and managing UI

         */

    binding = ActivityMainBinding.inflate(getLayoutInflater());

       setContentView(binding.getRoot());//display all UI elements inside XML layout

        /*
        binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
         instead you could use setContentView(R.layout.activity_main);
         i dont know right now it crashes the app
         */
        this.setTitle("Elearning"); //dynamically changing the title of the app
        setSupportActionBar(binding.toolbar);//provides app toolbar(heading)
        //R.java file contains all resources id we can use it to access views(element) from our java file

      /*adding click listener for different views for same functionality.
      Either you create listener separately for same functionality like i did for adminbtn and admintxt or
      create a commonlistener and add them to it that i  did  for teachbtn and studbtn
       */
        adminbtn=findViewById(R.id.adminimg);//converting xml view(element) to java object by using id
        //image button click event is handled for admin
       adminbtn.setOnClickListener((new View.OnClickListener() {
     @Override
    public void onClick(View v) {
        Toast.makeText(MainActivity.this, "Working admin button", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this, register.class);

         intent.putExtra("usertype","Admin");

         startActivity(intent);
    }

    }));
        admintxt=findViewById(R.id.admin_txt);
        //clickable text event is handled for admin
        admintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "working admin text", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this, register.class);
                //optionally we can add extras to the intent(if needed)
                intent.putExtra("usertype","Admin");
                startActivity(intent);
            }
        });
        teachbtn=findViewById(R.id.teachimg);//converting imagebutton from xml into object in java

        teachtxt=findViewById(R.id.teach_txt);//converting textview from xml into object in java

        View.OnClickListener listenerforteach=new View.OnClickListener()//creating commonlistener so if you want same functionality  you can use it
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Working teacher button and text", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this, register.class);
                //optionally we can add extras to the intent(if needed)
                intent.putExtra("usertype","Teacher");
                startActivity(intent);
            }
        };
        teachtxt.setOnClickListener(listenerforteach);//added listener for teachertxt
        teachbtn.setOnClickListener(listenerforteach);//added listener for teacher image

        studbtn=findViewById(R.id.studimg);//finding by id and converting imagebutton from xml into object in java
        studtxt=findViewById(R.id.stud_txt);//finding by id and converting textview from xml into object in java

        View.OnClickListener listenerforstud=new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Working student button and text", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this, register.class);
                //optionally we can add extras to the intent(if needed)
                intent.putExtra("usertype","Student");
                startActivity(intent);
            }
        };
        studbtn.setOnClickListener(listenerforstud);//added listener for student image
        studtxt.setOnClickListener(listenerforstud);//added listener for studenttxt

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
                setEnabled(false);
            }
        });
        SessionManager sessionManager = new SessionManager(getApplicationContext());
//if user has already loggedin
        if (sessionManager.isLoggedIn()) {
            String usertype = sessionManager.getUserType();
            String username=sessionManager.getUsername();
            if ("Admin".equals(usertype)) {
                startActivity(new Intent(MainActivity.this, Admin.class)
                        .putExtra("username",username)
                        .putExtra("usertype",usertype));
            } else if ("Teacher".equals(usertype)) {
                startActivity(new Intent(MainActivity.this, Teacher.class)
                        .putExtra("username",username)
                        .putExtra("usertype",usertype));
            }
            else if ("Student".equals(usertype)) {
                startActivity(new Intent(MainActivity.this, Student.class)
                        .putExtra("username",username)
                        .putExtra("usertype",usertype));
            }
            finish(); // Close MainActivity
        }
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;

           /*
        1.R.menu.menu_main:refers to the xml resource file defining the menu items(folder:res/menu)
        2. getMenuInflater().inflate(R.menu.menu_main, menu):converts menu in xml to actual menu
        that can be displayed in user interface
        3.return true;:indicates you handled creation of menu and it should be displayed
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //toast is simple popup message
            Toast.makeText(this, "you selected the settings from menu", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
        /*
        if you choose not to handle a particular menu item in your onOptionsItemSelected
        method and want default behavior to be executed,you can use
         return super.onOptionsItemSelected(item);
         */
    }


}