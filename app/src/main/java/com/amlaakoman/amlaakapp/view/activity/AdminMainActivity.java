package com.amlaakoman.amlaakapp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.User;
import com.amlaakoman.amlaakapp.model.Vehicles;
import com.amlaakoman.amlaakapp.view.fragment.AddNewUserFragment;
import com.amlaakoman.amlaakapp.view.fragment.AddNewVehicleFragment;
import com.amlaakoman.amlaakapp.view.fragment.AdminMainFragment;
import com.amlaakoman.amlaakapp.view.fragment.CarsFragment;
import com.amlaakoman.amlaakapp.view.fragment.FuelRateFragment;
import com.amlaakoman.amlaakapp.view.fragment.SeeAllFuelRateFragment;
import com.amlaakoman.amlaakapp.view.fragment.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdminMainActivity extends AppCompatActivity implements
        AdminMainFragment.OnFragmentInteractionListener,
        CarsFragment.OnFragmentInteractionListener,
        FuelRateFragment.OnFragmentInteractionListener,
        UsersFragment.OnFragmentInteractionListener,
        AddNewUserFragment.OnFragmentInteractionListener,
        SeeAllFuelRateFragment.OnFragmentInteractionListener,
        AddNewVehicleFragment.OnFragmentInteractionListener

{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        String uid = (String) getIntent().getExtras().get("uid");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (uid != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            AdminMainFragment adminMainFragment = AdminMainFragment.newInstance(null, null);
            fragmentTransaction.replace(R.id.admin_main_container, adminMainFragment);
            fragmentTransaction.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        // toolbar = findViewById(R.id.toolbara);
        //setSupportActionBar(toolbar);

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        AdminMainFragment adminMainFragment = AdminMainFragment.newInstance(null, null);
//        fragmentTransaction.replace(R.id.admin_main_container, adminMainFragment);
//        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                Intent logout_intent = new Intent(AdminMainActivity.this, LoginActivity.class);
                startActivity(logout_intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        Fragment fragment=null;

        List fragments = getSupportFragmentManager().getFragments();
        Fragment currentFragment = (Fragment) fragments.get(fragments.size() - 1);

        if (currentFragment instanceof FuelRateFragment ||
                currentFragment instanceof CarsFragment ||
                currentFragment instanceof UsersFragment) {

            final Dialog dialog = new Dialog(AdminMainActivity.this);
            dialog.setContentView(R.layout.custom_dialoge_exit);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            // set the custom dialog components - text and button
            TextView txtTitle = dialog.findViewById(R.id.txt_title);
            txtTitle.setText("Logout");
            //TextView txtMessage = dialog.findViewById(R.id.txt_message);
            //txtMessage.setText(R.string.exit_message);

            Button btnYes = dialog.findViewById(R.id.btn_yes);
            Button btnNo = dialog.findViewById(R.id.btn_no);

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AdminMainActivity.this != null) {
                        finish();
                    }
                    dialog.dismiss();
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        else if(currentFragment instanceof SeeAllFuelRateFragment){
            fragment = AdminMainFragment.newInstance(null, null);
            //getSupportActionBar().show();
        }
        else if(currentFragment instanceof AddNewUserFragment){
            fragment = AdminMainFragment.newInstance(null, null);
            //getSupportActionBar().show();
        }
        else if(currentFragment instanceof AddNewVehicleFragment){
            fragment = AdminMainFragment.newInstance(null, null);
            //getSupportActionBar().show();
        }

        else{super.onBackPressed();}

        if(fragment !=null) {
            fragmentTransaction.replace(R.id.admin_main_container, fragment);
            fragmentTransaction.commit();
        }
    }

    public void back_btn(View view) {
        onBackPressed();
    }

    @Override
    public void onFragmentInteraction(User selecteUser) {

    }

    @Override
    public void onFragmentInteraction(Vehicles selecteVehicle) {

    }

//    public void setActionBarTitle(String title) {
//        getSupportActionBar().setTitle(title);
//    }
}