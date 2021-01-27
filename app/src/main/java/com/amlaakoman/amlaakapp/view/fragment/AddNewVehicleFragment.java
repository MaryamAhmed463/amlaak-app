package com.amlaakoman.amlaakapp.view.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.User;
import com.amlaakoman.amlaakapp.model.UserReference;
import com.amlaakoman.amlaakapp.model.VehicleReference;
import com.amlaakoman.amlaakapp.model.Vehicles;
import com.amlaakoman.amlaakapp.view.adapter.DriverNameAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewVehicleFragment extends Fragment implements DriverNameAdapter.DriverSelectionAdapter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    ImageView img_vehicle;
    Button btn_add_img,btn_add;
    EditText et_VCode,et_VName,et_VType,et_VModel,et_VTankCapacity,et_VOpeningKm;
    Spinner sp_paymentMethod;
    private RadioGroup rgFuelType;
    private RadioButton rbSelect , rbm95,rbm91,rbgo;
    String item;
    private OnFragmentInteractionListener mListener;
    private MyProgressDialog myProgressDialog;

    private ArrayList<String> driverSelected = new ArrayList();
    private ArrayList<String> vehicleID = new ArrayList();

    private  Context mContext = null;

    private Bitmap bitmap;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private DriverNameAdapter driverNameAdapter;
    private String FT = "";
    private String key;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNewVehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewVehicleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewVehicleFragment newInstance(String param1, String param2) {
        AddNewVehicleFragment fragment = new AddNewVehicleFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_new_vehicle, container, false);

        TextView txtheader=view.findViewById(R.id.txt_header);
        txtheader.setText("Add Vehicle");

        Context mContext = getContext();
        myProgressDialog=new MyProgressDialog(mContext);

        recyclerView = view.findViewById(R.id.rv_driverName);
        img_vehicle = view.findViewById(R.id.img_vehicle);
        btn_add_img = view.findViewById(R.id.btn_add_img);
        sp_paymentMethod = view.findViewById(R.id.sp_payment_method);
        rgFuelType = view.findViewById(R.id.rg_FuelType);
        btn_add = view.findViewById(R.id.btn_addV);
        et_VCode = view.findViewById(R.id.et_VCode);
        et_VName = view.findViewById(R.id.et_VName);
        et_VModel = view.findViewById(R.id.et_VModel);
        et_VType = view.findViewById(R.id.et_VType);
        et_VTankCapacity = view.findViewById(R.id.et_VTankCapacity);
        et_VOpeningKm = view.findViewById(R.id.et_opening_km);

//        int getSelected = rgFuelType.getCheckedRadioButtonId();
//        rbSelect = view.findViewById(getSelected);

        rgFuelType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_m95:
                        // do operations specific to this selection
                        FT = "M95";
                        break;
                    case R.id.rb_m91:
                        // do operations specific to this selection
                        FT = "M91";
                        break;
                    case R.id.rb_go:
                        // do operations specific to this selection
                        FT = "GasOil";
                        break;
                }

            }
        });




