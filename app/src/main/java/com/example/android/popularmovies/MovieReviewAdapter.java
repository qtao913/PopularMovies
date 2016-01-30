package com.example.android.popularmovies;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by qlzh727 on 1/28/16.
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {
    private View itemView;
    private String[] author;
    private String[] content;
    private int lastPosition = -1;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mAuthorView;
        private TextView mContentView;
        private CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            mAuthorView = (TextView)itemView.findViewById(R.id.review_author);
            mContentView = (TextView)itemView.findViewById(R.id.review_content);
            cardView = (CardView)itemView.findViewById(R.id.review_card_view);
        }
    }

    public MovieReviewAdapter(String[] author, String[] content, Activity activity) {
        this.author = author;
        this.content = content;
        this.activity = activity;
    }

    @Override
    public MovieReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review, parent, false);
        ViewHolder vh = new ViewHolder(v);
        itemView = v;
        return vh;
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.ViewHolder viewHolder, int position) {
        viewHolder.mAuthorView.setText(author[position]);
        viewHolder.mContentView.setText(content[position]);
        //setAnimation(viewHolder.cardView, position);
    }

    @Override
    public int getItemCount() {
        return author.length;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
