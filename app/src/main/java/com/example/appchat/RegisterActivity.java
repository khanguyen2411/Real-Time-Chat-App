package com.example.appchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appchat.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.content.ContentValues.TAG;


public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText edtUsername, edtEmailAddress, edtPassword;
    Toolbar sToolbar;
    private FirebaseAuth mAuth;
    private     DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btn_register);
        edtUsername = findViewById(R.id.edt_username);
        edtEmailAddress = findViewById(R.id.edt_emailAddress);
        edtPassword = findViewById(R.id.edt_password);


        sToolbar = findViewById(R.id.register_toolbar);

        setSupportActionBar(sToolbar);
        getSupportActionBar().setTitle("Register Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String email = edtEmailAddress.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    progressDialog.setTitle("Registering Account");
                    progressDialog.setMessage("Please wait while we are create your account!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    registerAccount(username, email, password);
                } else {

                    Toast.makeText(RegisterActivity.this, "Someone field is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    assert current_user != null;
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance("https://app-chat-be401-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


                    String status = "Hi there, I\\'m using App Chat";

                    User user = new User(username, status, "default", "default" );

                    mDatabase.child("Users").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        }
                    });

                } else {
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot sign up, please check the form and try again ", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}