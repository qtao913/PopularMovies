package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


public class MovieReviewActivity extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] contentData = new String[] {
            "This is a dummy content. So RecyclerView is the appropriate view to use when you have multiple " +
                    "items of the same type and it’s very likely that your user’s device cannot present all of those items at once. " +
                    "Possible examples are contacts, customers, audio files and so on. ",
            "Continued. Recycle (view): A view previously used to display data for a specific adapter position may be placed in a cache for later reuse " +
                    "to display the same type of data again later. This can drastically improve performance " +
                    "by skipping initial layout inflation or construction",
            "RecyclerView introduces an additional level of abstraction between the RecyclerView.Adapter and RecyclerView.LayoutManager to be able to detect data set changes in batches during a layout calculation. " +
                    "This saves LayoutManager from tracking adapter changes to calculate animations. It also helps with performance because all view bindings happen at the same time and " +
                    "unnecessary bindings are avoided.",
            "We recommend that this project use Picasso, a powerful library that will handle image loading and caching on your behalf. If you prefer, you’re welcome to use an alternate library such as Glide.\n" +
                    "We’ve included this to reduce unnecessary extra work and help you focus on applying your app development skills.\n",
            "This is a dummy content. So RecyclerView is the appropriate view to use when you have multiple " +
                    "items of the same type and it’s very likely that your user’s device cannot present all of those items at once. " +
                    "Possible examples are contacts, customers, audio files and so on. "
    };
    private String[] authorData = new String[] {
            "Kitty 913",
            "Uncle smelly",
            "abcd2016",
            "Jay Chow",
            "hateGames"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);
        mRecyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieReviewAdapter(authorData, contentData, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
