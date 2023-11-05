package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {

    private EditText name, bio, profession, email, web;
    private Button button;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String currentID = user.getUid();
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        documentReference = db.collection("user").document(currentID);

        name = findViewById(R.id.idUpdateProfileTextName);
        profession = findViewById(R.id.idUpdateProfileTextProfession);
        bio = findViewById(R.id.idUpdateProfileTextBio);
        email = findViewById(R.id.idUpdateProfileTextEmail);
        web = findViewById(R.id.idUpdateProfileTextWeb);
        button = findViewById(R.id.idUpdateBTN);
        progressBar = findViewById(R.id.idUpdateProfileProgressBar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfileData();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){
                    String nameResults = task.getResult().getString("name");
                    String bioResults = task.getResult().getString("bio");
                    String emailResults = task.getResult().getString("email");
                    String webResults = task.getResult().getString("web");
                    String professionResults = task.getResult().getString("profession");

                    name.setText(nameResults);
                    email.setText(emailResults);
                    web.setText(webResults);
                    profession.setText(professionResults);
                    bio.setText(bioResults);
                } else{
                    Toast.makeText(UpdateProfile.this, "Fields not found", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    private void updateProfileData() {

        progressBar.setVisibility(View.VISIBLE);
        String nameText = name.getText().toString();
        String boiText = bio.getText().toString();
        String webText = web.getText().toString();
        String professionText = profession.getText().toString();
        String emailText = email.getText().toString();

        final  DocumentReference sfDocRef = db.collection("user").document(currentID);

        db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                        transaction.update(sfDocRef, "name", nameText);
                        transaction.update(sfDocRef, "profession", professionText);
                        transaction.update(sfDocRef, "email", emailText);
                        transaction.update(sfDocRef, "bio", boiText);
                        transaction.update(sfDocRef, "web", webText);

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UpdateProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UpdateProfile.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}