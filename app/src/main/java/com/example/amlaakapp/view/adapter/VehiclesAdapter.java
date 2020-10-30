package com.example.amlaakapp.view.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.R;
import com.example.amlaakapp.model.Vehicles;
import com.example.amlaakapp.view.fragment.CarsFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.ViewHolder> {

    private List<Vehicles> vehiclesArrayList;
    private Context context1;
    private Vehicles vehicles;

    public interface VehicleSelectionAdapter {
        // TODO: Update argument type and name
        void onVehicleSelected(Vehicles selecteVehicle);
    }
    private VehicleSelectionAdapter mVehicleListner;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Vehicles");

    public VehiclesAdapter(List<Vehicles> vehiclesArrayList, Context context , CarsFragment fragContext) {
        this.vehiclesArrayList = vehiclesArrayList;
        context1 = context;
        mVehicleListner = fragContext;
    }

    @NonNull
    @Override
    public VehiclesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vehicle_img, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiclesAdapter.ViewHolder holder, final int position) {

        Vehicles vehicles = vehiclesArrayList.get(position);
        Picasso.with(context1).load(vehicles.getVImageUrl()).into(holder.imageView);
        holder.textView.setText(vehicles.getVName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Vehicles vehicles = vehiclesArrayList.get(position);
                mVehicleListner.onVehicleSelected(vehicles);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehiclesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_vehicle);
            textView = itemView.findViewById(R.id.txt_VName);
            cardView = itemView.findViewById(R.id.cv_Vimg);
        }
    }
}
