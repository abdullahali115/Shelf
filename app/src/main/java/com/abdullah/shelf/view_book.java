package com.abdullah.shelf;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.experimental.BitwiseOperationsKt;


public class view_book extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static view_book newInstance(String param1, String param2) {
        view_book fragment = new view_book();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public view_book() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_view_book, container, false);
    }

    RecyclerView booksRecycler;
    BookAdapter adapter;
    Firebase_Helper firebase;

    FirebaseAuth auth;

    Boolean reloadCheck;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Gson gson;
    String booksJson;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebase = new Firebase_Helper();
        auth = FirebaseAuth.getInstance();

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        editor = prefs.edit();

        gson = new Gson();
        booksJson = "";

        booksRecycler = view.findViewById(R.id.booksRView);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        booksRecycler.setLayoutManager(manager);
        PagerSnapHelper snapper = new PagerSnapHelper();
        snapper.attachToRecyclerView(booksRecycler);

        reloadCheck = prefs.getBoolean("check", true);

        if(reloadCheck) {
            firebase.getBookData(auth.getCurrentUser().getUid().toString(), new Firebase_Helper.FirestoreCallBack() {
                @Override
                public void onCallBack(List<Book> books) {
                    booksJson = gson.toJson(books);
                    adapter = new BookAdapter(books);
                    booksRecycler.setAdapter(adapter);
                    editor.putBoolean("check", false);
                    editor.putString("books_data", booksJson);
                    editor.apply();
                    reloadCheck = false;
                }
            });
        }
        else
        {
            List<Book> myBooks;
            booksJson = prefs.getString("books_data", "");
            if(!booksJson.isEmpty()) {
                Book[] books = gson.fromJson(booksJson, Book[].class);
                myBooks = new ArrayList<>(Arrays.asList(books));
            }
            else
            {
                myBooks = new ArrayList<>();
            }
            adapter = new BookAdapter(myBooks);
            booksRecycler.setAdapter(adapter);
        }




    }
}