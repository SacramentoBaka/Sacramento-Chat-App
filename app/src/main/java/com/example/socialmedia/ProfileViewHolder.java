package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    TextView textViewName, textViewProfession, viewUserprofile, chatSendMessage;
    ImageView imageView;
    CardView cardView, chatToProfile;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(FragmentActivity fragmentActivity, String name, String uid, String prof, String url){


        cardView = itemView.findViewById(R.id.cardview_profile);
        textViewName = itemView.findViewById(R.id.tv_name_profile);
        textViewProfession = itemView.findViewById(R.id.tv_profession_profile);
        viewUserprofile = itemView.findViewById(R.id.viewUser_profile);
        imageView = itemView.findViewById(R.id.profile_imageview);

        Picasso.get().load(url).into(imageView);
        textViewName.setText(name);
        textViewProfession.setText(prof);

    }
    public void setProfileInChat(Application fragmentActivity, String name, String uid, String prof, String url){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = user.getUid();

        ImageView chatImageView = itemView.findViewById(R.id.idChatItemIMG);
        TextView chatNameTv = itemView.findViewById(R.id.idChatItemName);
        TextView chatProfessionTV = itemView.findViewById(R.id.idChatItemProfession);
        chatSendMessage = itemView.findViewById(R.id.idChatItemMessage);
        chatToProfile = itemView.findViewById(R.id.idChatToProfile);

        if(currentUID.equals(uid)){
            Picasso.get().load(url).into(chatImageView);
            chatNameTv.setText(name);
            chatProfessionTV.setText(prof);
            chatSendMessage.setVisibility(View.INVISIBLE);
        }else {
            chatSendMessage.setVisibility(View.VISIBLE);
            Picasso.get().load(url).into(chatImageView);
            chatNameTv.setText(name);
            chatProfessionTV.setText(prof);

        }

    }
}
