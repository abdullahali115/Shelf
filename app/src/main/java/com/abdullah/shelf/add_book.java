package com.abdullah.shelf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class add_book extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputEditText name, isbn, author, pubYear;
    MaterialButton submit;

    public add_book() {
        // Required empty public constructor
    }

    public static add_book newInstance(String param1, String param2) {
        add_book fragment = new add_book();
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
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    FirebaseAuth auth;
    Firebase_Helper firebase;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.bookNameText);
        isbn = view.findViewById(R.id.isbnText);
        author = view.findViewById(R.id.authorText);
        pubYear = view.findViewById(R.id.pubYearText);
        submit = view.findViewById(R.id.submitButton);
        auth = FirebaseAuth.getInstance();
        firebase = new Firebase_Helper();

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        editor = prefs.edit();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookid = System.currentTimeMillis() + "";
                String n = name.getText().toString();
                String a = author.getText().toString();
                String i = isbn.getText().toString();
                String y = pubYear.getText().toString();
                String uid = auth.getCurrentUser().getUid();

                if(n.isEmpty() || a.isEmpty() || i.isEmpty() || y.isEmpty() || uid.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebase.storeBookData(uid, bookid, n, a, i, y, new Firebase_Helper.FirebaseCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("check", true);
                        editor.apply();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(requireContext(), "Error!" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}