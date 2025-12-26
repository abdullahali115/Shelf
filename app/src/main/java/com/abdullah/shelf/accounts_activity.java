package com.abdullah.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
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

public class accounts_activity extends AppCompatActivity {

    FrameLayout mainFrame;
    Fragment frag;
    FragmentManager manager;
    FragmentTransaction transactor;
    TabLayout tabs;
    Intent i;

    TextView topHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accounts);

        mainFrame = findViewById(R.id.mainFrame);
        manager = getSupportFragmentManager();
        transactor = manager.beginTransaction();
        tabs = findViewById(R.id.accountsTab);
        topHeader = findViewById(R.id.topHeader);

        //select tab
        i = getIntent();
        int tabNo = i.getIntExtra("key", 0);
        TabLayout.Tab tab = tabs.getTabAt(tabNo);
        if(tab != null)
        {
            tab.select();
            if(tabNo == 1) {
                topHeader.setText("Sign Up");
                frag = new signup_fragment();
            }
            else {
                topHeader.setText("Login");
                frag = new login_fragment();
            }
        }

        transactor.replace(R.id.mainFrame, frag);
        transactor.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transactor.commit();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition())
                {
                    case 0:
                    {
                        frag = new login_fragment();
                        topHeader.setText("Login");
                        break;
                    }
                    case 1:
                    {
                        frag = new signup_fragment();
                        topHeader.setText("Sign Up");
                        break;
                    }
                }
                manager = getSupportFragmentManager();
                transactor = manager.beginTransaction();
                transactor.replace(R.id.mainFrame, frag);
                transactor.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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