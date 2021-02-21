package com.amlaakoman.amlaakapp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Use the {@link AlmahaDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlmahaDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView txt_vcode, txt_date, txt_driver, txt_fuelType, txt_unitPrice, txt_station, txt_payment, txt_qty, txt_amount, txt_kmReading, txt_span, txt_kmperliter;
    ImageView img_km, img_invoice;
    TextView txt_lat, txt_long;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyProgressDialog myProgressDialog;
    private Context context;

    public AlmahaDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlmahaDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlmahaDetailsFragment newInstance(String param1, String param2) {
        AlmahaDetailsFragment fragment = new AlmahaDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_almaha_details, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        txtheader.setText("Invoice Details");

        final Context mContext = getContext();
        myProgressDialog = new MyProgressDialog(mContext);

        Bundle bundle = this.getArguments();
        final String invoiceID = bundle.getString("invoiceId", null);

        txt_vcode = view.findViewById(R.id.txt_vcode);
        txt_date = view.findViewById(R.id.txt_date);
        txt_driver = view.findViewById(R.id.txt_driverValue);
        txt_fuelType = view.findViewById(R.id.txt_fuelTypeValue);
        txt_unitPrice = view.findViewById(R.id.txt_unitPriceValue);
        txt_station = view.findViewById(R.id.txt_stationValue);
        txt_payment = view.findViewById(R.id.txt_paymentValue);
        txt_qty = view.findViewById(R.id.txt_qtyValue);
        txt_amount = view.findViewById(R.id.txt_amountValue);
        txt_kmReading = view.findViewById(R.id.txt_kmReadingValue);
        txt_span = view.findViewById(R.id.textView19);
        txt_kmperliter = view.findViewById(R.id.txt_kmLValue);
        txt_lat = view.findViewById(R.id.textView2);
        txt_long = view.findViewById(R.id.txt_longValue);

        img_km = view.findViewById(R.id.img_km);
        img_invoice = view.findViewById(R.id.img_invoice);


        /////////////////reading specific invoice/////////////////////////
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Invoice");

        databaseReference.orderByChild("invoiceID").equalTo(invoiceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Invoice invoice = postSnapshot.getValue(Invoice.class);
                    txt_vcode.setText(invoice.getvCode());
                    txt_date.setText(invoice.getDate());
                    txt_driver.setText(invoice.getDFN() + " " + invoice.getDSN() + " " + invoice.getDLN());
                    txt_fuelType.setText(invoice.getFuleType());
                    txt_unitPrice.setText(invoice.getUnitPrice());
                    txt_station.setText(invoice.getStation());
                    txt_payment.setText(invoice.getPaymentMethode());
                    txt_qty.setText(String.valueOf(invoice.getVolume()));
                    txt_amount.setText(String.valueOf(invoice.getAmount()));
                    txt_kmReading.setText(String.valueOf(invoice.getVkm()));
                    txt_span.setText(String.valueOf(invoice.getKm_span()));
                    txt_kmperliter.setText(String.valueOf(invoice.getKm_perLitre()));
                    txt_lat.setText(invoice.getLatitude());
                    txt_long.setText(invoice.getLongitude());

                    Picasso.with(mContext).load(invoice.getInvoiceImgUrl()).into(img_invoice);
                    Picasso.with(mContext).load(invoice.getKmImgUrl()).into(img_km);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////////////////////////////////////////////////////
        return view;
    }
}