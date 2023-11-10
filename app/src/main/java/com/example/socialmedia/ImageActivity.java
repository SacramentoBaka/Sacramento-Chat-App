package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    private TextView name;
    private ImageView profileIMG;
    private CardView editBTN, deleteBTN;
    private DocumentReference reference;
    private String url;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        name = findViewById(R.id.idImageViewName);
        profileIMG = findViewById(R.id.idImageView);
        editBTN = findViewById(R.id.idImageViewEditBTN);
        deleteBTN = findViewById(R.id.idImageViewDeleteBTN);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();

        reference = db.collection("user").document(currentID);
        editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ImageActivity.this, "Edit Profile Photo", Toast.LENGTH_SHORT).show();
            }
        });
        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ImageActivity.this, "Delete Profile Photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String username = task.getResult().getString("name");
                    url = task.getResult().getString("url");
                    Picasso.get().load(url).into(profileIMG);
                    name.setText(username);
                }else {
                    Toast.makeText(ImageActivity.this, "Profile Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}