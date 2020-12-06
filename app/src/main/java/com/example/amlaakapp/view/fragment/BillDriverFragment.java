package com.example.amlaakapp.view.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.Invoice;
import com.example.amlaakapp.model.VehicleReference;
import com.example.amlaakapp.model.Vehicles;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BillDriverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillDriverFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_CODE = 101;
    Context mContext = null;
    DatabaseReference databaseReference, myRef1, databaseReference1;
    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> vehiclList;

    EditText et_currentDate, et_volume, et_recipt, et_amount, et_km;
    ImageView img_attachRecipt, img_attechkm, img_edit;
    Button btn_addInvoice;
    TextView txt_ftvalue, txt_VPM;
    String VPM;

    String item, vehicleItem;
    String stationitem;
    Double logtitude, latitude;
    FusedLocationProviderClient client;
    Location currentLocation;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Spinner sp_vehicle, sp_station, sp_paymentMethod;
    private MyProgressDialog myProgressDialog;
    private Bitmap bitmap;

    public BillDriverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillDriverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillDriverFragment newInstance(String param1, String param2) {
        BillDriverFragment fragment = new BillDriverFragment();
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
        View view = inflater.inflate(R.layout.fragment_bill_driver, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("New invoice");

        mContext = getContext();
        //Context mContext = getContext();
        myProgressDialog = new MyProgressDialog(mContext);

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

        sp_vehicle = view.findViewById(R.id.sp_vehicleCode);
        et_currentDate = view.findViewById(R.id.et_date);
        sp_paymentMethod = view.findViewById(R.id.sp_paymentMethod);
        sp_station = view.findViewById(R.id.sp_station);
        et_amount = view.findViewById(R.id.et_amount);
        et_km = view.findViewById(R.id.et_km);
        et_recipt = view.findViewById(R.id.et_recipt);
        et_volume = view.findViewById(R.id.et_volume);
        img_attechkm = view.findViewById(R.id.img_attach_km);
        img_attachRecipt = view.findViewById(R.id.img_attach_recipt);
        btn_addInvoice = view.findViewById(R.id.btn_addInvoice);
        txt_ftvalue = view.findViewById(R.id.txt_FTValue);
        txt_VPM = view.findViewById(R.id.txt_VPM);
        img_edit = view.findViewById(R.id.img_edit);

        et_currentDate.setEnabled(false);
        et_recipt.setEnabled(false);
        et_km.setEnabled(true);

        sp_paymentMethod.setEnabled(false);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_paymentMethod.setEnabled(true);
            }
        });
        /////display vehicle that this driver drive it in a spinner/////
        vehiclList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sUserVehicle");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehiclList);
        sp_vehicle.setAdapter(adapter);
        readingVehicleCodeForCurrentDriver();
        sp_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleItem = parent.getItemAtPosition(position).toString();

                myRef1 = FirebaseDatabase.getInstance().getReference("Vehicles");
                myRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                            Vehicles vehicles = postdataSnapshot.getValue(Vehicles.class);
                            if (vehicles.getVCode().equals(vehicleItem)) {
                                txt_ftvalue.setText(vehicles.getVFuelType());
                                VPM = vehicles.getVPaymentMethod();
                                txt_VPM.setText(VPM);
                                //sp_paymentMethod.setSelection(adapter.getPosition(VPM));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        et_currentDate.setText(formattedDate);

        //setting spinner payment method.........
        List<String> paymentMethod = new ArrayList<>();
        // paymentMethod.add(0,"Select payment method");
        paymentMethod.add("E-Fill");
        paymentMethod.add("Card");
        paymentMethod.add("VRS");
        paymentMethod.add("Cash");
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, paymentMethod);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_paymentMethod.setAdapter(dataAdapter);

        sp_paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(parent.getItemAtPosition(position).equals("Select payment method")){
//                    //nothing to do
//                }
                //else{
                item = parent.getItemAtPosition(position).toString();
                txt_VPM.setText(item);
                //}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //////////////////////////
//setting spinner station.........
        List<String> station = new ArrayList<>();
        station.add(0, "Select station");
        station.add("Oman Oil");
        station.add("Shell");
        station.add("ALMaha");
        ArrayAdapter<String> stationAdapter;
        stationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, station);
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_station.setAdapter(stationAdapter);
        sp_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select station")) {
                    //nothing to do
                } else {
                    stationitem = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //////////////////////////

        img_attachRecipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImageFromCamera();
            }
        });

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLastLocation();

        btn_addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bitmap != null) {
                    myProgressDialog.showDialog();
                    myProgressDialog.cancelAble();

                    final String volume = et_volume.getText().toString();
                    final String amount = et_amount.getText().toString();
                    final String km = et_km.getText().toString();
                    final String date = et_currentDate.getText().toString();
                    final String fuleType = txt_ftvalue.getText().toString();
                    final String paymentMethodUpdate = item;
                    final String paymentMethod = txt_VPM.getText().toString();
                    final String vCode = vehicleItem;
                    final String station = stationitem;

                    if (TextUtils.isEmpty(volume) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(km) ||
                            TextUtils.isEmpty(date) || TextUtils.isEmpty(fuleType) || TextUtils.isEmpty(vCode)) {
                        Toast.makeText(getContext(), "Please fill all field", Toast.LENGTH_LONG).show();
                        myProgressDialog.dismissDialog();
                        return;
                    } else {
                        firebaseStorage = FirebaseStorage.getInstance();
                        databaseReference1 = FirebaseDatabase.getInstance().getReference("Invoice");
                        // to be different name of image
                        storageReference = firebaseStorage.getReference("Invoice" + "/" + UUID.randomUUID());

                        //step2 >> upload image
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();
                        UploadTask uploadTask = storageReference.putBytes(data);

                        //step3 >> get the image url
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                } else {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            Log.d("TAG", uri.toString());

                                            Invoice invoice = new Invoice();

                                            invoice.setInvoiceImgUrl(uri.toString());
                                            invoice.setAmount(Double.valueOf(amount));
                                            invoice.setVolume(Double.valueOf(volume));
                                            invoice.setDate(date);
                                            invoice.setFuleType(fuleType);
                                            invoice.setLatitude(String.valueOf(latitude));
                                            invoice.setLongitude(String.valueOf(logtitude));
                                            invoice.setStation(station);
                                            invoice.setvCode(vCode);
                                            invoice.setVkm(Double.valueOf(km));
                                            if (sp_paymentMethod.isEnabled()) {
                                                invoice.setPaymentMethode(paymentMethodUpdate);
                                            } else {
                                                invoice.setPaymentMethode(paymentMethod);
                                            }

                                            //////////adding driver name to invoice/////////////////////

                                            ///////////////////////////////////////////////////////////

                                            String key = databaseReference1.push().getKey();
                                            invoice.setInvoiceID(key);
                                            databaseReference1.child(key).setValue(invoice);

                                        }
                                    });
                                }
                                return storageReference.getDownloadUrl();
                            }

                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String path = downloadUri.getPath();

                                    Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_LONG).show();
                                    sp_station.setSelection(0);
                                    sp_paymentMethod.setSelection(0);
                                    sp_paymentMethod.setEnabled(false);
                                    et_volume.setText("");
                                    et_amount.setText("");
                                    et_recipt.setText("");
                                    et_km.setText("");

                                    myProgressDialog.dismissDialog();
                                    bitmap = null;
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "You have to attach invoice", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                }

            }
        });

        return view;
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getActivity().getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
                    supportMapFragment.getMapAsync(BillDriverFragment.this);
                    latitude = currentLocation.getLatitude();
                    logtitude = currentLocation.getLongitude();
                } else {
                    Toast.makeText(getContext(), "Please check your location", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }
            }
        });
    }


    private void readingVehicleCodeForCurrentDriver() {

        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    VehicleReference vr = item.getValue(VehicleReference.class);
                    vehiclList.add(vr.getVcode());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takeImageFromCamera() {
        if (checkPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 2);
        } else {
            requestPermission();
        }

    }

    ///////////////////checking permission for camera///////////////////////////
    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                // main logic
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessageOKCancel("You need to allow access permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermission();
                                        }
                                    }
                                });
                    }
                }
            }

            ///checking permission for location
            switch (requestCode) {
                case REQUEST_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        fetchLastLocation();
                    }
                    break;
            }

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            // img_attachRecipt.setImageBitmap(bitmap);
            et_recipt.setText("attached done");
            et_recipt.setTextColor(this.getResources().getColor(R.color.green));

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                //img_attachRecipt.setImageBitmap(bitmap);
                //et_recipt.setText(uri.toString());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
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
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am hera");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        googleMap.addMarker(markerOptions);
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