package com.amlaakoman.amlaakapp.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amlaakoman.amlaakapp.R;
import com.amlaakoman.amlaakapp.view.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagerReportsHFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagerReportsHFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context mContext = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn_omanOil, btn_shell, btn_almaha;
    private FirebaseAuth.AuthStateListener authStateListener;

    public ManagerReportsHFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagerReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagerReportsHFragment newInstance(String param1, String param2) {
        ManagerReportsHFragment fragment = new ManagerReportsHFragment();
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
        View view = inflater.inflate(R.layout.fragment_manager_reports, container, false);

        TextView txtheader = view.findViewById(R.id.txt_header);
        ImageView imgLogout = view.findViewById(R.id.img_logout);
        txtheader.setText("Header Report");

        mContext = getContext();
        logout();
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_dialoge_exit);
                Button btnYes = dialog.findViewById(R.id.btn_yes);
                Button btnNo = dialog.findViewById(R.id.btn_no);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
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
        });

        btn_omanOil = view.findViewById(R.id.btn_omanOil);
        btn_shell = view.findViewById(R.id.btn_shell);
        btn_almaha = view.findViewById(R.id.btn_almaha);

        Fragment omanOilRHFragment = new OmanOilRHFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, omanOilRHFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        btn_omanOil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_omanOil.setBackground(getResources().getDrawable(R.drawable.tab_bkg));
                btn_omanOil.setTextColor(getResources().getColor(R.color.white));
                btn_shell.setBackground(getResources().getDrawable(R.drawable.tab_bkg_center_white));
                btn_shell.setTextColor(getResources().getColor(R.color.dark_blue));
                btn_almaha.setBackground(getResources().getDrawable(R.drawable.tab_bkg_right_white));
                btn_almaha.setTextColor(getResources().getColor(R.color.dark_blue));

                Fragment omanOilRHFragment = new OmanOilRHFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, omanOilRHFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        btn_shell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_omanOil.setBackground(getResources().getDrawable(R.drawable.tab_bkg_white));
                btn_omanOil.setTextColor(getResources().getColor(R.color.dark_blue));
                btn_shell.setBackground(getResources().getDrawable(R.drawable.tab_bkg_center));
                btn_shell.setTextColor(getResources().getColor(R.color.white));
                btn_almaha.setBackground(getResources().getDrawable(R.drawable.tab_bkg_right_white));
                btn_almaha.setTextColor(getResources().getColor(R.color.dark_blue));

                Fragment shellRHFragment = new ShellRHFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, shellRHFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_almaha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_omanOil.setBackground(getResources().getDrawable(R.drawable.tab_bkg_white));
                btn_omanOil.setTextColor(getResources().getColor(R.color.dark_blue));
                btn_shell.setBackground(getResources().getDrawable(R.drawable.tab_bkg_center_white));
                btn_shell.setTextColor(getResources().getColor(R.color.dark_blue));
                btn_almaha.setBackground(getResources().getDrawable(R.drawable.tab_bkg_right));
                btn_almaha.setTextColor(getResources().getColor(R.color.white));

                Fragment almahaRHFragment = new AlmahaRHFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, almahaRHFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }

    private void logout() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    //Log.d(TAG,"on auth state changed",firebaseUser.getUid());
                } else {
                    Toast.makeText(getContext(), "You have logout", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        };
    }

    public interface OnFragmentInteractionListener {
    }
}