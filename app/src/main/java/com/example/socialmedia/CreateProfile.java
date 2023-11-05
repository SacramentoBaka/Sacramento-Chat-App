package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {
    private EditText name, bio, profession, email, web;
    private Button button;
    private ImageView profileIMG;
    private ProgressBar progressBar;
    private Uri imageUri;
    private UploadTask uploadTask;
    private StorageReference storageReference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private static final int PICK_IMAGE = 1;
    private All_UserMember member;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        member = new All_UserMember();
        profileIMG = findViewById(R.id.idCreateProfileIMG);
        name = findViewById(R.id.idCreateProfileTextName);
        profession = findViewById(R.id.idCreateProfileTextProfession);
        bio = findViewById(R.id.idCreateProfileTextBio);
        email = findViewById(R.id.idCreateProfileTextEmail);
        web = findViewById(R.id.idCreateProfileTextWeb);
        button = findViewById(R.id.idCreateBTN);
        progressBar = findViewById(R.id.idCreateProfileProgressBar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUserId = user.getUid();
        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All Users");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
        profileIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE || requestCode == RESULT_OK || data != null || data.getData() != null){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(profileIMG);
            }
        }catch (Exception e){
            Toast.makeText(this, "error " + e, Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }
    private void uploadData() {

        String nameText = name.getText().toString();
        String boiText = bio.getText().toString();
        String webText = web.getText().toString();
        String professionText = profession.getText().toString();
        String emailText = email.getText().toString();

        if(!TextUtils.isEmpty(nameText) || !TextUtils.isEmpty(boiText) || !TextUtils.isEmpty(webText) ||
                !TextUtils.isEmpty(professionText) || !TextUtils.isEmpty(emailText) || imageUri != null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
            uploadTask = reference.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        Map<String, String> profile = new HashMap<>();
                        profile.put("name", nameText);
                        profile.put("userID", currentUserId);
                        profile.put("profession", professionText);
                        profile.put("bio", boiText);
                        profile.put("url", downloadUri.toString());
                        profile.put("email", emailText);
                        profile.put("web", webText);
                        profile.put("privacy", "Public");

                        member.setName(nameText);
                        member.setUserID(currentUserId);
                        member.setProfession(professionText);
                        member.setUrl(downloadUri.toString());

                        databaseReference.child(currentUserId).setValue(member);
                        documentReference.set(profile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(CreateProfile.this, "Profile Created!...", Toast.LENGTH_SHORT).show();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }, 2000);
                                    }
                                });
                    }
                }
            });
        }else {
            Toast.makeText(this, "please fill all fields", Toast.LENGTH_SHORT).show();
        }

    }
}