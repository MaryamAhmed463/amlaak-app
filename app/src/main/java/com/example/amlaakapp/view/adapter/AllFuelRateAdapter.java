package com.example.amlaakapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.R;
import com.example.amlaakapp.model.FuleRate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AllFuelRateAdapter extends RecyclerView.Adapter<AllFuelRateAdapter.ViewHolder> {

    private List<FuleRate> fuleRateList;
    private Context context1;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("FuleRate");

    public AllFuelRateAdapter(Context context, List<FuleRate> fuleRateList) {
        context1 = context;
        this.fuleRateList = fuleRateList;
    }

    @NonNull
    @Override
    public AllFuelRateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rate_fuel_row, viewGroup, false);

        return new ViewHolder(view);
}

    @Override
    public void onBindViewHolder(@NonNull AllFuelRateAdapter.ViewHolder viewHolder, int i) {
        final FuleRate fuleRate = fuleRateList.get(i);

        viewHolder.tvDateFrom.setText(fuleRate.getDate_from());
        viewHolder.tvDateTo.setText(fuleRate.getDate_to());
        viewHolder.tvRate95.setText(fuleRate.getRate_m95());
        viewHolder.tvRate91.setText(fuleRate.getRate_m91());
        viewHolder.tvRatego.setText(fuleRate.getRate_go());

    }

    @Override
    public int getItemCount() {
        return fuleRateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateFrom;
        TextView tvDateTo;
        TextView tvRate95;
        TextView tvRate91;
        TextView tvRatego;
        ImageView imgArrow95;
        ImageView imgArrow91;
        ImageView imgArrowgo;

        ViewHolder(View itemView) {

            super(itemView);
            tvDateFrom = itemView.findViewById(R.id.txt_dateFrom);
            tvDateTo = itemView.findViewById(R.id.txt_dateTo);
            tvRate95 = itemView.findViewById(R.id.txt_m95_value);
            tvRate91 = itemView.findViewById(R.id.txt_m91_value);
            tvRatego = itemView.findViewById(R.id.txt_go_value);
            imgArrow95 = itemView.findViewById(R.id.img_arrow_m95);
            imgArrow91 = itemView.findViewById(R.id.img_arrow_m91);
            imgArrowgo = itemView.findViewById(R.id.img_arrow_go);



        }
    }

}
