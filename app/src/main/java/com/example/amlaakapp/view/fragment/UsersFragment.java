package com.example.amlaakapp.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.User;
import com.example.amlaakapp.model.Vehicles;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.example.amlaakapp.view.adapter.DisplayUsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment implements DisplayUsersAdapter.UserSelectionAdapter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<User> userArrayList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    private MyProgressDialog myProgressDialog;
    private Context mContext = null;

   Button btn_add;

    private FirebaseAuth.AuthStateListener authStateListener;

    private OnFragmentInteractionListener mListener;

    List<String> VehicleArrayList = new ArrayList<>();
    List vlist = new ArrayList<>();

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        TextView txtheader=view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Users");

        final Context mContext = getContext();

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


        myProgressDialog = new MyProgressDialog(mContext);
        myProgressDialog.showDialog();
        myProgressDialog.cancelAble();

        recyclerView = view.findViewById(R.id.rv_users);
        btn_add = view.findViewById(R.id.btn_add);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  userArrayList = new ArrayList<>();
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    User user = postdataSnapshot.getValue(User.class);
                    if(user.getsUserRole().equals("DRIVER")){
                        Collections.reverse(userArrayList);
                        userArrayList.add(user);
                        Collections.reverse(userArrayList);
                    }
                }
                adapter = new DisplayUsersAdapter(getContext(), userArrayList,UsersFragment.this);
                recyclerView.setAdapter(adapter);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);

                myProgressDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                AddNewUserFragment addNewUserFragment = AddNewUserFragment.newInstance(null, null);
                fragmentTransaction.replace(R.id.admin_main_container, addNewUserFragment).addToBackStack("");
                fragmentTransaction.commit();
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(authStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }


    @Override
    public void onUserSelected(final User selecteUser) {

        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_user_detail);
        final TextView txt_UserCode = dialog.findViewById(R.id.txt_user_code);
        final TextView txt_UserName = dialog.findViewById(R.id.txt_user_name);
        final TextView txt_phone = dialog.findViewById(R.id.txt_user_phone);
        final TextView txt_email = dialog.findViewById(R.id.txt_email);
        final TextView txt_vehicles = dialog.findViewById(R.id.txt_vehicle_number);

        final FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();

        DatabaseReference UserRef = databaseUser.getReference("Users" + "/" + selecteUser.getsUserId());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                txt_UserCode.setText(user.getsUserCode());
                txt_UserName.setText(user.getsFName()+" "+user.getsSName()+" "+user.getsLName());
                txt_phone.setText(user.getsPhone());
                txt_email.setText(user.getsEmail());
               // txt_vehicles.setText(user.getsUserVehicle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        final FirebaseDatabase DBV = FirebaseDatabase.getInstance();
        DatabaseReference VRef = DBV.getReference("Vehicles");
        VRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    Vehicles vehicles = postSnapShot.getValue(Vehicles.class);
                        VehicleArrayList.addAll(vehicles.getVdriver());

                        if(VehicleArrayList.contains(selecteUser.getsUserId())){
                            for(int i=0;i<VehicleArrayList.size();i++){
                                vlist.add(vehicles.getVCode());
                        }
                            txt_vehicles.setText(String.valueOf(vlist));
                }
                }


//               for(int i=0;i<VehicleArrayList.size();i++){
//                   txt_vehicles.setText(VehicleArrayList.ge+" - "+VehicleArrayList.get(i).getVName());
//               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        Button btn_update = dialog.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogU = new Dialog(mContext);
                dialogU.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialogU.setContentView(R.layout.dialog_user_update);
                final EditText et_user_code = dialogU.findViewById(R.id.et_UCode);
                final EditText et_user_name = dialogU.findViewById(R.id.et_UName);
                final EditText et_user_phone = dialogU.findViewById(R.id.et_UPhone);
                final EditText et_user_email = dialogU.findViewById(R.id.et_UEmail);

                FirebaseDatabase databaseUser2 = FirebaseDatabase.getInstance();

                DatabaseReference UserRef = databaseUser2.getReference("Users" + "/" + selecteUser.getsUserId());

                UserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        User user = dataSnapshot.getValue(User.class);
                        et_user_code.setText(user.getsUserCode());
                        et_user_name.setText(user.getsFName()+" "+user.getsSName()+" "+user.getsLName());
                        et_user_phone.setText(user.getsPhone());
                        et_user_email.setText(user.getsEmail());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
                dialogU.show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(User selecteUser) {
        if (mListener != null) {
            mListener.onFragmentInteraction(selecteUser);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(User selecteUser);
    }
}