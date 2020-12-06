package com.example.amlaakapp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.amlaakapp.R;
import com.example.amlaakapp.view.fragment.BillDriverFragment;
import com.example.amlaakapp.view.fragment.DriverMainFragment;
import com.example.amlaakapp.view.fragment.ProfileDriverFragment;
import com.example.amlaakapp.view.fragment.ReportsDriverFragment;

import java.util.List;

public class DriverMainActivity extends AppCompatActivity implements
        DriverMainFragment.OnFragmentInteractionListener,
        BillDriverFragment.OnFragmentInteractionListener,
        ProfileDriverFragment.OnFragmentInteractionListener,
        ReportsDriverFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DriverMainFragment driverMainFragment = DriverMainFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.driver_main_container, driverMainFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                Intent logout_intent = new Intent(DriverMainActivity.this, LoginActivity.class);
                startActivity(logout_intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        List fragments = getSupportFragmentManager().getFragments();
        Fragment currentFragment = (Fragment) fragments.get(fragments.size() - 1);

        if (currentFragment instanceof BillDriverFragment ||
                currentFragment instanceof ReportsDriverFragment ||
                currentFragment instanceof ProfileDriverFragment) {

            final Dialog dialog = new Dialog(DriverMainActivity.this);
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
                    if (DriverMainActivity.this != null) {
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
        } else {
            super.onBackPressed();
        }

        if (fragment != null) {
            fragmentTransaction.replace(R.id.driver_main_container, fragment);
            fragmentTransaction.commit();
        }
    }

    public void back_btn(View view) {
        onBackPressed();
    }

}