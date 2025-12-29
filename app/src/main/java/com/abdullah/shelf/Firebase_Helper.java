package com.abdullah.shelf;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

// 5. JAVA UTILS
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
public class Firebase_Helper {
    private final FirebaseAuth auth;
    private final DatabaseReference ref;

    private final FirebaseFirestore db;
    public Firebase_Helper(){
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance("https://shelf-d4ce9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        db = FirebaseFirestore.getInstance();
    }
    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface  FirestoreCallBack{
        void onCallBack(List<Book> books);
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

    public void logout()
    {
        auth.signOut();
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

    // store data of books
    public void storeBookData(String uid, String name, String author, String isbn, String year, Boolean check, FirebaseCallback call) {
        if (uid == null || uid.isEmpty() || isbn == null || isbn.isEmpty()) {
            call.onFailure("UID or ISBN is empty!");
            return;
        }
        HashMap<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("author", author);
        values.put("isbn", isbn);
        values.put("year", year);

        DocumentReference ref = db.collection("books").document(uid).collection("user_books").document(isbn);
        if(check)
        {
            uploadBook(ref, values, call);
        }
        else
        {
            ref.get().addOnSuccessListener(snapShot -> {
               if(snapShot.exists())
               {
                   call.onFailure("Book already exists with the same ISBN");
               }
               else
               {
                   uploadBook(ref, values, call);
               }
            });
        }

    }

    public void uploadBook(DocumentReference ref, HashMap<String, Object> map, FirebaseCallback call)
    {
        ref.set(map).addOnSuccessListener(s -> call.onSuccess())
                .addOnFailureListener(e -> {
                    Log.d("FirestoreError", "Upload failed", e);
                    call.onFailure("Error: " + e.getMessage());
                });
    }


    // retrieve data of books
    public void getBookData(String uid, FirestoreCallBack call)
    {
        List<Book> books = new ArrayList<>();
        db.collection("books").document(uid).collection("user_books").get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot document : task.getResult())
                        {
                            String name = document.getString("name");
                            String isbn = document.getString("isbn");
                            String author = document.getString("author");
                            String year = document.getString("year");

                            Book b = new Book(name, author, "ISBN: " + isbn, "Pub. Year: " + year);

                            books.add(b);
                        }
                    }
                    call.onCallBack(books);
                }
        );
    }

    public void deleteBook(String isbn, FirebaseCallback call)
    {
        String uid = auth.getCurrentUser().getUid();
        db.collection("books").document(uid).collection("user_books").document(isbn).delete().addOnSuccessListener(a ->{
            call.onSuccess();
        }).addOnFailureListener(e -> call.onFailure(e.getMessage().toString()));
    }
    // delete account
    public void deleteUser(String password, FirebaseCallback call) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteUserBooks(user.getUid(), new FirebaseCallback() {
                    @Override
                    public void onSuccess() {
                        deleteUserDetails(user.getUid(), new FirebaseCallback() {
                            @Override
                            public void onSuccess() {
                                user.delete().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        call.onSuccess();
                                    } else {
                                        call.onFailure("Failed to delete Auth user");
                                    }
                                });
                            }
                            @Override
                            public void onFailure(String error) {
                                call.onFailure(error);
                            }
                        });
                    }
                    @Override
                    public void onFailure(String error) {
                        call.onFailure(error);
                    }
                });
            } else {
                call.onFailure("Wrong Password");
            }
        });
    }
    public void deleteUserBooks(String uid, FirebaseCallback call) {
        db.collection("books")
                .document(uid).collection("user_books").get().addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        batch.delete(document.getReference());
                    }
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            call.onSuccess();
                        } else {
                            call.onFailure("Failed to delete books");
                        }
                    });
                }).addOnFailureListener(e -> call.onFailure("Failed to get books"));
    }
    public void deleteUserDetails(String uid, FirebaseCallback call) {
        ref.child(uid).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                call.onSuccess();
            } else {
                call.onFailure("Failed to delete user details");
            }
        });
    }
}
