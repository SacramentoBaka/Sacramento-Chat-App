package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private ImageView profileIMG, profileIMG2;
    private ImageButton menu;
    private TextView name, profession, bio, email, web,followersNo, postNo;
    private CardView updateProfile_IMG_BTN, profileToPost, mediaCardView;
    int followerCount;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileIMG = view.findViewById(R.id.idProfileFragIMG);
        profileIMG2 = view.findViewById(R.id.idProfileFragIMG2);
        name = view.findViewById(R.id.idProfileFragName);
        profession = view.findViewById(R.id.idProfileFragProfession);
        bio = view.findViewById(R.id.idProfileFragBio);
        email = view.findViewById(R.id.idProfileFragEmail);
        web = view.findViewById(R.id.idProfileFragWeb);
        updateProfile_IMG_BTN = view.findViewById(R.id.idProfileFragEdit);
        mediaCardView = view.findViewById(R.id.idProfileFragMedia);
        profileToPost = view.findViewById(R.id.idProfileToAddPost);
        menu = view.findViewById(R.id.idProfileFragMenu);
        followersNo = view.findViewById(R.id.idProfileFragFollowersNo);
        postNo = view.findViewById(R.id.idProfileFragPostNo);

        profileIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                startActivity(intent);
            }
        });
        profileToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetMenu sheetMenu = new BottomSheetMenu();
                sheetMenu.show(getFragmentManager(), "bottomSheet");
            }
        });

        updateProfile_IMG_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
            }
        });
        mediaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IndividualPost.class);
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference followersRef, postRef;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentID);
        followersRef = database.getReference("followers").child(currentID);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String nameResults = task.getResult().getString("name");
                            String bioResults = task.getResult().getString("bio");
                            String emailResults = task.getResult().getString("email");
                            String webResults = task.getResult().getString("web");
                            String professionResults = task.getResult().getString("profession");
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(profileIMG);
                            Picasso.get().load(url).into(profileIMG2);
                            name.setText(nameResults);
                            email.setText(emailResults);
                            web.setText(webResults);
                            profession.setText(professionResults);
                            bio.setText(bioResults);

                        }else{
                            Intent intent = new Intent(getActivity(), CreateProfile.class);
                            startActivity(intent);
                        }
                    }
                });

        // refernce for following
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    followerCount = (int) snapshot.getChildrenCount();
                    followersNo.setText(Integer.toString(followerCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}