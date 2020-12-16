package com.example.amlaakapp.view.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.Invoice;
import com.example.amlaakapp.model.VehicleReference;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.example.amlaakapp.view.adapter.DriverInvoiceAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsDriverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsDriverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final Calendar calendar = Calendar.getInstance();
    Context mContext = null;
    Spinner sp_vehicle;
    EditText et_date_from, et_date_to;
    ImageView img_search;
    TextView txt_totalAmount, txt_totalVolume;
    String vehicleItem;
    Double tv = 0.0;
    Double ta = 0.0;
    DatabaseReference databaseReference, dbrefSearch, databaseReference1;
    FirebaseDatabase fdbSearch, firebaseDatabase1;
    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> vehiclList;
    DriverInvoiceAdapter driverInvoiceAdapter;
    RecyclerView rv_invoice;
    private FirebaseAuth.AuthStateListener authStateListener;
    private MyProgressDialog myProgressDialog;
    private SimpleDateFormat dateFormat;
    private ArrayList<Invoice> invoiceArrayList = new ArrayList<>();

    public ReportsDriverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsDriverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsDriverFragment newInstance(String param1, String param2) {
        ReportsDriverFragment fragment = new ReportsDriverFragment();
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

    private void updateLabel_from() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_date_from.setText(sdf.format(calendar.getTime()));
    }

    private void updateLabel_to() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_date_to.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports_driver, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Reports");

        mContext = getContext();

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
        et_date_from = view.findViewById(R.id.et_date_from);
        et_date_to = view.findViewById(R.id.et_to_date);
        img_search = view.findViewById(R.id.img_search);
        rv_invoice = view.findViewById(R.id.rv_report);
        txt_totalAmount = view.findViewById(R.id.txt_TA);
        txt_totalVolume = view.findViewById(R.id.txt_TV);


        /////display vehicle that this driver drive it in a spinner/////
        vehiclList = new ArrayList<>();
        vehiclList.add(0, "All");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehiclList);
        sp_vehicle.setAdapter(adapter);
        readingVehicleCodeForCurrentDriver();

        databaseReference1 = FirebaseDatabase.getInstance().getReference("Invoice");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Invoice invoice = post.getValue(Invoice.class);
                    if (invoice.getDID() == FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                        Collections.reverse(invoiceArrayList);
                        invoiceArrayList.add(invoice);
                        Collections.reverse(invoiceArrayList);
                    }

                }
                driverInvoiceAdapter = new DriverInvoiceAdapter(getContext(), invoiceArrayList);
                rv_invoice.setAdapter(driverInvoiceAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                rv_invoice.setLayoutManager(linearLayoutManager);
                myProgressDialog.dismissDialog();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sp_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("All")) {
                    /////display all invoice for current driver ///////
                    invoiceArrayList.clear();
                    ta = 0.0;
                    tv = 0.0;
                    txt_totalAmount.setText("00.00");
                    txt_totalVolume.setText("00.00");
                    et_date_from.setText("");
                    et_date_to.setText("");
                    databaseReference1 = FirebaseDatabase.getInstance().getReference("Invoice");
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot post : dataSnapshot.getChildren()) {
                                Invoice invoice = post.getValue(Invoice.class);
                                if (invoice.getDID() == FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                                    Collections.reverse(invoiceArrayList);
                                    invoiceArrayList.add(invoice);
                                    Collections.reverse(invoiceArrayList);
                                }

                            }
                            driverInvoiceAdapter = new DriverInvoiceAdapter(getContext(), invoiceArrayList);
                            rv_invoice.setAdapter(driverInvoiceAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            rv_invoice.setLayoutManager(linearLayoutManager);
                            myProgressDialog.dismissDialog();
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //////////////////////////////////////////

                } else {
                    vehicleItem = parent.getItemAtPosition(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////

        final DatePickerDialog.OnDateSetListener date_from = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel_from();
            }

        };
        final DatePickerDialog.OnDateSetListener date_to = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel_to();
            }

        };

        et_date_from.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date_from, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        et_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date_to, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        ////Search part////////////////////////////////////
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoiceArrayList.clear();
                ta = 0.0;
                tv = 0.0;

                dbrefSearch = FirebaseDatabase.getInstance().getReference("Invoice");
                dbrefSearch.orderByChild("date").startAt(et_date_from.getText().toString()).
                        endAt(et_date_to.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            Invoice invoice = post.getValue(Invoice.class);
                            if (invoice.getvCode().equals(vehicleItem)) {
                                Collections.reverse(invoiceArrayList);
                                invoiceArrayList.add(invoice);
                                tv += invoice.getVolume();
                                ta += invoice.getAmount();
                                Collections.reverse(invoiceArrayList);
                            }

                        }
                        txt_totalVolume.setText(tv.toString());

                        txt_totalAmount.setText(ta.toString());
                        driverInvoiceAdapter = new DriverInvoiceAdapter(getContext(), invoiceArrayList);
                        rv_invoice.setAdapter(driverInvoiceAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        rv_invoice.setLayoutManager(linearLayoutManager);
                        myProgressDialog.dismissDialog();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        ///////////////////////////////////////////////////

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

    private void readingVehicleCodeForCurrentDriver() {

        listener = databaseReference.child("sUserVehicle").addValueEventListener(new ValueEventListener() {
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

    public interface OnFragmentInteractionListener {
    }
}