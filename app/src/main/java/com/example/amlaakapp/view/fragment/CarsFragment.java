package com.example.amlaakapp.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amlaakapp.view.activity.LoginActivity;
import com.example.amlaakapp.R;
import com.example.amlaakapp.view.adapter.VehiclesAdapter;
import com.example.amlaakapp.model.Vehicles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarsFragment extends Fragment implements VehiclesAdapter.VehicleSelectionAdapter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btn_addVehicle;
    RecyclerView recyclerView;
    VehiclesAdapter vehiclesAdapter;
    ArrayList vehicleArrayList;
    Context mContext;
    private FirebaseAuth.AuthStateListener authStateListener;

    private OnFragmentInteractionListener mListener;
    public CarsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarsFragment newInstance(String param1, String param2) {
        CarsFragment fragment = new CarsFragment();
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
        View view = inflater.inflate(R.layout.fragment_cars, container, false);

        TextView txtheader=view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Vehicles");

        mContext = getContext();
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


        recyclerView = view.findViewById(R.id.rv_vehicles);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Vehicles");

        myRef.orderByChild("vid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             vehicleArrayList   = new ArrayList<>();
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {

                    Vehicles vehicles = postdataSnapshot.getValue(Vehicles.class);

                    Collections.reverse(vehicleArrayList);
                    vehicleArrayList.add(vehicles);
                    Collections.reverse(vehicleArrayList);
                }


                GridLayoutManager manager = new GridLayoutManager(getContext(),2);
                recyclerView.setLayoutManager(manager);
                vehiclesAdapter = new VehiclesAdapter(vehicleArrayList,getContext(),CarsFragment.this);
                recyclerView.setAdapter(vehiclesAdapter);

                //myProgressDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_addVehicle = view.findViewById(R.id.btn_AddVehicle);
        btn_addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                AddNewVehicleFragment addNewVehicleFragment = AddNewVehicleFragment.newInstance(null, null);
                fragmentTransaction.replace(R.id.admin_main_container, addNewVehicleFragment).addToBackStack("");
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
    public void onVehicleSelected(Vehicles selecteVehicle) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_vehicle_detail);
        final TextView txt_VCode = dialog.findViewById(R.id.txt_VCode);
        final TextView txt_VName = dialog.findViewById(R.id.txt_VName);
        final TextView txt_VModel = dialog.findViewById(R.id.txt_VModel);
        final TextView txt_VTankCapacity = dialog.findViewById(R.id.txt_VTankCapacity);
        final TextView txt_VFuelType = dialog.findViewById(R.id.txt_VFuelType);
        final TextView txt_VType = dialog.findViewById(R.id.txt_VType);
        final TextView txt_VOpeningKm = dialog.findViewById(R.id.txt_VOpeningKm);
        final TextView txt_VDriver = dialog.findViewById(R.id.txt_DriverName);

        final FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();

        DatabaseReference VRef = databaseUser.getReference("Vehicles" + "/" + selecteVehicle.getVID());

        VRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Vehicles vehicles = dataSnapshot.getValue(Vehicles.class);
                txt_VCode.setText("Vehicle Code: " + vehicles.getVCode());
                txt_VName.setText("Vehicle Name: " + vehicles.getVName());
                txt_VModel.setText("Vehicle Model: " + vehicles.getVModel());
                txt_VTankCapacity.setText("Tank Capacity: " + vehicles.getVTankCapacity());
                txt_VFuelType.setText("Fuel Type: " + vehicles.getVFuelType());
                txt_VType.setText("Vehicle Type: " + vehicles.getVType());
                txt_VOpeningKm.setText("Opening km: " + vehicles.getVOpeningKm());
               // txt_VDriver.setText("Vehicle Driver: \n" + vehicles.getVdriver()+"\n");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        Button btn_update = dialog.findViewById(R.id.btn_update);

        dialog.show();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Vehicles selecteVehicle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(selecteVehicle);
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
        void onFragmentInteraction(Vehicles selecteVehicle);
    }

}