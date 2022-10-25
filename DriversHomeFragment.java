package com.example.myemechanic;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriversHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriversHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DriversHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriversHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriversHomeFragment newInstance(String param1, String param2) {
        DriversHomeFragment fragment = new DriversHomeFragment();
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
    ImageView imageView,imageView1,imageView2,imageView3, imageView4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_drivers_home, container, false);
       imageView=view.findViewById(R.id.imgdrivers_getmechanic);
        imageView1=view.findViewById(R.id.imgdrivers_account);
        imageView4=view.findViewById(R.id.imgdrivers_chats);
        imageView2=view.findViewById(R.id.imgdrivers_payments);
        imageView3=view.findViewById(R.id.imgdrivers_history);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getContext(),DriverProblemActivity.class);
                startActivity(intent);
                ;
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ChatlistFragment());


            }
        });




        return  view;
    }
    public void replaceFragment(Fragment fragment){

        androidx.fragment.app.FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_viewD,fragment);
        fragmentTransaction.addToBackStack(null);           //Optional
        fragmentTransaction.commit();



    }
}