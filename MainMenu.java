package com.example.footballapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.view.View.OnClickListener;

import com.google.android.gms.common.SignInButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenu extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    NavController navController;
    SessionHandler session;

    private OnFragmentInteractionListener mListener;

    public MainMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenu newInstance(String param1, String param2) {
        MainMenu fragment = new MainMenu();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button EnglandButton;
        Button signUp;


        navController = Navigation.findNavController(view);
        EnglandButton = view.findViewById(R.id.EnglandButton);
        EnglandButton.setOnClickListener(new View.OnClickListener()
        {

                public void onClick(View v)
        {
            navController.navigate(R.id.action_mainMenu_to_chooseTeam);
        }
    });

        Button signUpButton = (Button) getActivity().findViewById(R.id.signUpButton);
        Button createAccountButton = (Button) getActivity().findViewById((R.id.createAccountButton));

        createAccountButton.setOnClickListener((OnClickListener) this);
        signUpButton.setOnClickListener((OnClickListener) this);



    }


    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signUpButton:


            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);

            break;

            case R.id.createAccountButton:

                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent2);

                break;

        }
    }






    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
