package com.amlaakoman.amlaakapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.view.fragment.OmanOilRHFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OmanOilReportHeaderAdapter extends RecyclerView.Adapter<OmanOilReportHeaderAdapter.ViewHolder> {
    private List<Invoice> invoiceList;
    private Context context1;
    private ArrayList<Invoice> invoiceArrayList;

    private Invoice invoice;
    private ArrayList arrayListInvid = new ArrayList<>();
    private ArrayList arrayListvcode;
    private ArrayList arrayAmount;
    private ArrayList arrayVolume;
    private ArrayList arraykmperliter;
    private InvoiceSelectionAdapter mInvoiceListner;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Invoice");


    public OmanOilReportHeaderAdapter(ArrayList<String> arrayListvcode, ArrayList<Double> arrayAmount, ArrayList<Double> arrayVolume, ArrayList<Double> arraykmperliter, Context context1, OmanOilRHFragment fragContext) {
        context1 = context1;
        //this.invoiceArrayList = invoiceArrayList;
        this.arrayListvcode = arrayListvcode;
        this.arrayAmount = arrayAmount;
        this.arrayVolume = arrayVolume;
        this.arraykmperliter = arraykmperliter;
        mInvoiceListner = fragContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_header_card_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // invoice = invoiceArrayList.get(position);


        for (int i = 0; i < arrayListvcode.size(); i++) {
            holder.txt_vcode.setText(String.valueOf(arrayListvcode.get(position)));
            holder.txt_TA.setText(String.valueOf(arrayAmount.get(position)));
            holder.txt_TQ.setText(String.valueOf(arrayVolume.get(position)));
            holder.txt_kml.setText(String.valueOf(arraykmperliter.get(position)));

        }
    }

    @Override
    public int getItemCount() {
        return arrayListvcode.size();
    }

    public interface InvoiceSelectionAdapter {
        void InvoiceArrayList(ArrayList arrayList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_vcode, txt_TA, txt_TQ, txt_kml;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_vcode = itemView.findViewById(R.id.txt_vcode);
            txt_TA = itemView.findViewById(R.id.txt_TAmountValue);
            txt_TQ = itemView.findViewById(R.id.txt_tQunatityValue);
            txt_kml = itemView.findViewById(R.id.txt_kmLValue);
        }
    }
}
