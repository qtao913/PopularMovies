package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoviePosterMainFragment extends Fragment {
    private AndroidImageAdapter imageAdapter;
    private ArrayList<Movie> movieList;
    static Movie[] movies = {
            new Movie(R.drawable.test1),
            new Movie(R.drawable.test2),
            new Movie(R.drawable.test3),
            new Movie(R.drawable.test4)
    };
    public MoviePosterMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            movieList = new ArrayList<>(Arrays.asList(movies));
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movie");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movie", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        populateMovieListView(rootView, movieList);
        return rootView;
    }

    private void populateMovieListView(View rootView, List<Movie> movies) {
        imageAdapter = new AndroidImageAdapter(getActivity(), movies);
        GridView movieGridView = (GridView) rootView.findViewById(R.id.grid_movie_view);
        movieGridView.setAdapter(imageAdapter);
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                Bundle detail = new Bundle();
                detail.putParcelable("movie", movieList.get(position));
                intent.putExtras(detail);
                startActivity(intent);
            }
        });
    }
}
