package com.example.amlaakapp.view.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.amlaakapp.MyProgressDialog;
import com.example.amlaakapp.R;
import com.example.amlaakapp.model.FuleRate;
import com.example.amlaakapp.view.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FuelRateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FuelRateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final Calendar calendar = Calendar.getInstance();

    MyProgressDialog myProgressDialog;

    EditText et_date_from , et_date_to , et_M95 , et_M91 , et_GO;
    Button btn_Save , btn_all;

    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;

    private FirebaseAuth.AuthStateListener authStateListener;

    private SimpleDateFormat dateFormat;
    Context mContext;


    public FuelRateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FuelRateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FuelRateFragment newInstance(String param1, String param2) {
        FuelRateFragment fragment = new FuelRateFragment();
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
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_date_from.setText(sdf.format(calendar.getTime()));
    }
    private void updateLabel_to() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_date_to.setText(sdf.format(calendar.getTime()));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fuel_rate, container, false);

        final Context mContext = getContext();
        myProgressDialog=new MyProgressDialog(mContext);

        TextView txtheader=view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Fuel Rate");
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

        et_date_from = view.findViewById(R.id.et_date_from);
        et_date_to = view.findViewById(R.id.et_to_date);
        et_M95 = view.findViewById(R.id.et_m95);
        et_M91 = view.findViewById(R.id.et_m91);
        et_GO = view.findViewById(R.id.et_gasoil);
        btn_Save = view.findViewById(R.id.btn_save);
        btn_all = view.findViewById(R.id.btn_see_all);

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                SeeAllFuelRateFragment seeAllFuelRateFragment = SeeAllFuelRateFragment.newInstance(null, null);
                fragmentTransaction.replace(R.id.admin_main_container, seeAllFuelRateFragment).addToBackStack("");
                fragmentTransaction.commit();
            }
        });

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

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference= firebaseDatabase.getReference("FuleRate");
                firebaseStorage = FirebaseStorage.getInstance();

                String sdate_from = et_date_from.getText().toString();
                String sdate_to = et_date_to.getText().toString();
                String sfule_95 = et_M95.getText().toString();
                String sfule_91 = et_M91.getText().toString();
                String sfule_go = et_GO.getText().toString();

                myProgressDialog.showDialog();
                myProgressDialog.cancelAble();

                if (TextUtils.isEmpty(sdate_from) || TextUtils.isEmpty(sdate_to)) {
                    Toast.makeText(getContext(),"Please pick a date",Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }

                //check date from is before to date and date to after date from
                dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date fromDate = new Date();
                Date toDate = new Date();
                try {
                    fromDate = dateFormat.parse(et_date_from.getText().toString());
                    toDate = dateFormat.parse(et_date_to.getText().toString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.after(toDate)) {
                    Toast.makeText(getContext(),"Date From should be before To Date",Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }
                else if (toDate.before(fromDate)) {
                    Toast.makeText(getContext(),"Date To should be After From Date",Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }

                if (TextUtils.isEmpty(sfule_91)) {
                    Toast.makeText(getContext(),"Please enter fule rate for M91",Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }
                if (TextUtils.isEmpty(sfule_95)) {
                    Toast.makeText(getContext(),"Please enter fule rate for M95",Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }
                if (TextUtils.isEmpty(sfule_go)) {
                    Toast.makeText(getContext(),"Please enter fule rate for GasOil",Toast.LENGTH_LONG).show();
                    myProgressDialog.dismissDialog();
                    return;
                }

                FuleRate fuleRate = new FuleRate();
                fuleRate.setDate_from(sdate_from);
                fuleRate.setDate_to(sdate_to);
                fuleRate.setRate_m95(sfule_95);
                fuleRate.setRate_m91(sfule_91);
                fuleRate.setRate_go(sfule_go);

                String key = databaseReference.push().getKey();
                fuleRate.setR_id(key);
                databaseReference.child(key).setValue(fuleRate);


                myProgressDialog.dismissDialog();
                Toast.makeText(getContext(),"Rate fuel for this month saved successfully",Toast.LENGTH_LONG).show();

                et_date_from.setText("");
                et_date_to.setText("");
                et_M91.setText("");
                et_M95.setText("");
                et_GO.setText("");

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}