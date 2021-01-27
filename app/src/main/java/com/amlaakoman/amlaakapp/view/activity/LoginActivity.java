package com.amlaakoman.amlaakapp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.User;
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

    private static final String PREFS_NAME = "pref";
    private EditText etEmail;
    private EditText etPassword;
    private MyProgressDialog myProgressDialog;
    private CheckBox checkBox;
    private Button btn_forfetPass;
    private SharedPreferences mPrefs;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Context mContext = LoginActivity.this;
        myProgressDialog = new MyProgressDialog(mContext);

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        etEmail = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        checkBox = findViewById(R.id.checkBox);
        btn_forfetPass = findViewById(R.id.btn_forgetPassword);

        btn_forfetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToForgetPasswordActivity = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(goToForgetPasswordActivity);
            }
        });

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

        getPrefrenceData();

    }

    private void getPrefrenceData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sp.contains("Pref_name")) {
            String u = sp.getString("Pref_name", "not found.");
            etEmail.setText(u);
        }
//        if(sp.contains("Pref_pass")){
//            String p = sp.getString("Pref_name","not found.");
//            etPassword.setText(p);
//            Toast.makeText(getApplicationContext(),p,Toast.LENGTH_SHORT).show();
//        }
        if (sp.contains("Pref_check")) {
            Boolean b = sp.getBoolean("Pref_check", false);
            checkBox.setChecked(b);
        }
    }

    public void logIn() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final String Email = etEmail.getText().toString().trim();
        String Password = etPassword.getText().toString();

        if (Email.isEmpty() || Password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter user name and Password",
                    Toast.LENGTH_SHORT).show();
        } else if (!isEmailValid(Email)) {
            Toast.makeText(LoginActivity.this, "Your email is invalid",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (checkBox.isChecked()) {
                Boolean boolIsChecked = checkBox.isChecked();
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("Pref_name", etEmail.getText().toString().trim());
                //editor.putString("Pref_pass",etPassword.getText().toString().trim());
                editor.putBoolean("Pref_check", boolIsChecked);
                editor.apply();
            } else {
                mPrefs.edit().clear().apply();
            }
            if (isNetworkConnected()) {
                myProgressDialog.showDialog();
                myProgressDialog.cancelAble();

                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Sign in success, update UI with the signed-in user's information
                                if (task.isSuccessful()) {
                                    if (mAuth.getCurrentUser().isEmailVerified()) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference userDBRef = database.getReference("Users" + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        userDBRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                User user = dataSnapshot.getValue(User.class);
                                                String userType = user.getsUserRole();
                                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                if (user.getsUserStatus().equals("Suspend")) {
                                                    Toast.makeText(LoginActivity.this, "Your account is suspended",
                                                            Toast.LENGTH_SHORT).show();
                                                    myProgressDialog.dismissDialog();
                                                } else {
                                                    moveToUserSpecificActivity(userType, uid);
                                                    etEmail.getText().clear();
                                                    etPassword.getText().clear();
                                                }

                                                // }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(LoginActivity.this, " Failed to read value",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Please you have to verify your email first",
                                                Toast.LENGTH_SHORT).show();
                                        myProgressDialog.dismissDialog();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Incorrect user name / password",
                                            Toast.LENGTH_SHORT).show();
                                    myProgressDialog.dismissDialog();

                                }

                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, "Check your network connection",
                        Toast.LENGTH_SHORT).show();
            }


        }

    }

    private void moveToUserSpecificActivity(String userType, String uid) {
        Intent destActivity = null;
        if (userType.equals("ADMIN")) {
            destActivity = new Intent(LoginActivity.this, AdminMainActivity.class);
        } else if (userType.equals("DRIVER")) {

            destActivity = new Intent(LoginActivity.this, DriverMainActivity.class);
        } else if (userType.equals("MANAGER")) {
            destActivity = new Intent(LoginActivity.this, ManagerMainActivity.class);
        }

        destActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        destActivity.putExtra("uid", uid);
        startActivity(destActivity);
        myProgressDialog.dismissDialog();
        finish();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
