package com.amlaakoman.amlaakapp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amlaakoman.amlaakapp.MyProgressDialog;
import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeConfirmInvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeConfirmInvoiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyProgressDialog myProgressDialog;
    private EditText et_vCode, et_volume, et_amount, et_km;
    private ImageView img_invoice, img_km;
    private Button btn_confirm;

    private Context context;

    public MakeConfirmInvoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MakeConfirmInvoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeConfirmInvoiceFragment newInstance(String param1, String param2) {
        MakeConfirmInvoiceFragment fragment = new MakeConfirmInvoiceFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_confirm_invoice, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        txtheader.setText("Make Confirm");

        final Context mContext = getContext();
        myProgressDialog = new MyProgressDialog(mContext);

        Bundle bundle = this.getArguments();
        final String invoiceID = bundle.getString("invoiceId", null);

        et_vCode = view.findViewById(R.id.et_VCode);
        et_volume = view.findViewById(R.id.et_volume);
        et_amount = view.findViewById(R.id.et_amount);
        et_km = view.findViewById(R.id.et_km);
        img_invoice = view.findViewById(R.id.img_invoice);
        img_km = view.findViewById(R.id.img_km);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        et_vCode.setEnabled(false);

        /////////////////reading specific invoice/////////////////////////
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Invoice");

        databaseReference.orderByChild("invoiceID").equalTo(invoiceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Invoice invoice = postSnapshot.getValue(Invoice.class);
                    et_vCode.setText(invoice.getvCode());
                    et_volume.setText(String.valueOf(invoice.getVolume()));
                    et_amount.setText(String.valueOf(invoice.getAmount()));
                    et_km.setText(String.valueOf(invoice.getVkm()));
                    Picasso.with(mContext).load(invoice.getInvoiceImgUrl()).into(img_invoice);
                    Picasso.with(mContext).load(invoice.getKmImgUrl()).into(img_km);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////////////////////////////////////////////////////
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("Invoice")
                        .child(invoiceID)
                        .child("volume")
                        .setValue(Double.valueOf(et_volume.getText().toString()));

                database.getReference("Invoice")
                        .child(invoiceID)
                        .child("vkm")
                        .setValue(Double.valueOf(et_km.getText().toString()));

                database.getReference("Invoice")
                        .child(invoiceID)
                        .child("amount")
                        .setValue(Double.valueOf(et_amount.getText().toString()));

                database.getReference("Invoice")
                        .child(invoiceID)
                        .child("confirmation")
                        .setValue("Confirm");
            }
        });


        return view;
    }
}