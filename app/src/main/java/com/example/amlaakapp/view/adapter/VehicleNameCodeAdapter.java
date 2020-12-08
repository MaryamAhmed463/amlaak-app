package com.example.amlaakapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amlaakapp.R;
import com.example.amlaakapp.model.Vehicles;
import com.example.amlaakapp.view.fragment.UsersFragment;

import java.util.ArrayList;

public class VehicleNameCodeAdapter extends RecyclerView.Adapter<VehicleNameCodeAdapter.ViewHolder> {

    private ArrayList<Vehicles> vehicleReferenceArrayList;
    private ArrayList<String> prevVehicleAssigned;
    private Context context;
    private VehiclesSelectionAdapter mUserListener;

    public VehicleNameCodeAdapter(Context context, ArrayList<Vehicles> vehicleReferenceArrayList, final ArrayList<String> prevVehicleAssigned, UsersFragment fragment) {
        this.vehicleReferenceArrayList = vehicleReferenceArrayList;
        this.prevVehicleAssigned = new ArrayList<>(prevVehicleAssigned);
        this.context = context;
        mUserListener = (VehiclesSelectionAdapter) fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.vehicle_checkbox, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Vehicles vehicles = vehicleReferenceArrayList.get(i);

        if (prevVehicleAssigned != null && prevVehicleAssigned.contains(vehicles.getVID())) {
            holder.checkBoxVehicle.setChecked(true);
        }

        holder.checkBoxVehicle.setText(vehicles.getVCode() + " - " + vehicles.getVName());

        holder.checkBoxVehicle.setTag(i);
        holder.checkBoxVehicle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Integer pos = (Integer) holder.checkBoxVehicle.getTag();
                String selectedvehicleFirebaseId = vehicleReferenceArrayList.get(pos).getVID();
                if (prevVehicleAssigned.contains(selectedvehicleFirebaseId)) {
                    prevVehicleAssigned.remove(selectedvehicleFirebaseId);
                } else {
                    prevVehicleAssigned.add(selectedvehicleFirebaseId);
                }
                mUserListener.VehicleArrayList(prevVehicleAssigned);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleReferenceArrayList.size();
    }

    public interface VehiclesSelectionAdapter {
        void VehicleArrayList(ArrayList arrayList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxVehicle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxVehicle = itemView.findViewById(R.id.cb_vehicle);
        }
    }
}
