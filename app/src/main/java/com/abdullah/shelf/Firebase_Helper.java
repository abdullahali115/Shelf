package com.abdullah.shelf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Firebase_Helper {
    private final FirebaseAuth auth;
    private final DatabaseReference ref;
    public Firebase_Helper(){
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance("https://shelf-d4ce9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
    }
    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void registerUser(String email, String username, String password, String dob, FirebaseCallback call)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(test ->{
            if(!test.isSuccessful())
            {
                call.onFailure(test.getException().getMessage());
                return;
            }
            if(auth.getCurrentUser() == null)
            {
                call.onFailure("User Creation failed");
                return;
            }
            String uid = auth.getCurrentUser().getUid();

            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("username", username);
            map.put("dob", dob);

            ref.child(uid).setValue(map).addOnCompleteListener(test1 -> {
                if(test1.isSuccessful())
                {
                    call.onSuccess();
                }
                else
                {
                    call.onFailure("Database error");
                }
            });
        });
    }
    public void loginUser(String email, String password, FirebaseCallback call)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(!task.isSuccessful())
            {
                call.onFailure(task.getException() != null? task.getException().getMessage() : "Login Failed");
                return;
            }

            if(auth.getCurrentUser() != null)
            {
                call.onSuccess();
            }
            else
            {
                call.onFailure("Invalid Credentials! Login Failed");
            }

        });
    }

}
