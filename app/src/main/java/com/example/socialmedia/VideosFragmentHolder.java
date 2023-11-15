package com.example.socialmedia;

import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideosFragmentHolder extends RecyclerView.ViewHolder {

    SimpleExoPlayer exoPlayer;
    PlayerView playerView;

    public VideosFragmentHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setVideo(FragmentActivity activity, String name, String url, String postUri, String time,
                         String userID, String type, String description) {

        playerView = itemView.findViewById(R.id.id_post_video);

        try {
            Uri video = Uri.parse(postUri);
            exoPlayer = new SimpleExoPlayer.Builder(activity.getApplicationContext()).build();
            playerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(video);
            exoPlayer.addMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(false);

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
