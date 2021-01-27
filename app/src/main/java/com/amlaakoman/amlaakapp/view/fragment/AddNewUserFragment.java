package com.amlaakoman.amlaakapp.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText et_FName,et_SName,et_LName,et_Email,et_Phone,et_Pass,et_CPass,et_UserCode;
    Button btn_Add;
    MyProgressDialog myProgressDialog;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;

    public AddNewUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewUserFragment newInstance(String param1, String param2) {
        AddNewUserFragment fragment = new AddNewUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_user, container, false);

        TextView txtheader=view.findViewById(R.id.txt_header);
        txtheader.setText("Add User");

        Context mContext = getContext();
        myProgressDialog=new MyProgressDialog(mContext);

        mAuth = FirebaseAuth.getInstance();

        et_FName = view.findViewById(R.id.etFName);
        et_SName = view.findViewById(R.id.etSName);
        et_LName = view.findViewById(R.id.etLName);
        et_Email = view.findViewById(R.id.etEmail);
        et_Phone = view.findViewById(R.id.etPhone);
        et_Pass = view.findViewById(R.id.etPassword);
        et_CPass = view.findViewById(R.id.etConfirmPassword);
        et_UserCode = view.findViewById(R.id.et_user_code);

        btn_Add = view.findViewById(R.id.btn_addUser);

        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 final String s_FName = et_FName.getText().toString();
                 final String s_SName = et_SName.getText().toString();
                 final String s_LName = et_LName.getText().toString();
                 final String s_Email = et_Email.getText().toString();
                 final String s_Phone = et_Phone.getText().toString();
                String s_Pass = et_Pass.getText().toString();
                String s_CPass = et_CPass.getText().toString();
                 final String s_UserCode = et_UserCode.getText().toString();

                myProgressDialog.showDialog();
                myProgressDialog.cancelAble();

                if (s_FName.isEmpty() || s_SName.isEmpty() || s_LName.isEmpty()
                        || s_Email.isEmpty() || s_Phone.isEmpty() || s_Pass.isEmpty()
                        || s_CPass.isEmpty() || s_UserCode.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all field", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();

                } else if (s_Pass.length() < 6) {
                    Toast.makeText(getContext(), "Password length should be 6 or more digit", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();

                } else if (!s_CPass.equals(s_Pass)) {
                    Toast.makeText(getContext(), "Confirm password and password must be equal", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();

                } else if (s_Phone.length() != 8) {
                    Toast.makeText(getContext(), "Phone number should be 8 digit", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();

                } else if (!isEmailValid(s_Email)) {
                    Toast.makeText(getContext(), "Email is not valid", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();

                }
                else {
                    mAuth.createUserWithEmailAndPassword(s_Email, s_Pass).
                            addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {


                                         database = FirebaseDatabase.getInstance();
                                         myRef = database.getReference("Users");


                                        User user = new User();
                                        user.setsFName(s_FName);
                                        user.setsSName(s_SName);
                                        user.setsLName(s_LName);
                                        user.setsEmail(s_Email.trim());
                                        //user.setPassword(etPassword.getText().toString());
                                        user.setsPhone(s_Phone);
                                        user.setsUserCode(s_UserCode);
                                        user.setsUserRole("DRIVER");
                                        user.setsUserStatus("Active");
                                        //user.setsUserVehicle(null);

                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        user.setsUserId(uid);
                                        myRef.child(uid).setValue(user);

                                        myProgressDialog.dismissDialog();

                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "User Added successfully, Ask him to verify mail",
                                                            Toast.LENGTH_SHORT).show();
                                                    myProgressDialog.dismissDialog();
                                                    et_FName.setText("");
                                                    et_SName.setText("");
                                                    et_LName.setText("");
                                                    et_Email.setText("");
                                                    et_Phone.setText("");
                                                    et_Phone.setText("");
                                                    et_Pass.setText("");
                                                    et_CPass.setText("");
                                                    et_UserCode.setText("");

                                                } else {
                                                    Toast.makeText(getContext(), task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });}
//                                    } else {
//                                        // If sign in fails, display a message to the user.
//                                        Toast.makeText(getContext(), "This email is already used", Toast.LENGTH_SHORT).show();
//                                        myProgressDialog.dismissDialog();
//                                    }

                                }
                            });
                }
            }
        });

        return view;
    }

    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}