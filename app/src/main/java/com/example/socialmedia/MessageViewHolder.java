package com.example.socialmedia;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView senderTV, receiverTV;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setMessage(Application application,String name, String time, String date,
                           String message, String type, String senderUID, String receiverUID){

        senderTV = itemView.findViewById(R.id.idSenderTV);
        receiverTV = itemView.findViewById(R.id.idReceiverTV);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = user.getUid();

        if (currentUID.equals(senderUID)){
            receiverTV.setVisibility(View.GONE);
            senderTV.setText(message);
        }else if (currentUID.equals(receiverUID)){
            senderTV.setVisibility(View.GONE);
            receiverTV.setText(message);
        }
    }
}
