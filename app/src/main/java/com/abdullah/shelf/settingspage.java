package com.abdullah.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class settingspage extends AppCompatActivity {
    ImageView backBtn;
    CardView logoutCard, deleteCard;
    Firebase_Helper firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settingspage);

        backBtn = findViewById(R.id.backBtn);
        logoutCard = findViewById(R.id.logoutCard);
        deleteCard = findViewById(R.id.deleteCard);
        firebase = new Firebase_Helper();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(settingspage.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            firebase.logout();
                            Intent i = new Intent(getApplicationContext(), get_started_activity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        deleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(settingspage.this);
                View dialogView = inflater.inflate(R.layout.password_input_interface, null);

                TextInputEditText passwordInput = dialogView.findViewById(R.id.passwordInput);

                new MaterialAlertDialogBuilder(settingspage.this)
                        .setTitle("Delete Account")
                        .setMessage("Please confirm your password to delete your account.")
                        .setView(dialogView)
                        .setPositiveButton("Delete", null)
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show().getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String password = passwordInput.getText().toString();
                                if(password.isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                firebase.deleteUser(password, new Firebase_Helper.FirebaseCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getApplicationContext(),"Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(settingspage.this, get_started_activity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(getApplicationContext(), "Error! "+error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

            }
        });
    }
}