package com.amlaakoman.amlaakapp.view.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.view.fragment.ConfirmationFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ConfirmationAdapter extends RecyclerView.Adapter<ConfirmationAdapter.ViewHolder> {

    private List<Invoice> invoiceList;
    private Context context1;
    private InvoiceSelectionAdapter mUserListner;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Invoice");

    public ConfirmationAdapter(Context context, List<Invoice> invoiceList, ConfirmationFragment fragContext) {
        context1 = context;
        this.invoiceList = invoiceList;
        mUserListner = fragContext;
    }

    @NonNull
    @Override
    public ConfirmationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_invoice_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmationAdapter.ViewHolder holder, final int position) {
        Invoice invoice = invoiceList.get(position);

        holder.tv_vehicleCode.setText(invoice.getvCode());
        holder.tv_invoiceDate.setText(invoice.getDate());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Invoice invoice1 = invoiceList.get(position);
                mUserListner.onInvoiceSelected(invoice1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public interface InvoiceSelectionAdapter {
        // TODO: Update argument type and name
        void onInvoiceSelected(Invoice selecteInvoice);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_vehicleCode;
        TextView tv_invoiceDate;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_vehicleCode = itemView.findViewById(R.id.txt_vcode);
            tv_invoiceDate = itemView.findViewById(R.id.txt_date);
            cardView = itemView.findViewById(R.id.cv_confirm_invoice);

        }
    }
}
