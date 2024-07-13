package com.example.maxapp1.Utillities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maxapp1.Interfaces.HighScoreCallback;
import com.example.maxapp1.Models.HighScore;
import com.example.maxapp1.Models.HighScoreList;
import com.example.maxapp1.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>{
    private final ArrayList<HighScore> highScores;
    private HighScoreCallback highScoreCallback;

    public HighScoreAdapter(HighScoreList highScores) {
        this.highScores = highScores.getHighScores();
    }

    public void setHighScoreCallback(HighScoreCallback highScoreCallback) {
        this.highScoreCallback = highScoreCallback;
    }

    @NonNull
    @Override
    public HighScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_highscore_item, parent, false);
        return new HighScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighScoreViewHolder holder, int position) {
        HighScore highScore = highScores.get(position);
        String placementStr = (position + 1) + ".";
        holder.score_LBL_placement.setText(placementStr);
        holder.highScore_LBL_score.setText(String.valueOf(highScore.getScore()));
    }

    @Override
    public int getItemCount() {
        return highScores == null ? 0 : highScores.size();
    }

    private HighScore getItem(int position)
    {
        return highScores.get(position);
    }

    public class HighScoreViewHolder extends RecyclerView.ViewHolder{
        private final MaterialTextView score_LBL_placement;
        private final MaterialTextView highScore_LBL_score;

        public HighScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            score_LBL_placement = itemView.findViewById(R.id.score_LBL_placement);
            highScore_LBL_score = itemView.findViewById(R.id.highScore_LBL_score);
            CardView highscore_CARD_data = itemView.findViewById(R.id.highscore_CARD_data);
            highscore_CARD_data.setOnClickListener(v -> {
                if (highScoreCallback != null)
                    highScoreCallback.selectHighScore(getItem(getAdapterPosition()), getAdapterPosition());
            });
        }
    }
}