//setting spinner payment method.........
        List<String> paymentMethod = new ArrayList<>();
        paymentMethod.add(0,"Select payment method");
        paymentMethod.add("E-Fill");
        paymentMethod.add("Card");
        paymentMethod.add("VRS");
        paymentMethod.add("Cash");
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,paymentMethod);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_paymentMethod.setAdapter(dataAdapter);
        sp_paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Select payment method")){
                    //nothing to do
                }
                else{
                     item = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //////////////////////////
        img_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());

                String[] pictureDialogItems = {
                       "Open photo",
                        "Open camera" };
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case 0:
                                        chooseImageFromGallery();
                                        btn_add_img.setVisibility(View.INVISIBLE);
                                        break;
                                    case 1:
                                        takeImageFromCamera();
                                        btn_add_img.setVisibility(View.INVISIBLE);
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });
        btn_add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());

                String[] pictureDialogItems = {
                        "Open photo",
                        "Open camera" };
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case 0:
                                        chooseImageFromGallery();
                                        btn_add_img.setVisibility(View.INVISIBLE);
                                        break;
                                    case 1:
                                        takeImageFromCamera();
                                        btn_add_img.setVisibility(View.INVISIBLE);
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });
        displayDriver();

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("Vehicles");
        firebaseStorage = FirebaseStorage.getInstance();

        ///adding vehicles in firebase.....//
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

               if (bitmap != null) {
                    myProgressDialog.showDialog();
                    myProgressDialog.cancelAble();

                   final String sVCode = et_VCode.getText().toString();
                   final String sVName = et_VName.getText().toString();
                   final String sVModel = et_VModel.getText().toString();
                   final String sVType = et_VType.getText().toString();
                   // final String sVFuelType = rbSelect.getText().toString();
                   final String sVOppeningKm = et_VOpeningKm.getText().toString();
                   final String sVTankCapacity = et_VTankCapacity.getText().toString();
                   //item for payment method
                   if (TextUtils.isEmpty(sVCode) || TextUtils.isEmpty(sVName) || TextUtils.isEmpty(sVModel) ||
                           TextUtils.isEmpty(sVType) || TextUtils.isEmpty(FT) || TextUtils.isEmpty(sVOppeningKm)
                           || TextUtils.isEmpty(sVTankCapacity) || TextUtils.isEmpty(item) || driverSelected.isEmpty()) {
                       Toast.makeText(getContext(),"Please fill all field",Toast.LENGTH_LONG).show();
                       myProgressDialog.dismissDialog();
                       return;
                   }
                   else if(rgFuelType.getCheckedRadioButtonId() == -1){
                       Toast.makeText(getContext(),"You have to select fuel type",Toast.LENGTH_LONG).show();
                       myProgressDialog.dismissDialog();
                       return;
                   }

                   else {
                       // to be different name of image
                       storageReference = firebaseStorage.getReference("Vehicles" + "/" + UUID.randomUUID());

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

                                           Vehicles vehicles = new Vehicles();

                                           vehicles.setVImageUrl(uri.toString());
                                           vehicles.setVCode(sVCode);
                                           vehicles.setVName(sVName);
                                           vehicles.setVType(sVType);
                                           vehicles.setVFuelType(FT);
                                           vehicles.setVModel(sVModel);
                                           vehicles.setVOpeningKm(sVOppeningKm);
                                           vehicles.setVPaymentMethod(item);
                                           vehicles.setVStatus("Active");
                                           vehicles.setVTankCapacity(sVTankCapacity);
                                           // vehicles.setVdriver(driverSelected);
                                           for (int i = 0; i < driverSelected.size(); i++) {
                                               final FirebaseDatabase databaseu = FirebaseDatabase.getInstance();
                                               final DatabaseReference userDBRef = databaseu.getReference("Users").child(String.valueOf(driverSelected.get(i)));
                                               userDBRef.addValueEventListener(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                       User user = dataSnapshot.getValue(User.class);
                                                       UserReference userReference = new UserReference();
                                                       String uid = user.getsUserId();
                                                       String ufn = user.getsFName();
                                                       String usn = user.getsSName();
                                                       String uln = user.getsLName();
                                                       userReference.setUID(uid);
                                                       userReference.setUFName(ufn);
                                                       userReference.setUSName(usn);
                                                       userReference.setULName(uln);
                                                       databaseu.getReference("Vehicles")
                                                               .child(key)
                                                               .child("vdriver")
                                                               .child(user.getsUserId()).setValue(userReference);
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                   }
                                               });
                                               ///databaseu.getReference("Users").child(String.valueOf(driverSelected.get(i))).child("sUserVehicle").setValue(vehicleID);
                                           }

                                           key = databaseReference.push().getKey();
                                           vehicles.setVID(key);
                                           databaseReference.child(key).setValue(vehicles);


                                           for (int i = 0; i < driverSelected.size(); i++) {
                                               final FirebaseDatabase databaseu = FirebaseDatabase.getInstance();
                                               final DatabaseReference userDBRef = databaseu.getReference("Users").child(String.valueOf(driverSelected.get(i)));
                                               userDBRef.addValueEventListener(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                       User user = dataSnapshot.getValue(User.class);

                                                       VehicleReference vr = new VehicleReference();
                                                       vr.setVid(key);
                                                       vr.setVcode(sVCode);
                                                       vr.setVname(sVName);
                                                       databaseu.getReference("Users")
                                                               .child(user.getsUserId())
                                                               .child("sUserVehicle")
                                                               .child(key).setValue(vr);
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                   }
                                               });
                                               ///databaseu.getReference("Users").child(String.valueOf(driverSelected.get(i))).child("sUserVehicle").setValue(vehicleID);
                                           }

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



                                   img_vehicle.setImageResource(0);
                                   btn_add_img.setVisibility(View.VISIBLE);
                                   et_VCode.setText("");
                                   et_VName.setText("");
                                   et_VModel.setText("");
                                   et_VOpeningKm.setText("");
                                   et_VTankCapacity.setText("");
                                   et_VType.setText("");


                                   myProgressDialog.dismissDialog();
                                   bitmap = null;
                               }
                           }
                       });
                   }
                }

                else {
                    Toast.makeText(getActivity(),"Please select image", Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                }
            }
        });
        ////////////////////////////////////////
        return view;
    }
    private void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 1);

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
            img_vehicle.setImageBitmap(bitmap);

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                img_vehicle.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void displayDriver() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference DBRef=firebaseDatabase.getReference("Users");

        DBRef.orderByChild("sUserRole").equalTo("DRIVER").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<User> arrayList = new ArrayList<>();
                if(dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        User user = snap.getValue(User.class);
                        String role = user.getsUserRole();
                        arrayList.add(user);
                    }
                    Collections.reverse(arrayList);
                }
                else{
                    User user = dataSnapshot.getValue(User.class);
                    arrayList.add(user);
                }

                Collections.reverse(arrayList);

                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(manager);
                driverNameAdapter= new DriverNameAdapter(arrayList,getActivity(),AddNewVehicleFragment.this);
                recyclerView.setAdapter(driverNameAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void DriverNameAdapter(User selecteDriver) {

    }

    @Override
    public void userArrayList(ArrayList arrayList) {
        driverSelected = arrayList;
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

    @Override
    public void onDriverSelected(User user) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(mContext);
        mContext = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}