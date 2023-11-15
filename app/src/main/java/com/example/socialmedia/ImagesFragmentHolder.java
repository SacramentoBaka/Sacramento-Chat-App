package com.example.socialmedia;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ImagesFragmentHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    public ImagesFragmentHolder(@NonNull View itemView) {
        super(itemView);

    }
    public void setImage(FragmentActivity activity, String name, String url, String postUri, String time,
                         String userID, String type, String description) {

        imageView = itemView.findViewById(R.id.id_post_individual);
        Picasso.get().load(postUri).into(imageView);

    }
}
