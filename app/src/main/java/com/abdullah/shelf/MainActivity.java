package com.abdullah.shelf;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    FrameLayout mainFrame;
    Fragment frag;
    FragmentManager manager;
    FragmentTransaction transactor;
    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mainFrame = findViewById(R.id.mainFrame);
        frag = new login_fragment();
        manager = getSupportFragmentManager();
        transactor = manager.beginTransaction();
        tabs = findViewById(R.id.accountsTab);
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
                        break;
                    }
                    case 1:
                    {
                        frag = new signup_fragment();
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