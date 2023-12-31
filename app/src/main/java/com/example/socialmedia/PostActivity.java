package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private ImageView imageView, profileIMG;
    private VideoView videoView;
    private CardView chooseFile, uploadFile;
    private ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    private UploadTask uploadTask;
    private EditText descriptionET;
    private String url, name;
    private StorageReference storageReference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference db1, db2, db3;
    private MediaController mediaController;
    private String type;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserID = user.getUid();
    private PostMember postMember;

    int clickCount = 0;
    long startTime;
    long duration;
    static final int MAX_DURATION = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postMember = new PostMember();
        mediaController = new MediaController(this);
        imageView = findViewById(R.id.idPostImageView);
        profileIMG = findViewById(R.id.idPostProfileIMG);
        videoView = findViewById(R.id.idPostVideoView);
        progressBar = findViewById(R.id.idProgressBar);
        descriptionET = findViewById(R.id.idPostDescription);
        chooseFile = findViewById(R.id.idPostChooseFileBTN);
        uploadFile = findViewById(R.id.idPostUploadFileBTN);
        db1 = database.getReference("All Images").child(currentUserID);
        db2 = database.getReference("All Videos").child(currentUserID);
        db3 = database.getReference("All Posts");
        storageReference = FirebaseStorage.getInstance().getReference("User Posts");


        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost();
            }
        });
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data!= null || data.getData() != null){
            selectedUri = data.getData();
            if(selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";

            } else if (selectedUri.toString().contains("video")) {

                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            }else
                Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentUserID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    name = task.getResult().getString("name");
                    url = task.getResult().getString("url");
                    Picasso.get().load(url).into(profileIMG);



                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createPost() {

        progressBar.setVisibility(View.VISIBLE);
        String description = descriptionET.getText().toString();
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveDate = currentDate.format(callForDate.getTime());
        // for Dat
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        final String saveTime = currentTime.format(callForTime.getTime());


        String time = saveDate + ":" + saveTime;
        if(!TextUtils.isEmpty(description) || selectedUri != null ){

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);
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

                        if(type.equals("iv")){

                            postMember.setName(name);
                            postMember.setTime(time);
                            postMember.setDescription(description);
                            postMember.setPostUri(downloadUri.toString());
                            postMember.setUserID(currentUserID);
                            postMember.setUrl(url);
                            postMember.setType("iv");

                            String id = db1.push().getKey();
                            db1.child(id).setValue(postMember);

                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postMember);
                            Toast.makeText(PostActivity.this, "Post Successfully created", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        } else if (type.equals("vv")) {
                            postMember.setName(name);
                            postMember.setTime(time);
                            postMember.setDescription(description);
                            postMember.setPostUri(downloadUri.toString());
                            postMember.setUserID(currentUserID);
                            postMember.setUrl(url);
                            postMember.setType("vv");

                            String id3 = db2.push().getKey();
                            db2.child(id3).setValue(postMember);

                            String id4 = db3.push().getKey();
                            db3.child(id4).setValue(postMember);
                            Toast.makeText(PostActivity.this, "Post Successfully created", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }else {
                            Toast.makeText(PostActivity.this, "Cannot Create Post", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                }
            });
        }else {

        }
    }
}