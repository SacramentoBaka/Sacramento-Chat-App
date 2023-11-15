package com.example.socialmedia;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PeopleFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, profileRef;
    private DocumentReference documentReference;
    private ImageView profileIMG;
    View view;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView recyclerView, recyclerView_profile;
//    RequestMember requestMember;
    TextView requestTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_people, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        database = FirebaseDatabase.getInstance();
        documentReference = db.collection("user").document(currentUserId);
        databaseReference = database.getReference("Requests").child(currentUserId);
        profileRef = database.getReference("All Users");
        profileIMG = view.findViewById(R.id.idPeopleFragProfileIMG);


//        requestMember = new RequestMember();
        recyclerView_profile = view.findViewById(R.id.id_recyclerview_peopleProfile);
        recyclerView = view.findViewById(R.id.id_recyclerview_request);
        requestTV = view.findViewById(R.id.id_requestTV);

        recyclerView_profile.setHasFixedSize(false);
        recyclerView_profile.setLayoutManager(new LinearLayoutManager(getActivity()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("url");
                            Picasso.get().load(url).into(profileIMG);
                        } else {
                            Snackbar.make(view, "Your profile in incomplete, please update your profile", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
    }
}