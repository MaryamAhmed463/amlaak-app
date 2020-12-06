package com.example.amlaakapp.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.User;
import com.example.amlaakapp.model.VehicleReference;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileDriverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDriverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    MyProgressDialog myProgressDialog;

    ValueEventListener listener;
    private FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference databaseReference;
    private TextView txt_DCode, txt_DName, txt_DPhone, txt_DEmail, txt_DVehicle;

    public ProfileDriverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileDriverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileDriverFragment newInstance(String param1, String param2) {
        ProfileDriverFragment fragment = new ProfileDriverFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_driver, container, false);

        final Context mContext = getContext();
        myProgressDialog=new MyProgressDialog(mContext);

        TextView txtheader=view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Profile");
        logout();
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_dialoge_exit);
                Button btnYes = dialog.findViewById(R.id.btn_yes);
                Button btnNo = dialog.findViewById(R.id.btn_no);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        txt_DCode = view.findViewById(R.id.txt_DCode);
        txt_DName = view.findViewById(R.id.txt_DName);
        txt_DPhone = view.findViewById(R.id.txt_DPhone);
        txt_DEmail = view.findViewById(R.id.txt_DEmail);
        txt_DVehicle = view.findViewById(R.id.txt_driverVehicle);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userDBRef = database.getReference("Users"+ "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                txt_DCode.setText(user.getsUserCode());
                txt_DName.setText(user.getsFName() + " " + user.getsSName() + " " + user.getsLName());
                txt_DPhone.setText(user.getsPhone());
                txt_DEmail.setText(user.getsEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sUserVehicle");
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = " ";
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    VehicleReference vr = item.getValue(VehicleReference.class);
                    s += vr.getVcode() + " - " + vr.getVname() + "\n";
                }
                txt_DVehicle.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void logout(){

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    //Log.d(TAG,"on auth state changed",firebaseUser.getUid());
                }
                else{
                    Toast.makeText(getContext(),"You have logout",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        };
    }

    public interface OnFragmentInteractionListener {
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

}