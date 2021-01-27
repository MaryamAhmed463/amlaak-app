package com.amlaakoman.amlaakapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.User;
import com.amlaakoman.amlaakapp.view.fragment.AddNewVehicleFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DriverNameAdapter extends RecyclerView.Adapter<DriverNameAdapter.ViewHolder> {

    private List<User> driverList;
    private Context context1;
    private ArrayList<User> userArrayList;

    private User user;
    private ArrayList arrayListuid = new ArrayList<>();

    public DriverNameAdapter(ArrayList<User> userArrayList, FragmentActivity activity, AddNewVehicleFragment fragContext) {
        context1 = context1;
        this.userArrayList = userArrayList;
        mUserListner = fragContext;
    }

    public interface DriverSelectionAdapter {
        // TODO: Update argument type and name
        void DriverNameAdapter(User selecteDriver);

        void userArrayList(ArrayList arrayList);
        void onDriverSelected(User user);
    }
    private  DriverSelectionAdapter mUserListner;
    
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
       // LayoutInflater layoutInflater= (LayoutInflater) context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.driver_checkbox, viewGroup, false);

        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        user = userArrayList.get(i);

        viewHolder.checkBox.setText(user.getsFName()+" "+user.getsSName()+" "+user.getsLName());

        viewHolder.checkBox.setTag(i);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Integer pos = (Integer) viewHolder.checkBox.getTag();

                user = userArrayList.get(pos);
                if(buttonView.isChecked()){
                    arrayListuid.add(user.getsUserId());
                    mUserListner.userArrayList(arrayListuid);

                }else{
                    arrayListuid.remove(user.getsUserId());
                }
            }
        });
//        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View v) {
//                User user = driverList.get(i);
//                mUserListner.onDriverSelected(user);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_driver);
        }
    }
}
