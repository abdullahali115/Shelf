package com.abdullah.shelf;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class login_fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public login_fragment() {
        // Required empty public constructor
    }

    public static login_fragment newInstance(String param1, String param2) {
        login_fragment fragment = new login_fragment();
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
        return inflater.inflate(R.layout.fragment_login_fragment, container, false);
    }

    Firebase_Helper firebase;
    MaterialButton loginBtn;
    TextInputEditText email, password;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginBtn = view.findViewById(R.id.loginButton);
        firebase = new Firebase_Helper();
        email = view.findViewById(R.id.loginEmailText);
        password = view.findViewById(R.id.loginPasswordText);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString();
                String p = password.getText().toString();
                if(!e.isEmpty() && !p.isEmpty()) {
                    firebase.loginUser(e, p, new Firebase_Helper.FirebaseCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(requireContext(), "Error! " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(requireContext(), "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}