package com.example.elearning;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class useradapter extends RecyclerView.Adapter<useradapter.ViewHolder> {
    String name,username,usertype;
    int position;
    Context context;
    ArrayList<requestmodel> requestarr;
    private Retrofit rf;
    private sqlinterface sql;

    public useradapter(Context context, ArrayList<requestmodel> requestarr) {
        this.context = context;//pass context to class context variable
        this.requestarr = requestarr;

        RetrofitClient rf=new RetrofitClient();
        sqlinterface sql=rf.getClient(context.getApplicationContext());
        this.sql=sql;
    }
    View.OnClickListener undolistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Processing request", Snackbar.LENGTH_SHORT);
            snackbar.show();
            // Define a method to handle undo operation
            retryUndoOperation();
        }

        private void retryUndoOperation() {
            // Make the network call to undo the request acceptance
            Call<String> undoCall = sql.undo(name, username, usertype);
            undoCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String undoRes = response.body();
                        // If undo is successful, add the request back to its original position
                        requestarr.add(position, new requestmodel(name, username, usertype));
                        // Notify adapter about item addition
                        notifyItemInserted(position);
                        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), undoRes, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Handle failure
                    // Retry the undo operation immediately
                    retryUndoOperation();
                }
            });
        }
    };
    @NonNull
    @Override
    //where to get the single card as viewholder object
    public useradapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.requestadmin_layout, parent, false);

        /*
        inflater(int resource path,viewgroup parent ,attachtoroot);-->returns the view
        and takes the following argument
        I)integer resource path-path of the layout(reuse this layout as viewholder)
        II)the default layout of recyclerview is the viewgroup-->to which the view is attached
        III)attachtoroot is false so they can be reused else it is attached
        and you cannot detach it

        Q)why we called from(context) before the inflate()
        Ans-before we actually inflate means convert design into actual UI we will need
        to access the resources for applying the themes,styles and the behaviour of the
        UI elements
         */
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    //what will happen after we create viewholder object(OR where to get data to bind to viewholder)
    public void onBindViewHolder(@NonNull useradapter.ViewHolder holder, int position) {
        holder.tvname.setText(requestarr.get(position).getName());
        holder.tvusername.setText(requestarr.get(position).getUsername());

    }

    @Override
    //how many items are there
    public int getItemCount() {
        return requestarr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvname, tvusername;

        Button btnacc, btnrej;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*gets the layout you set for the element inside recyclerview
            (not the recyclerview layout but layout for the element inside
            recyclerview) and sends to upper class for setting as viewholder
            ex name username acceptbtn  rejectbtn
             */
            tvusername = itemView.findViewById(R.id.tvusername);
            tvname = itemView.findViewById(R.id.tvname);
            btnacc = itemView.findViewById(R.id.btnacc);
            btnrej = itemView.findViewById(R.id.btnrej);
            btnacc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Request Accepted", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    snackbar.setAction("Undo",new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undolistener.onClick(v);
                        }
                    });
                     position = getAdapterPosition();
                     name = requestarr.get(position).getName();
                     username = requestarr.get(position).getUsername();
                     usertype = requestarr.get(position).getUsertype();

                    // Show a Snackbar to indicate that the request is being processed
                    //   Snackbar.make(holder.itemView, "Processing request...", Snackbar.LENGTH_SHORT).show();

                    // Make the network call to accept the request
                    Call<String> call = sql.acceptrequest(name, username, usertype);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String res = response.body();
                                //Snackbar.make(holder.itemView, res, Snackbar.LENGTH_INDEFINITE).show();
                                // Show the response message in a Toast
                                //Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
                                // If the request is accepted, show a Snackbar with an action to undo
                                if (res.contains("Request Accepted")) {
                                    if(getAdapterPosition()>0) {
                                        requestarr.remove(getAdapterPosition());
                                        // Notify adapter about item removal
                                        notifyItemRemoved(getAdapterPosition());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            // Handle failure
                            Toast.makeText(context, "Failed to process request", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            btnrej.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     position = getAdapterPosition(); // Get the adapter position of the clicked item

                        name = requestarr.get(position).getName();
                        username = requestarr.get(position).getUsername();
                        usertype = requestarr.get(position).getUsertype();

                        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Request Rejected", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undolistener.onClick(v); // Call undolistener with correct values
                            }
                        });

                        // Make the network call to reject the request
                        Call<String> call = sql.rejectrequest(name, username, usertype);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String res = response.body();
                                    // Show a Snackbar with an action to undo
                                    if (res.contains("Request Rejected")) {
                                        requestarr.remove(position);
                                        // Notify adapter about item removal
                                        notifyItemRemoved(position);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                // Handle failure
                                Toast.makeText(context, "Failed to process request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

            });
        }
    }
}
