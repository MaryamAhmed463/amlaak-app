package com.amlaakoman.amlaakapp.view.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.FuleRate;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.model.User;
import com.amlaakoman.amlaakapp.model.VehicleReference;
import com.amlaakoman.amlaakapp.model.Vehicles;
import com.amlaakoman.amlaakapp.view.activity.LoginActivity;
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
import java.text.DecimalFormat;
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
    DatabaseReference databaseReference, myRef1, myRef2, databaseReference1, databaseReference2;
    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> vehiclList;

    EditText et_currentDate, et_volume, et_recipt, et_amount, et_km, et_km_num;
    ImageView img_attachRecipt, img_attechkm, img_edit;
    Button btn_addInvoice;
    TextView txt_ftvalue, txt_VPM;
    String VPM;

    String item, vehicleItem;
    String stationitem;
    final Calendar calendar = Calendar.getInstance();
    String unitPrice = "";
    String uid = " ";
    String ufn = " ";
    String usn = " ";
    String uln = " ";
    Double uPrice;
    Boolean img_km = false;
    Boolean img_invoice = false;
    Double logtitude, latitude;
    FusedLocationProviderClient client;
    Location currentLocation;
    StorageReference storageReference, storageReference1;
    FirebaseStorage firebaseStorage, firebaseStorage1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Spinner sp_vehicle, sp_station, sp_paymentMethod;
    private MyProgressDialog myProgressDialog;
    TextView txt_errDate;
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!TextUtils.isEmpty(et_volume.getText().toString().trim())) {

                Double answer = Double.parseDouble(et_volume.getText().toString()) * Double.parseDouble(unitPrice);
                Log.e("RESULT", String.valueOf(answer));
                double number1 = answer;
                DecimalFormat numberFormat1 = new DecimalFormat("###.000");

                et_amount.setText(numberFormat1.format(number1));
            } else {
                et_amount.setText("");
            }
        }
    };
    private Bitmap bitmap, bitmap1;
    private Uri kmImageUrl;
    private ArrayList<Double> kmArrayList = new ArrayList();

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

    private double H_km, span, kmPerLiter;

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_currentDate.setText(sdf.format(calendar.getTime()));

        String dates = et_currentDate.getText().toString();
        String[] items1 = dates.split("/");
        final String m1 = items1[1];
        final String y1 = items1[2];

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("FuleRate");

        myRef.orderByChild("date_from").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<FuleRate> fuleRatesArrayList = new ArrayList<>();
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    FuleRate fuleRate = postdataSnapshot.getValue(FuleRate.class);

                    String fd = fuleRate.getDate_from();
                    String[] items1 = fd.split("/");
                    String m2 = items1[1];
                    String y2 = items1[2];

                    if (!m1.equals(m2) && !y1.equals(y2)) {
                        txt_errDate.setVisibility(View.VISIBLE);
                        txt_errDate.setText("Fuel rate for this month is not available");
                        et_volume.setEnabled(false);
                        sp_station.setEnabled(false);
                        img_attachRecipt.setEnabled(false);
                        img_attechkm.setEnabled(false);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef1 = FirebaseDatabase.getInstance().getReference("Vehicles");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    Vehicles vehicles = postdataSnapshot.getValue(Vehicles.class);
                    if (vehicles.getVCode().equals(vehicleItem)) {
                        txt_ftvalue.setText(vehicles.getVFuelType());
                        getUnitPrice(vehicles.getVFuelType());
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
        txt_errDate = view.findViewById(R.id.txt_errordate);
        et_km_num = view.findViewById(R.id.et_km_number);


        et_recipt.setEnabled(false);
        et_km.setEnabled(false);
        sp_paymentMethod.setEnabled(false);
        et_amount.setEnabled(false);

        final DatePickerDialog.OnDateSetListener date_from = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        et_currentDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date_from, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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
                                getUnitPrice(vehicles.getVFuelType());
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

                //////reading invoices for this vehicle//////
                myRef2 = FirebaseDatabase.getInstance().getReference("Invoice");
                myRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                            Invoice invoice = postdataSnapshot.getValue(Invoice.class);
                            if (invoice.getvCode().equals(vehicleItem)) {
                                kmArrayList.add(invoice.getVkm());
                                //Toast.makeText(getActivity(), String.valueOf(kmArrayList), Toast.LENGTH_LONG).show();
                            }
                        }

                        for (int i = 0; i < kmArrayList.size(); i++) {
                            H_km = kmArrayList.get(0);
                            if (H_km < kmArrayList.indexOf(i)) {
                                H_km = kmArrayList.get(0);
                                Toast.makeText(getActivity(), String.valueOf(H_km), Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                ////////////////////////////////////////////
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
                img_invoice = true;
                takeImageFromCamera();
            }
        });

        img_attechkm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_km = true;
                takeImageFromCamera1();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 101);
            }
        });


        et_volume.addTextChangedListener(textWatcher);

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLastLocation();

        btn_addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bitmap != null && bitmap1 != null) {
                    myProgressDialog.showDialog();
                    myProgressDialog.cancelAble();

                    final String volume = et_volume.getText().toString();
                    final String amount = et_amount.getText().toString();
                    final String km = et_km_num.getText().toString();
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
                    } else if (sp_station.getSelectedItemId() == 0) {
                        Toast.makeText(getContext(), "Please select station", Toast.LENGTH_LONG).show();
                        myProgressDialog.dismissDialog();
                        return;
                    } else {
                        firebaseStorage = FirebaseStorage.getInstance();
                        databaseReference1 = FirebaseDatabase.getInstance().getReference("Invoice");
                        databaseReference2 = FirebaseDatabase.getInstance().getReference("kmPhoto");
                        // to be different name of image
                        storageReference = firebaseStorage.getReference("Invoice" + "/" + UUID.randomUUID());
                        storageReference1 = firebaseStorage.getReference("kmPhoto" + "/" + UUID.randomUUID());

                        //step2 >> upload image
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1);
                        byte[] data = byteArrayOutputStream.toByteArray();
                        byte[] data1 = byteArrayOutputStream1.toByteArray();
                        final UploadTask uploadTask = storageReference.putBytes(data);
                        UploadTask uploadTask1 = storageReference1.putBytes(data1);

                        //step3 >> get the image url
                        uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                } else {
                                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            Log.d("TAG", uri.toString());

                                            kmImageUrl = uri;
                                            /////////////////////////////////////
                                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    } else {
                                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(final Uri uri1) {
                                                                Log.d("TAG", uri1.toString());

                                                                final Invoice invoice = new Invoice();

                                                                invoice.setInvoiceImgUrl(uri1.toString());
                                                                invoice.setKmImgUrl(kmImageUrl.toString());
                                                                invoice.setAmount(Double.valueOf(amount));
                                                                invoice.setVolume(Double.valueOf(volume));
                                                                invoice.setDate(date);
                                                                invoice.setFuleType(fuleType);
                                                                invoice.setUnitPrice(unitPrice);
                                                                invoice.setLatitude(String.valueOf(latitude));
                                                                invoice.setLongitude(String.valueOf(logtitude));
                                                                invoice.setStation(station);
                                                                invoice.setvCode(vCode);
                                                                invoice.setDFN(" ");
                                                                invoice.setDSN(" ");
                                                                invoice.setDLN(" ");
                                                                invoice.setDID(" ");
                                                                invoice.setConfirmation("Pending");
                                                                invoice.setVkm(Double.valueOf(km));
                                                                span = Double.valueOf(km) - H_km;
                                                                invoice.setKm_span(span);

                                                                kmPerLiter = span / Double.valueOf(volume);

                                                                DecimalFormat spanffFormat = new DecimalFormat("###.000");
                                                                double spanff = Double.valueOf(spanffFormat.format(kmPerLiter));
                                                                invoice.setKm_perLitre(spanff);

                                                                if (sp_paymentMethod.isEnabled()) {
                                                                    invoice.setPaymentMethode(paymentMethodUpdate);
                                                                } else {
                                                                    invoice.setPaymentMethode(paymentMethod);
                                                                }
                                                                final String key = databaseReference1.push().getKey();
                                                                invoice.setInvoiceID(key);

                                                                //////////adding driver name to invoice/////////////////////
                                                                final FirebaseDatabase databaseu = FirebaseDatabase.getInstance();
                                                                final DatabaseReference userDBRef = databaseu.getReference("Users" + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                userDBRef.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        User user = dataSnapshot.getValue(User.class);
                                                                        uid = user.getsUserId();
                                                                        ufn = user.getsFName();
                                                                        usn = user.getsSName();
                                                                        uln = user.getsLName();

                                                                        databaseu.getReference("Invoice")
                                                                                .child(key)
                                                                                .child("dfn").setValue(ufn);
                                                                        databaseu.getReference("Invoice")
                                                                                .child(key)
                                                                                .child("dsn").setValue(usn);
                                                                        databaseu.getReference("Invoice")
                                                                                .child(key)
                                                                                .child("dln").setValue(uln);
                                                                        databaseu.getReference("Invoice")
                                                                                .child(key)
                                                                                .child("did").setValue(uid);

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                                ///////////////////////////////////////////////////////////
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
                                                        Uri downloadUri1 = task.getResult();
                                                        String path = downloadUri1.getPath();

                                                        Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_LONG).show();
                                                        sp_station.setSelection(0);
                                                        sp_paymentMethod.setSelection(0);
                                                        sp_paymentMethod.setEnabled(false);
                                                        et_volume.setText("");
                                                        et_amount.setText("");
                                                        et_recipt.setText("");
                                                        et_km.setText("");
                                                        et_km_num.setText("");

                                                        myProgressDialog.dismissDialog();
                                                        bitmap = null;
                                                    }
                                                }
                                            });
                                            ////////////////////////////////////

                                        }
                                    });
                                }
                                return storageReference1.getDownloadUrl();
                            }

                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String path = downloadUri.getPath();
                                    bitmap1 = null;
                                }
                            }
                        });


                    }
                } else {
                    Toast.makeText(getActivity(), "You have to attach receipt and km", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                }

            }
        });

        return view;
    }

    private void getUnitPrice(final String fuelType) {
        ////////////checking fuel type///////////////////
        String currentDate = et_currentDate.getText().toString();
        String[] items1 = currentDate.split("/");
        String d1 = items1[0];
        final String m1 = items1[1];
        final String y1 = items1[2];
        int d = Integer.parseInt(d1);
        final int m = Integer.parseInt(m1);
        final int y = Integer.parseInt(y1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("FuleRate");

        myRef.orderByChild("date_from").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<FuleRate> fuleRatesArrayList = new ArrayList<>();
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    FuleRate fuleRate = postdataSnapshot.getValue(FuleRate.class);

                    String fd = fuleRate.getDate_from();
                    String[] items1 = fd.split("/");
                    String d2 = items1[0];
                    String m2 = items1[1];
                    String y2 = items1[2];

                    int from_d = Integer.parseInt(d2);
                    int from_m = Integer.parseInt(m2);
                    int from_y = Integer.parseInt(y2);

                    if (m1.equals(m2) && y1.equals(y2)) {
                        txt_errDate.setVisibility(View.INVISIBLE);
                        et_volume.setEnabled(true);
                        sp_station.setEnabled(true);
                        img_attachRecipt.setEnabled(true);
                        img_attechkm.setEnabled(true);

                        if (fuelType.equals("GasOil")) {
                            unitPrice = fuleRate.getRate_go();
                        } else if (fuelType.equals("M95")) {
                            unitPrice = fuleRate.getRate_m95();
                        } else if (fuelType.equals("M91")) {
                            unitPrice = fuleRate.getRate_m91();
                        }
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ////////////////////////////////////////////////
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
                    Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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

    private void takeImageFromCamera1() {
        if (checkPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 3);
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
        if (img_invoice) {
            if (requestCode == 2 && resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                // img_attachRecipt.setImageBitmap(bitmap);
                et_recipt.setText("attached done");
                et_recipt.setTextColor(this.getResources().getColor(R.color.green));

            }
        }

        if (img_km) {
            if (requestCode == 3 && resultCode == RESULT_OK) {
                Bundle bundle1 = data.getExtras();
                bitmap1 = (Bitmap) bundle1.get("data");
                // img_attachRecipt.setImageBitmap(bitmap);
                et_km.setText("km done");
                et_km.setTextColor(this.getResources().getColor(R.color.green));

            }

//            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap1);
//            FirebaseVision firebaseVision = FirebaseVision.getInstance();
//            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
//            Task<FirebaseVisionText> task1 = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
//            task1.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                @Override
//                public void onSuccess(FirebaseVisionText firebaseVisionText) {
//
//                    String s = firebaseVisionText.getText().replace(",", ".");
//                    String s2 = s.replace("l", "1");
//                    String s3 = s2.replaceAll("[^\\d.]", "");
//
//                    et_km.setText(s3);
//                }
//            });
//
//            task1.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });

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