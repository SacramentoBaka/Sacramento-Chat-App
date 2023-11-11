package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetAskFragment extends BottomSheetDialogFragment {

    private TextView relatedCardView, userQuestionCardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_ask_fragment, null);

        relatedCardView = view.findViewById(R.id.idAskBottomSheetRelated);
        userQuestionCardView = view.findViewById(R.id.idAskBottomSheetUserQuestions);

        relatedCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RelatedQuestions.class));
            }
        });
        userQuestionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UserQuestions.class));
            }
        });
        return view;
    }
}
