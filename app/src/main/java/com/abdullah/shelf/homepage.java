package com.abdullah.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class homepage extends AppCompatActivity {

    ImageView settingsBtn;
    FrameLayout mainFrame;
    Fragment frag;
    FragmentManager manager;
    FragmentTransaction transactor;
    TextView homeHeader;
    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        settingsBtn = findViewById(R.id.gearBtn);
        mainFrame = findViewById(R.id.homeFrame);
        tabs = findViewById(R.id.homeTab);
        homeHeader = findViewById(R.id.homeHeader);
        frag = new view_book();
        manager = getSupportFragmentManager();



        transactor = manager.beginTransaction();
        transactor.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transactor.replace(R.id.homeFrame, frag);
        transactor.commit();


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(homepage.this, settingspage.class);
                startActivity(i);
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition())
                {
                    case 0:
                    {
                        frag = new view_book();
                        homeHeader.setText("My Books");
                        break;
                    }
                    case 1:
                    {
                        frag = new add_book();
                        homeHeader.setText("Add Book");
                        break;
                    }
                }
                manager = getSupportFragmentManager();
                transactor = manager.beginTransaction();
                transactor.replace(R.id.homeFrame, frag);
                transactor.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transactor.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}