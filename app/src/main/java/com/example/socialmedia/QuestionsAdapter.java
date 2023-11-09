package com.example.socialmedia;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class QuestionsAdapter extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView timeResult, nameResult, questionResult;

    public QuestionsAdapter(@NonNull View itemView) {
        super(itemView);
    }


    public void setItem(FragmentActivity activity, String name, String url, String userID, String key, String question,
                   String privacy, String time){

        imageView = itemView.findViewById(R.id.idQuestionItemIMG);
        timeResult = itemView.findViewById(R.id.idQuestionItemTime);
        nameResult = itemView.findViewById(R.id.idQuestionItemName);
        questionResult = itemView.findViewById(R.id.idQuestionItemQuestion);

        Picasso.get().load(url).into(imageView);
        timeResult.setText(time);
        nameResult.setText(name);
        questionResult.setText(question);

    }
}
