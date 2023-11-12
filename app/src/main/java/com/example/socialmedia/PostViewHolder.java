package com.example.socialmedia;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView profileImage, postImage;
    TextView nameTV, descriptionTV, likeTV, commentsTV, timeTV, nameProfileTV;
    ImageView likeBTN, commentBTN, moreBTN;
    DatabaseReference likesRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int likeCount;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setPost(FragmentActivity activity, String name, String url, String postUri, String time,
                        String userID, String type, String description) {

        profileImage = itemView.findViewById(R.id.id_image_profile);
        postImage = itemView.findViewById(R.id.id_post_image_list);
        nameProfileTV = itemView.findViewById(R.id.id_post_username);
        timeTV = itemView.findViewById(R.id.id_post_time);
        descriptionTV = itemView.findViewById(R.id.id_post_description_list);
        commentsTV = itemView.findViewById(R.id.id_post_comments_list);
        likeTV = itemView.findViewById(R.id.idLikes);
        likeBTN = itemView.findViewById(R.id.idLikeIMG);
        commentBTN = itemView.findViewById(R.id.idCommentIMG);
        moreBTN = itemView.findViewById(R.id.id_post_moreButton);

        SimpleExoPlayer simpleExoPlayer;
        PlayerView playerView = itemView.findViewById(R.id.id_post_video_list);

        if (type.equals("iv")) {
            Picasso.get().load(url).into(profileImage);
            Picasso.get().load(postUri).into(postImage);
            descriptionTV.setText(description);
            timeTV.setText(time);
            nameProfileTV.setText(name);
            playerView.setVisibility(View.INVISIBLE);
        } else if (type.equals("vv")) {
            postImage.setVisibility(View.INVISIBLE);
            Picasso.get().load(url).into(profileImage);
            descriptionTV.setText(description);
            timeTV.setText(time);
            nameProfileTV.setText(name);

            try {
                Uri video = Uri.parse(postUri);
                simpleExoPlayer = new SimpleExoPlayer.Builder(activity.getApplicationContext()).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(video);
                simpleExoPlayer.addMediaItem(mediaItem);
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);

            } catch (Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void likeChecker(String postKey) {

        likeBTN = itemView.findViewById(R.id.idLikeIMG);
        likesRef = database.getReference("Post Likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postKey).hasChild(userID)) {
                    likeBTN.setImageResource(R.drawable.round_favorite_24);
                    likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    likeTV.setText(Integer.toString(likeCount) + " likes");
                } else {
                    likeBTN.setImageResource(R.drawable.round_favorite_border_24);
                    likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    likeTV.setText(Integer.toString(likeCount) + " likes");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
