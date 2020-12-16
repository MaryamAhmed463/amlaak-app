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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.R;
import com.example.amlaakapp.model.UserReference;
import com.example.amlaakapp.model.VehicleReference;
import com.example.amlaakapp.model.Vehicles;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.example.amlaakapp.view.adapter.VehiclesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    Context mContext = null;
    String s;
    String FT = "";
    String FTU = "";
    private ArrayList<Vehicles> vehicleArrayList = new ArrayList<>();
    private RadioGroup rgFuelType;

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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //vehicleArrayList   = new ArrayList<>();
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    Vehicles vehicles = postdataSnapshot.getValue(Vehicles.class);
                    vehicleArrayList.add(vehicles);
                }
                Collections.reverse(vehicleArrayList);
                GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(manager);
                vehiclesAdapter = new VehiclesAdapter(vehicleArrayList, getContext(), CarsFragment.this);
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
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }


    @Override
    public void onVehicleSelected(Vehicles selecteVehicle) {

        readUserRefrence(selecteVehicle);

    }

    void readUserRefrence(final Vehicles selecteVehicle) {
        final FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();
        DatabaseReference UserRef2 = databaseUser.getReference("Vehicles").child(selecteVehicle.getVID()).child("vdriver");
        Log.d("fblog", selecteVehicle.getVID());

        UserRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = "";
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    UserReference ur = d.getValue(UserReference.class);
                    s += ur.getUFName() + " " + ur.getUSName() + " " + ur.getULName() + "\n";
                }

                showDialog(selecteVehicle, s);
                // dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("fblog", databaseError.toString());
            }

        });
    }

    private void showDialog(final Vehicles selecteVehicle, final String driverList) {
        final Context vContext = getContext();
        final Dialog dialogv = new Dialog(vContext);
        dialogv.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogv.setContentView(R.layout.dialog_vehicle_detail);
        TextView txt_VCode = dialogv.findViewById(R.id.txt_VCode);
        TextView txt_VName = dialogv.findViewById(R.id.txt_VName);
        TextView txt_VModel = dialogv.findViewById(R.id.txt_VModel);
        TextView txt_VTankCapacity = dialogv.findViewById(R.id.txt_VTankCapacity);
        TextView txt_VFuelType = dialogv.findViewById(R.id.txt_VFuelType);
        TextView txt_VType = dialogv.findViewById(R.id.txt_VType);
        TextView txt_VOpeningKm = dialogv.findViewById(R.id.txt_VOpeningKm);
        final TextView txt_VDriver = dialogv.findViewById(R.id.txt_DriverName);
        TextView txt_VPayment = dialogv.findViewById(R.id.txt_VPayment);
        ImageView img_Vehicle = dialogv.findViewById(R.id.img_vehicle);
        Button btn_update = dialogv.findViewById(R.id.btn_update);

        ////////////////////////
        txt_VCode.setText(selecteVehicle.getVCode());
        txt_VName.setText(selecteVehicle.getVName());
        txt_VModel.setText(selecteVehicle.getVModel());
        txt_VTankCapacity.setText(selecteVehicle.getVTankCapacity());
        txt_VFuelType.setText(selecteVehicle.getVFuelType());
        txt_VType.setText(selecteVehicle.getVType());
        txt_VOpeningKm.setText(selecteVehicle.getVOpeningKm());
        txt_VPayment.setText(selecteVehicle.getVPaymentMethod());
        Picasso.with(vContext).load(selecteVehicle.getVImageUrl()).into(img_Vehicle);
        txt_VDriver.setText(driverList);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(vContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_vehicle_update);
                final EditText et_vc = dialog.findViewById(R.id.et_vcode);
                final EditText et_vn = dialog.findViewById(R.id.et_vname);
                final EditText et_vm = dialog.findViewById(R.id.et_vmodel);
                final EditText et_vtc = dialog.findViewById(R.id.et_vtankcapacity);
                final EditText et_vt = dialog.findViewById(R.id.et_vtype);
                final EditText et_vokm = dialog.findViewById(R.id.et_vopeningkm);
                ImageView img_v = dialog.findViewById(R.id.img_vehicle);
                rgFuelType = dialog.findViewById(R.id.rg_FuelType);
                Button btn_save = dialog.findViewById(R.id.btn_save);

                et_vc.setText(selecteVehicle.getVCode());
                et_vn.setText(selecteVehicle.getVName());
                et_vm.setText(selecteVehicle.getVModel());
                et_vtc.setText(selecteVehicle.getVTankCapacity());
                et_vt.setText(selecteVehicle.getVType());
                et_vokm.setText(selecteVehicle.getVOpeningKm());
                Picasso.with(vContext).load(selecteVehicle.getVImageUrl()).into(img_v);
                RadioButton m95 = dialog.findViewById(R.id.rb_m95);
                RadioButton m91 = dialog.findViewById(R.id.rb_m91);
                RadioButton go = dialog.findViewById(R.id.rb_go);

                FT = selecteVehicle.getVFuelType();
                if (FT.equals("M95")) {
                    m95.setChecked(true);
                } else if (FT.equals("M91")) {
                    m91.setChecked(true);
                } else {
                    go.setChecked(true);
                }

                rgFuelType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rb_m95:
                                // do operations specific to this selection
                                FTU = "M95";
                                break;
                            case R.id.rb_m91:
                                // do operations specific to this selection
                                FTU = "M91";
                                break;
                            case R.id.rb_go:
                                // do operations specific to this selection
                                FTU = "GasOil";
                                break;
                        }

                    }
                });

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final FirebaseDatabase databaseu = FirebaseDatabase.getInstance();
                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vcode")
                                .setValue(et_vc.getText().toString());

                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vfuelType")
                                .setValue(FTU);
                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vmodel")
                                .setValue(et_vm.getText().toString());
                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vname")
                                .setValue(et_vn.getText().toString());
                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vopeningKm")
                                .setValue(et_vokm.getText().toString());
                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vtankCapacity")
                                .setValue(et_vtc.getText().toString());
                        databaseu.getReference("Vehicles")
                                .child(selecteVehicle.getVID())
                                .child("vtype")
                                .setValue(et_vt.getText().toString());

                        ///////update vehicle detail in users/////
                        final FirebaseDatabase dbu = FirebaseDatabase.getInstance();
                        DatabaseReference ref = dbu.getReference("Users");
                        ref.child("sUserVehicle").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    VehicleReference vr = d.getValue(VehicleReference.class);
                                    if (vr.getVid().contains(selecteVehicle.getVID())) {
                                        vr.setVcode(et_vc.getText().toString());
                                        vr.setVname(et_vn.getText().toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("fblog", databaseError.toString());
                            }

                        });

                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialogv.dismiss();

            }
        });

        dialogv.show();

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
        void onFragmentInteraction(Vehicles selecteVehicle);
    }

}