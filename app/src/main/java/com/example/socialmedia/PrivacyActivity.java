package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class PrivacyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String[] status = {"Choose Any One", "Public", "Private"};
    private TextView statusTV;
    private Spinner spinner;
    private Button saveButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        statusTV = findViewById(R.id.idPrivacyStatus);
        spinner = findViewById(R.id.idPrivacySpinner);
        saveButton = findViewById(R.id.idPrivacyBTN);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        reference = db.collection("user").document(currentID);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, status);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrivacy();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

        Toast.makeText(this, "Select Value", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.getResult().exists()){
                            String privacy_result = task.getResult().getString("privacy");
                            statusTV.setText("Your Account is Now: " + privacy_result);
                        }else {
                            Toast.makeText(PrivacyActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void savePrivacy() {

        final  String value = spinner.getSelectedItem().toString();
        if(value == "Choose Any One"){
            Toast.makeText(this, "Please select value", Toast.LENGTH_SHORT).show();
        }else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentID = user.getUid();

            final  DocumentReference sfDocRef = db.collection("user").document(currentID);

            db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                            transaction.update(sfDocRef, "privacy", value);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(PrivacyActivity.this, "Status  updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PrivacyActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}