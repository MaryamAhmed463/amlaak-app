package com.amlaakoman.amlaakapp.view.activity;

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
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.model.Invoice;
import com.amlaakoman.amlaakapp.view.adapter.AlmahaReportDetailsAdapter;
import com.amlaakoman.amlaakapp.view.adapter.ShellReportDetailAdapter;
import com.amlaakoman.amlaakapp.view.fragment.AlmahaRDFragment;
import com.amlaakoman.amlaakapp.view.fragment.AlmahaRHFragment;
import com.amlaakoman.amlaakapp.view.fragment.ConfirmationFragment;
import com.amlaakoman.amlaakapp.view.fragment.MakeConfirmInvoiceFragment;
import com.amlaakoman.amlaakapp.view.fragment.ManagerMainFragment;
import com.amlaakoman.amlaakapp.view.fragment.ManagerReportsDFragment;
import com.amlaakoman.amlaakapp.view.fragment.ManagerReportsHFragment;
import com.amlaakoman.amlaakapp.view.fragment.OmanOilDetailsFragment;
import com.amlaakoman.amlaakapp.view.fragment.OmanOilRDFragment;
import com.amlaakoman.amlaakapp.view.fragment.OmanOilRHFragment;
import com.amlaakoman.amlaakapp.view.fragment.ShellRDFragment;
import com.amlaakoman.amlaakapp.view.fragment.ShellRHFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ManagerMainActivity extends AppCompatActivity implements
        ManagerReportsHFragment.OnFragmentInteractionListener,
        ConfirmationFragment.OnFragmentInteractionListener,
        ManagerReportsDFragment.OnFragmentInteractionListener,
        OmanOilRDFragment.OnFragmentInteractionListener,
        ShellRDFragment.OnFragmentInteractionListener,
        AlmahaRDFragment.OnFragmentInteractionListener,
        ShellReportDetailAdapter.OnFragmentInteractionListener,
        OmanOilDetailsFragment.OnFragmentInteractionListener,
        AlmahaReportDetailsAdapter.OnFragmentInteractionListener,
        OmanOilRHFragment.OnFragmentInteractionListener,
        ShellRHFragment.OnFragmentInteractionListener,
        AlmahaRHFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

        String uid = (String) getIntent().getExtras().get("uid");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (uid != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ManagerMainFragment managerMainFragment = ManagerMainFragment.newInstance(null, null);
            fragmentTransaction.replace(R.id.manager_main_container, managerMainFragment);
            fragmentTransaction.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
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
                Intent logout_intent = new Intent(ManagerMainActivity.this, LoginActivity.class);
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

        if (currentFragment instanceof ManagerReportsHFragment ||
                currentFragment instanceof ConfirmationFragment ||
                currentFragment instanceof OmanOilRHFragment ||
                currentFragment instanceof OmanOilRDFragment ||
                currentFragment instanceof ShellRHFragment ||
                currentFragment instanceof ShellRDFragment ||
                currentFragment instanceof AlmahaRHFragment ||
                currentFragment instanceof AlmahaRDFragment) {

            final Dialog dialog = new Dialog(ManagerMainActivity.this);
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
                    if (ManagerMainActivity.this != null) {
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
        } else if (currentFragment instanceof MakeConfirmInvoiceFragment) {
            fragment = ManagerMainFragment.newInstance(null, null);
            //getSupportActionBar().show();
        } else if (currentFragment instanceof OmanOilDetailsFragment) {
            fragment = ManagerMainFragment.newInstance(null, null);
            //getSupportActionBar().show();
        } else {
            super.onBackPressed();
        }

        if (fragment != null) {
            fragmentTransaction.replace(R.id.manager_main_container, fragment);
            fragmentTransaction.commit();
        }
    }

    public void back_btn(View view) {
        onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Invoice selecteInvoice) {

    }
}