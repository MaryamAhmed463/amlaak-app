package com.example.amlaakapp.view.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.amlaakapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction fragmentTransaction;

    public AdminMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminMainFragment newInstance(String param1, String param2) {
        AdminMainFragment fragment = new AdminMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_main, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setSelectedItemId(R.id.dollar);

        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.AdminMainContainer, new FuelRateFragment()).commit();
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
       // getSupportFragmentManager().beginTransaction().replace(R.id.AdminMainContainer,new FuelRateFragment()).commit();

        return view;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.car:
                            fragment = new CarsFragment();
                            break;
                        case R.id.dollar:
                            fragment = new FuelRateFragment();
                            break;
                        case R.id.users:
                            fragment = new UsersFragment();
                            break;
                    }
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.AdminMainContainer, fragment);
                    fragmentTransaction.commit();
                    //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

                    //getSupportFragmentManager().beginTransaction().replace(R.id.AdminMainContainer,fragment).commit();
                    return true;
                }
            };

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}