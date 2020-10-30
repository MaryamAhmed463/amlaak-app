package com.example.amlaakapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private MyProgressDialog myProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Context mContext = LoginActivity.this;
        myProgressDialog = new MyProgressDialog(mContext);

        etEmail = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);

        getSupportActionBar().hide();
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent login_intent = new Intent(LoginActivity.this,AdminMainActivity.class);
//                startActivity(login_intent);

              logIn();
            }
        });

    }

    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void logIn() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final String Email = etEmail.getText().toString().trim();
        String Password = etPassword.getText().toString();

        if (Email.isEmpty() || Password.isEmpty()) {
            Toast.makeText(LoginActivity.this,"Enter user name and Password",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValid(Email)) {
            Toast.makeText(LoginActivity.this,"Your email is invalid",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            myProgressDialog.showDialog();
            myProgressDialog.cancelAble();

            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Sign in success, update UI with the signed-in user's information
                            if (task.isSuccessful()) {
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference userDBRef = database.getReference("Users"+ "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    userDBRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                User user = dataSnapshot.getValue(User.class);
                                                String userType = user.getsUserRole();
                                                moveToUserSpecificActivity(userType);

                                           // }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(LoginActivity.this," Failed to read value",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(LoginActivity.this,"Please you have to verify your email first",
                                            Toast.LENGTH_SHORT).show();
                                    myProgressDialog.dismissDialog();
                                }

                            }
                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Incorrect user name / password",
                                        Toast.LENGTH_SHORT).show();
                                myProgressDialog.dismissDialog();

                            }

                        }
                    });

        }

    }

    private void moveToUserSpecificActivity(String userType) {
        Intent destActivity = null;
        if (userType.equals("ADMIN")) {
                destActivity = new Intent(LoginActivity.this, AdminMainActivity.class);
        }
        else if (userType.equals("DRIVER")) {
                destActivity = new Intent(LoginActivity.this, DriverMainActivity.class);
        }
        else if (userType.equals("MANAGER")) {
            destActivity= new Intent(LoginActivity.this, ManagerMainActivity.class);
        }

        destActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(destActivity);
        myProgressDialog.dismissDialog();
        finish();
    }

}