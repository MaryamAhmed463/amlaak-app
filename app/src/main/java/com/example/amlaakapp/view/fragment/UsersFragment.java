package com.example.amlaakapp.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.User;
import com.example.amlaakapp.model.VehicleReference;
import com.example.amlaakapp.model.Vehicles;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.example.amlaakapp.view.adapter.DisplayUsersAdapter;
import com.example.amlaakapp.view.adapter.VehicleNameCodeAdapter;
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
public class UsersFragment extends Fragment implements DisplayUsersAdapter.UserSelectionAdapter, VehicleNameCodeAdapter.VehiclesSelectionAdapter {
    TextView txt_vehicles;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<User> userArrayList = new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    Button btn_add, btn_save;
    private ArrayList<Vehicles> vehicleSelbefore = new ArrayList<>();
    private ArrayList<Vehicles> allVehicles = new ArrayList<>();
    private ArrayList<String> preSelvehicleList = new ArrayList<>();

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<String> newvhicleId = new ArrayList<>();

    private MyProgressDialog myProgressDialog;
    private Context mContext = null;
    private RecyclerView rvVehicle;
    private boolean isListIsUpdated = false;
    private boolean providersListUpdate = true;

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

            if (preSelvehicleList == null) {
                preSelvehicleList = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Users");

        final Context mContext = getContext();
        providersListUpdate = true;

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
                    if (user.getsUserRole().equals("DRIVER")) {
                        Collections.reverse(userArrayList);
                        userArrayList.add(user);
                        Collections.reverse(userArrayList);
                    }
                }
                adapter = new DisplayUsersAdapter(getContext(), userArrayList, UsersFragment.this);
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

    private void logout() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    //Log.d(TAG,"on auth state changed",firebaseUser.getUid());
                } else {
                    Toast.makeText(getContext(), "You have logout", Toast.LENGTH_LONG).show();
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
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    void readVehicleReference(final User selecteUser){
        final FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
        DatabaseReference UserRef2 = databaseUser.getReference("Users").child(selecteUser.getsUserId()).child("sUserVehicle");
        Log.d("fblog", selecteUser.getsUserId());

        UserRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = "";
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    VehicleReference vr = d.getValue(VehicleReference.class);
                    s += vr.getVcode() + " - " + vr.getVname() + "\n";

                    vlist.add(vr.getVid());
                }

                showDialog(selecteUser, s, vlist);
                // dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("fblog", databaseError.toString());
            }

        });
    }

    @Override
    public void onUserSelected(final User selecteUser) {

        readVehicleReference(selecteUser);

    }

    private void showDialog(final User selecteUser, final String vehiclesList, final List vlist) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_user_detail);
        final TextView txt_UserCode = dialog.findViewById(R.id.txt_user_code);
        final TextView txt_UserName = dialog.findViewById(R.id.txt_user_name);
        final TextView txt_phone = dialog.findViewById(R.id.txt_user_phone);
        final TextView txt_email = dialog.findViewById(R.id.txt_email);
        txt_vehicles = dialog.findViewById(R.id.txt_vehicle_number);

        txt_UserCode.setText(selecteUser.getsUserCode());
        txt_UserName.setText(selecteUser.getsFName() + " " + selecteUser.getsSName() + " " + selecteUser.getsLName());
        txt_phone.setText(selecteUser.getsPhone());
        txt_email.setText(selecteUser.getsEmail());
        txt_vehicles .setText(vehiclesList);

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
                rvVehicle = dialogU.findViewById(R.id.rv_vehicle);
                btn_save = dialogU.findViewById(R.id.btn_save);

                et_user_code.setEnabled(false);
                et_user_email.setEnabled(false);

                et_user_code.setText(selecteUser.getsUserCode());
                et_user_name.setText(selecteUser.getsFName() + " " + selecteUser.getsSName() + " " + selecteUser.getsLName());
                et_user_phone.setText(selecteUser.getsPhone());
                et_user_email.setText(selecteUser.getsEmail());


                displayallVehicle();
                preSelvehicleList.add(String.valueOf(vlist));
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uphone = et_user_phone.getText().toString();
                        String[] arrSplit = et_user_name.getText().toString().split(" ");

                        firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference("Users")
                                .child(selecteUser.getsUserId())
                                .child("sPhone").setValue(uphone);
                        firebaseDatabase.getReference("Users")
                                .child(selecteUser.getsUserId())
                                .child("sFName").setValue(arrSplit[0]);
                        firebaseDatabase.getReference("Users")
                                .child(selecteUser.getsUserId())
                                .child("sSName").setValue(arrSplit[1]);
                        firebaseDatabase.getReference("Users")
                                .child(selecteUser.getsUserId())
                                .child("sLName").setValue(arrSplit[2]);
                        if (isListIsUpdated) {
                            firebaseDatabase.getReference("Users").child(selecteUser.getsUserId())
                                    .child("sUserVehicle").setValue(newvhicleId);
                        } else {
                            firebaseDatabase.getReference("Users").child(selecteUser.getsUserId())
                                    .child("sUserVehicle").setValue(preSelvehicleList);
                        }
                    }
                });

                dialog.dismiss();
                dialogU.show();
            }
        });


        dialog.show();
    }

    private void displayallVehicle() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference DBRef = firebaseDatabase.getReference("Vehicles");

        DBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<Vehicles> arrayList = new ArrayList<>();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Vehicles vehicles = snap.getValue(Vehicles.class);
                        arrayList.add(vehicles);
                    }
                    Collections.reverse(arrayList);
                } else {
                    Vehicles vehicles = dataSnapshot.getValue(Vehicles.class);
                    arrayList.add(vehicles);
                }

                Collections.reverse(arrayList);

                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                rvVehicle.setLayoutManager(manager);
                VehicleNameCodeAdapter vehicleNameCodeAdapter = new VehicleNameCodeAdapter(getActivity(), arrayList, preSelvehicleList, UsersFragment.this);
                rvVehicle.setAdapter(vehicleNameCodeAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

//    private void displayVehicles() {
//        vehicleSelbefore.clear();
//        allVehicles.clear();
//        DatabaseReference databaseReference1 = firebaseDatabase.getInstance().getReference("Vehicles");
//        databaseReference1.orderByChild("vid").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    Vehicles vehicles = snap.getValue(Vehicles.class);
//                    if(preSelvehicleList.contains(vehicles.getVID())) {
//                        vehicleSelbefore.add(vehicles);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        });
//
//        DatabaseReference databaseReference = firebaseDatabase.getInstance().getReference("Vehicles");
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    Vehicles vehicles = snap.getValue(Vehicles.class);
//                        allVehicles.add(vehicles);
//
//                }
//
//                allVehicles.addAll(vehicleSelbefore);
//                Collections.reverse(allVehicles);
//
//                LinearLayoutManager manager=new LinearLayoutManager(getActivity());
//                recyclerView.setLayoutManager(manager);
//                VehicleNameCodeAdapter vehicleNameCodeAdapter = new VehicleNameCodeAdapter(getActivity(),allVehicles,preSelvehicleList, UsersFragment.this);
//                recyclerView.setAdapter(vehicleNameCodeAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        });
//    }

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

    @Override
    public void VehicleArrayList(ArrayList arrayList) {
        isListIsUpdated = true;
        newvhicleId = arrayList;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(User selecteUser);
    }
}