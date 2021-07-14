package com.example.appchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class UsersActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    FirebaseRecyclerOptions<User> mOptions;
    FirebaseRecyclerAdapter<User, UserViewHolder> mAdapter;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mapping();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Query mQuery = FirebaseDatabase.getInstance("https://app-chat-be401-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users");

        mOptions = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(mQuery, User.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(mOptions) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.tvName.setText(model.getUsername());
                holder.tvStatus.setText(model.getStatus());
                if(!model.getImage().equals("default")){
                    Picasso.with(UsersActivity.this)
                            .load(model.getImage())
                            .placeholder(R.drawable.profile)
                            .into(holder.ivAvatar);
                }
                mDialog.dismiss();

                String uid = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(UsersActivity.this, ProfileActivity.class);

                        mIntent.putExtra("userId", uid);

                        startActivity(mIntent);
                    }
                });
            }
        };



        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }


    void mapping() {
        mToolbar = findViewById(R.id.users_appBar);
        mRecyclerView = findViewById(R.id.rcvListUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        mDialog = new ProgressDialog(UsersActivity.this);
        mDialog.setTitle("Loading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView ivAvatar;
        TextView tvName, tvStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}