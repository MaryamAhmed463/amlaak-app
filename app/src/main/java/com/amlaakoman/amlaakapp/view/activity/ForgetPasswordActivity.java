package com.amlaakoman.amlaakapp.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText et_email;
    private Button btnSend;
    private ImageView img_back;

    private MyProgressDialog myProgressDialog;

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
        setContentView(R.layout.activity_forget_password);

//        TextView txtheader= findViewById(R.id.txt_header);
//        txtheader.setText("Forget password");

        Context mContext = ForgetPasswordActivity.this;
        myProgressDialog = new MyProgressDialog(mContext);

        btnSend = findViewById(R.id.btn_send);
        et_email = findViewById(R.id.et_mail);
        img_back = findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordActivity.this.finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final String Email = et_email.getText().toString().trim();
                if (Email.isEmpty()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Enter your mail", Toast.LENGTH_SHORT).show();
                } else if (!isEmailValid(Email)) {
                    Toast.makeText(ForgetPasswordActivity.this, "Your mail is invalid", Toast.LENGTH_SHORT).show();
                } else {

                    myProgressDialog.showDialog();

                    mAuth.sendPasswordResetEmail(Email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgetPasswordActivity.this, "Check your mail to reset password", Toast.LENGTH_SHORT).show();

                                        finish();
                                    } else {
                                        Toast.makeText(ForgetPasswordActivity.this, "Fail to send reset password mail", Toast.LENGTH_SHORT).show();
                                        myProgressDialog.dismissDialog();
                                    }
                                }
                            });
                }
            }
        });
    }
}