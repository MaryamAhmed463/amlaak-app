package com.amlaakoman.amlaakapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.view.fragment.OmanOilRDFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OmanOilReportDetailsAdapter extends RecyclerView.Adapter<OmanOilReportDetailsAdapter.ViewHolder> {

    private List<Invoice> invoiceList;
    private Context context1;
    private ArrayList<Invoice> invoiceArrayList;

    private Invoice invoice;
    private ArrayList arrayListInvid = new ArrayList<>();
    private InvoiceSelectionAdapter mInvoiceListner;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Invoice");

    public OmanOilReportDetailsAdapter(ArrayList<Invoice> invoiceArrayList, FragmentActivity activity, OmanOilRDFragment fragContext) {
        context1 = context1;
        this.invoiceArrayList = invoiceArrayList;
        mInvoiceListner = fragContext;
    }

    @NonNull
    @Override
    public OmanOilReportDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_detail_rv, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OmanOilReportDetailsAdapter.ViewHolder holder, int i) {
        invoice = invoiceArrayList.get(i);

        holder.txt_VCode.setText(invoice.getvCode());
        holder.txt_Date.setText(invoice.getDate());
        holder.txt_FuelType.setText(invoice.getFuleType());
        holder.txt_Qty.setText(String.valueOf(invoice.getVolume()));
        holder.txt_UnitPrice.setText(invoice.getUnitPrice());
        holder.txt_amount.setText(String.valueOf(invoice.getAmount()));
        holder.txt_km.setText(String.valueOf(invoice.getVkm()));
        holder.txt_span.setText(String.valueOf(invoice.getKm_span()));
        holder.txt_kmperliter.setText(String.valueOf(invoice.getKm_perLitre()));

    }

    @Override
    public int getItemCount() {
        return invoiceArrayList.size();
    }

    public interface InvoiceSelectionAdapter {
        // TODO: Update argument type and name
        void OmanOilReportDetailsAdapter(Invoice selecteInvoice);

        void invoiceArrayList(ArrayList arrayList);

        void onInvoiceSelected(Invoice invoice);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_VCode, txt_Date, txt_FuelType, txt_Qty, txt_UnitPrice, txt_amount, txt_km, txt_span, txt_kmperliter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_VCode = itemView.findViewById(R.id.txt_vcode);
            txt_Date = itemView.findViewById(R.id.txt_date);
            txt_FuelType = itemView.findViewById(R.id.txt_fuelType);
            txt_Qty = itemView.findViewById(R.id.txt_qty_value);
            txt_UnitPrice = itemView.findViewById(R.id.txt_uPrice);
            txt_amount = itemView.findViewById(R.id.txt_aValue);
            txt_km = itemView.findViewById(R.id.txt_kmValue);
            txt_span = itemView.findViewById(R.id.txt_span_value);
            txt_kmperliter = itemView.findViewById(R.id.txt_kmperlitrevalue);

        }
    }
}
