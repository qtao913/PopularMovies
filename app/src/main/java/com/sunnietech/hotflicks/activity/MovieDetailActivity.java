package com.sunnietech.hotflicks.activity;


import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sunnietech.hotflicks.fragment.MoviePosterMainFragment;
import com.sunnietech.hotflicks.R;
import com.sunnietech.hotflicks.adapter.DetailViewPagerAdapter;
import com.sunnietech.hotflicks.persistence.MovieContract;
import com.sunnietech.hotflicks.utility.DepthPageTransformer;

@TargetApi(11)
public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_LOADER = 0;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private static final String[] MOVIE_TABLE_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
//        return true;
//    }

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


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_TABLE_PROJECTION,
                null,
                null,
                MoviePosterMainFragment.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        Intent intent = getIntent();
        if (intent == null)
            return;
        int currentPos = intent.getIntExtra("current pos", 0);
        data.moveToPosition(currentPos);
        mPager = (ViewPager) findViewById(R.id.detail_view_pager);
        mPagerAdapter = new DetailViewPagerAdapter(getSupportFragmentManager(), data);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(currentPos);
        //mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
