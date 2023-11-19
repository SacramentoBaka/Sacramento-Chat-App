package com.example.socialmedia;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class ShowUser extends AppCompatActivity {


    TextView nametv, professiontv, biotv, emailtv, websitetv, requesttv, button, followers_tv, posts_tv;
    RoundedImageView imageView;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, databaseReference2,
            postnoref;
    CardView followers_cv, posts_cd;
    String url, name, age, email, privacy, privacyStatus, website, bio, userid;
    RequestMember requestMember;
    String name_result;
    String uidreq, namereq, urlreq, professionreq;
    int postNo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference, documentReference1;

    int followercount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        database = FirebaseDatabase.getInstance();

        requestMember = new RequestMember();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        nametv = findViewById(R.id.name_tv_showprofile);
        professiontv = findViewById(R.id.age_tv_showprofile);
        biotv = findViewById(R.id.bio_tv_showprofile);
        emailtv = findViewById(R.id.email_tv_showProfile);
        imageView = findViewById(R.id.imageView_showprofile);
        websitetv = findViewById(R.id.website_tv_showprofile);
        button = findViewById(R.id.btn_requestshowprofile);
        requesttv = findViewById(R.id.tv_requestshowprofile);

        followers_tv = findViewById(R.id.followerNo_tv);
        posts_tv = findViewById(R.id.postsNo_tv);
        followers_cv = findViewById(R.id.followers_cardview);
        posts_cd = findViewById(R.id.posts_cardview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("u");
            name = extras.getString("n");
            userid = extras.getString("uid");
            age = extras.getString("profession");

        } else {

        }
        databaseReference = database.getReference("Requests").child(userid);
        databaseReference1 = database.getReference("followers").child(userid);
        documentReference = db.collection("user").document(userid);
        postnoref = database.getReference("User Posts").child(userid);
        databaseReference2 = database.getReference("followers");
        documentReference1 = db.collection("user").document(currentUserId);


        websitetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String url = websitetv.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(ShowUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postNo = (int) snapshot.getChildrenCount();
                posts_tv.setText(Integer.toString(postNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = button.getText().toString();
                if (status.equals("Follow")) {
                    follow();
                }else if (status.equals("Request sent")) {
                    delRequest();
                }else if (status.equals("Following")) {
                    unFollow();
                }
            }
        });
    }
    private void delRequest() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        databaseReference.child(currentUserId).removeValue();
        button.setText("Follow");
        requesttv.setText("Request Cancelled");
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String name_result = task.getResult().getString("name");
                            String age_result = task.getResult().getString("profession");
                            String bio_result = task.getResult().getString("bio");
                            String email_result = task.getResult().getString("email");
                            String web_result = task.getResult().getString("web");
                            String Url = task.getResult().getString("url");
                            privacyStatus = task.getResult().getString("privacy");

                            if (privacyStatus.equals("Public")) {
                                professiontv.setText(age_result);
                                nametv.setText(name_result);
                                biotv.setText(bio_result);
                                emailtv.setText(email_result);
                                websitetv.setText(web_result);
                                Picasso.get().load(Url).into(imageView);
                                requesttv.setVisibility(View.GONE);
                            } else {

                                String u = button.getText().toString();
                                if (u.equals("Following")) {
                                    professiontv.setText(age_result);
                                    nametv.setText(name_result);
                                    biotv.setText(bio_result);
                                    emailtv.setText(email_result);
                                    websitetv.setText(web_result);
                                    Picasso.get().load(Url).into(imageView);
                                    requesttv.setVisibility(View.GONE);
                                } else {
                                    professiontv.setText("*****************");
                                    nametv.setText(name_result);
                                    biotv.setText("*****************");
                                    emailtv.setText("*****************");
                                    websitetv.setText("*****************");
                                    Picasso.get().load(Url).into(imageView);
                                    requesttv.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Toast.makeText(ShowUser.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        documentReference1.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            namereq = task.getResult().getString("name");
                            professionreq = task.getResult().getString("profession");
                            urlreq = task.getResult().getString("url");

                        } else {
                            Toast.makeText(ShowUser.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        // refernce for following
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    followercount = (int) snapshot.getChildrenCount();
                    followers_tv.setText(Integer.toString(followercount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(currentUserId)) {
                    button.setText("Request sent");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).hasChild(currentUserId)) {
                    button.setText("Following");
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void follow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();


        if (privacyStatus.equals("Public")) {
            button.setText("Following");
            requestMember.setUserid(currentUserId);
            requestMember.setProfession(professionreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);
            databaseReference1.child(currentUserId).setValue(requestMember);
            requesttv.setText(" ");
        } else {
            button.setText("Requested");
            requestMember.setUserid(currentUserId);
            requestMember.setProfession(professionreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);
            databaseReference.child(currentUserId).setValue(requestMember);
            requesttv.setText("Request is sent");
        }
    }

    private void unFollow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUser.this);
        builder.setTitle("Unfollow")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentUserId).removeValue();
                        button.setText("Follow");
                        followers_tv.setText("No Followers");
                        Toast.makeText(ShowUser.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }
}