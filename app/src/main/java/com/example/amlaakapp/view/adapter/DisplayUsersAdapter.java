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
import com.example.amlaakapp.view.fragment.UsersFragment;
import com.example.amlaakapp.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DisplayUsersAdapter extends RecyclerView.Adapter<DisplayUsersAdapter.ViewHolder> {

    private List<User> userList;
    private Context context1;
    public interface UserSelectionAdapter {
        // TODO: Update argument type and name
        void onUserSelected(User selecteUser);
    }
    private UserSelectionAdapter mUserListner;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    public DisplayUsersAdapter(Context context, List<User> userList, UsersFragment fragContext) {
        context1 = context;
        this.userList = userList;
        mUserListner = fragContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
         User user = userList.get(i);

        viewHolder.tv_userName.setText(user.getsFName()+" "+user.getsSName()+" "+user.getsLName());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                User user = userList.get(i);
                mUserListner.onUserSelected(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_userName;
        ImageView img_driver;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_userName = itemView.findViewById(R.id.txt_user_name);
            img_driver = itemView.findViewById(R.id.img_driver_ic);
            cardView = itemView.findViewById(R.id.cv_user);

        }
    }
}
