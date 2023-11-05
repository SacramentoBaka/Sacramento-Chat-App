package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private ImageView profileIMG;
    private TextView name, profession, bio, email, web;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileIMG = view.findViewById(R.id.idProfileFragIMG);
        name = view.findViewById(R.id.idProfileFragName);
        profession = view.findViewById(R.id.idProfileFragProf);
        bio = view.findViewById(R.id.idProfileFragBio);
        email = view.findViewById(R.id.idProfileFragEmail);
        web = view.findViewById(R.id.idProfileFragWeb);

        return view;
    }
    @Override
    public void onClick(View view) {

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentID);
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
    }
}