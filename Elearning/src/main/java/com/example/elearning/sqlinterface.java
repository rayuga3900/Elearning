package com.example.elearning;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

//import com.example.elearning.registrationdata;
public interface sqlinterface {

    //    @FormUrlEncoded  is only used for @POST not for@GET
    //field name should match the variable thats catching the value
    //sequence doesnt matter
    @FormUrlEncoded
    @POST("registeruser.php")
    Call<String> registeruser(
                                @Field("usertype") String usertype,
                                @Field("name") String name,
                                @Field("email") String email,
                                @Field("un") String username,
                                @Field("pswd") String password
                           );
    @FormUrlEncoded
    @POST("loginuser.php")
    Call<String>loginuser(
            @Field("usertype") String usertype,
            @Field("un") String username,
            @Field("pswd") String password
    );
    @FormUrlEncoded
    @POST("sendemail.php")
    Call<String> sendemail(
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("verifyuser.php")
    Call<String> verifyuser(
            @Field("username") String username,
            @Field("usertype") String usertype,
            @Field("verify") String verify
    );
    @FormUrlEncoded
    @POST("getrequest.php")
    Call<List<requestmodel>> getrequest(
            @Field("usertype") String usertype
    );
    @FormUrlEncoded
    @POST("acceptrequest.php")
    Call<String> acceptrequest(
      @Field("name") String name,
      @Field("username") String username,
      @Field("usertype") String usertype
    );
    @FormUrlEncoded
    @POST("rejectrequest.php")
    Call<String> rejectrequest(
            @Field("name") String name,
            @Field("username") String username,
            @Field("usertype") String usertype
    );
    @FormUrlEncoded
    @POST("undo.php")
    Call<String> undo(
            @Field("name") String name,
            @Field("username") String username,
            @Field("usertype") String usertype
    );
    /* @FormUrlEncoded
    @POST("registeradmin.php")
    Call<String>registeradmin(
            @Field("name") String name,
            @Field("email") String email,
            @Field("un") String username,
            @Field("pswd") String password
    );
    @FormUrlEncoded
    @POST("registerteach.php")
    Call<String>registerteach(
            @Field("name") String name,
            @Field("email") String email,
            @Field("un") String username,
            @Field("pswd") String password
                            );

    @FormUrlEncoded
    @POST("registerstud.php")
    Call<String>registerstud(
            @Field("name") String name,
            @Field("email") String email,
            @Field("un") String username,
            @Field("pswd") String password
    );*/
  @FormUrlEncoded
    @POST("loginadmin.php")
  Call<String>loginadmin(
          @Field("un") String username,
          @Field("pswd") String password
  );
    @FormUrlEncoded
    @POST("loginteach.php")
    Call<String>loginteach(
            @Field("un") String username,
            @Field("pswd") String password
    );
    @FormUrlEncoded
    @POST("loginstud.php")
    Call<String>loginstud(
            @Field("un") String username,
            @Field("pswd") String password
    );

}
