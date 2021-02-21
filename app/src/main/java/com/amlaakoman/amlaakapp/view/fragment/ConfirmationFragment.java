package com.amlaakoman.amlaakapp.view.fragment;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.view.activity.LoginActivity;
import com.amlaakoman.amlaakapp.view.adapter.ConfirmationAdapter;
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
 * Use the {@link ConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmationFragment extends Fragment implements ConfirmationAdapter.InvoiceSelectionAdapter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context mContext = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth.AuthStateListener authStateListener;
    private RecyclerView recyclerView;
    private ArrayList<Invoice> invoiceArrayList = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    private ImageView img_noData;

    private MyProgressDialog myProgressDialog;
    private OnFragmentInteractionListener mListener;

    public ConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmationFragment newInstance(String param1, String param2) {
        ConfirmationFragment fragment = new ConfirmationFragment();
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
        View view = inflater.inflate(R.layout.fragment_confirmation, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Confirmation");

        mContext = getContext();
        myProgressDialog = new MyProgressDialog(mContext);
        myProgressDialog.showDialog();
        myProgressDialog.cancelAble();

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

        recyclerView = view.findViewById(R.id.rv_invoiceConfirmation);
        img_noData = view.findViewById(R.id.img_noRecord);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Invoice");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {
                    Invoice invoice = postdataSnapshot.getValue(Invoice.class);
                    if (invoice.getConfirmation().equals("Pending")) {
                        Collections.reverse(invoiceArrayList);
                        invoiceArrayList.add(invoice);
                        Collections.reverse(invoiceArrayList);
                        img_noData.setVisibility(View.INVISIBLE);
                    } else {
                        img_noData.setVisibility(View.VISIBLE);
                    }
                }
                if (invoiceArrayList.size() > 0) {
                    img_noData.setVisibility(View.INVISIBLE);
                    adapter = new ConfirmationAdapter(getContext(), invoiceArrayList, ConfirmationFragment.this);
                    recyclerView.setAdapter(adapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(linearLayoutManager);

                    myProgressDialog.dismissDialog();
                } else {
                    img_noData.setVisibility(View.VISIBLE);
                    adapter = new ConfirmationAdapter(getContext(), invoiceArrayList, ConfirmationFragment.this);
                    recyclerView.setAdapter(adapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(linearLayoutManager);

                    myProgressDialog.dismissDialog();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    public void onInvoiceSelected(Invoice selecteInvoice) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        MakeConfirmInvoiceFragment makeConfirmInvoiceFragment = MakeConfirmInvoiceFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.manager_main_container, makeConfirmInvoiceFragment);
        fragmentTransaction.commit();

        Bundle bundle = new Bundle();
        bundle.putString("invoiceId", selecteInvoice.getInvoiceID());
        makeConfirmInvoiceFragment.setArguments(bundle);
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