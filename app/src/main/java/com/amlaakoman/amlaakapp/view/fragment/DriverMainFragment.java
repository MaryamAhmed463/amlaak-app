package com.amlaakoman.amlaakapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amlaakoman.amlaakapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction fragmentTransaction;

    public DriverMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverMainFragment newInstance(String param1, String param2) {
        DriverMainFragment fragment = new DriverMainFragment();
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
        View view = inflater.inflate(R.layout.fragment_driver_main, container, false);
        bottomNavigationView = view.findViewById(R.id.bottomNavDriver);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setSelectedItemId(R.id.dollar);

        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.DriverMainContainer, new BillDriverFragment()).commit();

        return view;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.bill:
                            fragment = new BillDriverFragment();
                            break;
                        case R.id.profile:
                            fragment = new ProfileDriverFragment();
                            break;
                        case R.id.reports:
                            fragment = new ReportsDriverFragment();
                            break;
                    }
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.DriverMainContainer, fragment);
                    fragmentTransaction.commit();
                    //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

                    //getSupportFragmentManager().beginTransaction().replace(R.id.AdminMainContainer,fragment).commit();
                    return true;
                }
            };

    public interface OnFragmentInteractionListener {

    }
}