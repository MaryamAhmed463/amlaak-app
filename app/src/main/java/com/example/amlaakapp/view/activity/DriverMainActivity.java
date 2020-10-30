package com.example.amlaakapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.amlaakapp.R;
import com.example.amlaakapp.view.fragment.AdminMainFragment;
import com.example.amlaakapp.view.fragment.BillDriverFragment;
import com.example.amlaakapp.view.fragment.DriverMainFragment;
import com.example.amlaakapp.view.fragment.ProfileDriverFragment;
import com.example.amlaakapp.view.fragment.ReportsDriverFragment;

public class DriverMainActivity extends AppCompatActivity implements
        DriverMainFragment.OnFragmentInteractionListener,
        BillDriverFragment.OnFragmentInteractionListener,
        ProfileDriverFragment.OnFragmentInteractionListener,
        ReportsDriverFragment.OnFragmentInteractionListener
  {

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


  }