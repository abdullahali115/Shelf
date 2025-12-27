package com.abdullah.shelf;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

// 2. CREDENTIAL MANAGER IMPORTS
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

// 3. GOOGLE ID IMPORTS (This causes the most errors if missing)
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

// 4. FIREBASE IMPORTS
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 5. JAVA UTILS
import java.util.HashMap;
import java.util.concurrent.Executors;
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

    //Sign up or Login With Google Section
    public void signInWithGoogle(Context context, FirebaseCallback call) {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("936953400456-bifj9s6mc69hdfmovrkur3mtphf229ns.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CredentialManager credentialManager = CredentialManager.create(context);

        credentialManager.getCredentialAsync(
                context,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleResult(result.getCredential(), call);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        call.onFailure(e.getMessage());
                    }
                }
        );
    }
    private void handleGoogleResult(Credential credential, FirebaseCallback call) {
        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            if (customCredential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();

                    firebaseAuthWithGoogle(idToken, call);
                } catch (Exception e) {
                    call.onFailure("Invalid Google Data: " + e.getMessage());
                }
            } else {
                call.onFailure("Unexpected credential type: " + customCredential.getType());
            }
        } else {
            call.onFailure("Not a valid CustomCredential");
        }
    }
    private void firebaseAuthWithGoogle(String idToken, FirebaseCallback call) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                if (isNewUser) {
                    String uid = auth.getCurrentUser().getUid();
                    String email = auth.getCurrentUser().getEmail();
                    String name = auth.getCurrentUser().getDisplayName();

                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("username", name != null ? name : "No Name");
                    map.put("dob", "");

                    ref.child(uid).setValue(map).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            call.onSuccess();
                        } else {
                            call.onFailure("Account created, but database failed.");
                        }
                    });
                } else {
                    call.onSuccess();
                }
            } else {
                call.onFailure(task.getException() != null ? task.getException().getMessage() : "Google Firebase Auth Failed");
            }
        });
    }


}
