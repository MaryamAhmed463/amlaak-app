package com.example.amlaakapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.R;
import com.example.amlaakapp.model.Invoice;

import java.util.List;

public class DriverInvoiceAdapter extends RecyclerView.Adapter<DriverInvoiceAdapter.ViewHolder> {

    private List<Invoice> invoiceList;
    private Context context1;


    public DriverInvoiceAdapter(Context context, List<Invoice> invoiceList) {
        context1 = context;
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_report_card_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Invoice invoice = invoiceList.get(i);

        holder.txt_date.setText(invoice.getDate());
        holder.txt_station.setText(invoice.getStation());
        holder.txt_volume.setText(String.valueOf(invoice.getVolume()));
        holder.txt_amount.setText(String.valueOf(invoice.getAmount()));
        holder.txt_VCode.setText(invoice.getvCode());

    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date, txt_station, txt_volume, txt_amount, txt_VCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_station = itemView.findViewById(R.id.txt_station);
            txt_volume = itemView.findViewById(R.id.txt_volume);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_VCode = itemView.findViewById(R.id.txt_vehicle_code);
        }
    }
}
