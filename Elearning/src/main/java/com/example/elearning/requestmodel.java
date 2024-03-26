package com.example.elearning;

import android.view.View;
import android.widget.Button;

import com.google.gson.annotations.SerializedName;

public class requestmodel {


    private String name;

    private String username;
    private String usertype;
/*we dont need to add buttons they are not part of dataset.they are part of UI.
the requestmodel class represent the data associated with each item in recylcerview,
which in case include username and name .Buttons will be part of recyclerview item layout
and will be handled separately in the useradapter class
  */
public requestmodel(String name, String username,String usertype) {
    this.username = username;
    this.name = name;
    this.usertype=usertype;
}

    public String getUsertype() {
        return usertype;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}