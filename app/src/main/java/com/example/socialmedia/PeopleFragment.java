package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    String currentUserId;
    RecyclerView recyclerView, recyclerView_profile;
    RequestMember requestMember;
    TextView requestTV;
    EditText searchEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_people, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        database = FirebaseDatabase.getInstance();
        documentReference = db.collection("user").document(currentUserId);
        databaseReference = database.getReference("Requests").child(currentUserId);
        profileRef = database.getReference("All Users");
        profileIMG = view.findViewById(R.id.idPeopleFragProfileIMG);
        searchEditText = view.findViewById(R.id.idPeopleSearch);


        requestMember = new RequestMember();
        recyclerView_profile = view.findViewById(R.id.id_recyclerview_peopleProfile);
        recyclerView = view.findViewById(R.id.id_recyclerview_request);
        requestTV = view.findViewById(R.id.id_requestTV);

        recyclerView_profile.setHasFixedSize(false);
        recyclerView_profile.setLayoutManager(new LinearLayoutManager(getActivity()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = searchEditText.getText().toString();

                FirebaseRecyclerOptions<All_UserMember> options1 =
                        new FirebaseRecyclerOptions.Builder<All_UserMember>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("All Users")
                                        .orderByChild("name")
                                        .startAt(query).endAt(query+"~"), All_UserMember.class)
                                .build();

                FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter1 =
                        new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                                holder.setProfile(getActivity(), model.getName(), model.getUserID(), model.getProfession(), model.getUrl());

                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUserID();
                                String profession = getItem(position).getProfession();

                                holder.viewUserprofile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (currentUserId.equals(uid)) {
                                            Snackbar.make(view, "Your are currently the User, goto your profile...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();

                                        } else {
                                            Intent intent = new Intent(getActivity(), ShowUser.class);
                                            intent.putExtra("n", name);
                                            intent.putExtra("u", url);
                                            intent.putExtra("uid", uid);
                                            intent.putExtra("profession", profession);
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }
                            @NonNull
                            @Override
                            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.profile, parent, false);

                                return new ProfileViewHolder(view);
                            }
                        };
                firebaseRecyclerAdapter1.startListening();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                recyclerView_profile.setLayoutManager(gridLayoutManager);
                recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);
            }
        });

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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    requestTV.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                } else {
                    requestTV.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseRecyclerOptions<All_UserMember> options1 =
                new FirebaseRecyclerOptions.Builder<All_UserMember>()
                        .setQuery(profileRef, All_UserMember.class)
                        .build();

        FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                        holder.setProfile(getActivity(), model.getName(), model.getUserID(), model.getProfession(), model.getUrl());

                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUserID();
                        String profession = getItem(position).getProfession();

                        holder.viewUserprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (currentUserId.equals(uid)) {
                                    Snackbar.make(view, "Your are currently the User, goto your profile...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                } else {
                                    Intent intent = new Intent(getActivity(), ShowUser.class);
                                    intent.putExtra("n", name);
                                    intent.putExtra("u", url);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("profession", profession);
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.profile, parent, false);

                        return new ProfileViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter1.startListening();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView_profile.setLayoutManager(gridLayoutManager);
        recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);


        FirebaseRecyclerOptions<RequestMember> options =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                        .setQuery(databaseReference, RequestMember.class)
                        .build();

        FirebaseRecyclerAdapter<RequestMember, RequestViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<RequestMember, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();

                        holder.setRequest(getActivity(), model.getName(), model.getUrl(), model.getProfession()
                                , model.getBio(), model.getPrivacy(), model.getEmail(), model.getFollowers(), model.getWebsite(), model.getUserid());

                        String uid = getItem(position).getUserid();
                        String name = getItem(position).getName();
                        String age = getItem(position).getProfession();
                        String bio = getItem(position).getBio();
                        String email = getItem(position).getEmail();
                        String privacy = getItem(position).getPrivacy();
                        String url = getItem(position).getUrl();
                        String website = getItem(position).getWebsite();

                        holder.button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = getItem(position).getName();
                                decline(name);
                            }
                        });
                        holder.button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uid = getItem(position).getUserid();
                                databaseReference1 = database.getReference("followers").child(currentUserId);
                                requestMember.setName(name);
                                requestMember.setUserid(uid);
                                requestMember.setUrl(url);
                                requestMember.setProfession(age);
                                String id = databaseReference1.push().getKey();
                                databaseReference1.child(uid).setValue(requestMember);
                                databaseReference.child(currentUserId).child(uid).removeValue();

                                Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                                decline(name);
                                // handling request notification
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.request_item, parent, false);

                        return new RequestViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private void decline(String name) {

        Query query = databaseReference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();
                }
                   Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///
            }
        });
    }
}