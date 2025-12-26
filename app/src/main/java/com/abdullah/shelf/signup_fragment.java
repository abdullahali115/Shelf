package com.abdullah.shelf;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;


public class signup_fragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public signup_fragment() {
        // Required empty public constructor
    }

    public static signup_fragment newInstance(String param1, String param2) {
        signup_fragment fragment = new signup_fragment();
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
        return inflater.inflate(R.layout.fragment_signup_fragment, container, false);
    }

    TextInputEditText email, password, username;
    MaterialTextView dobText;
    ImageView dobBtn;
    FirebaseAuth mAuth;

    Firebase_Helper firebase;
    MaterialButton signupBtn;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.emailText);
        password = view.findViewById(R.id.passwordText);
        username = view.findViewById(R.id.usernameText);
        dobText = view.findViewById(R.id.dobText);
        dobBtn = view.findViewById(R.id.dob_ic);
        mAuth = FirebaseAuth.getInstance();
        signupBtn = view.findViewById(R.id.signupBtn);
        firebase = new Firebase_Helper();

        dobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month+1) + "/" + year;
                        dobText.setText(date);
                    }
                }, y, m, d);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email != null && password != null && username != null && dobText != null)
                {
                    String e = email.getText().toString().trim();
                    String p = password.getText().toString().trim();
                    String u = username.getText().toString().trim();
                    String d = dobText.getText().toString();
                    if(e.isEmpty() || p.isEmpty() || u.isEmpty() || d.isEmpty())
                        Toast.makeText(requireContext(), "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
                    else
                    {
                        firebase.registerUser(e, u, p, d, new Firebase_Helper.FirebaseCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(requireContext(), "Error! " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}