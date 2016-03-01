package com.sunnietech.hotflicks.adapter;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sunnietech.hotflicks.fragment.MovieDetailInfoFragment;
import com.sunnietech.hotflicks.persistence.MovieContract;


/**
 * Created by qlzh727 on 1/15/16.
 */
public class DetailViewPagerAdapter extends FragmentStatePagerAdapter {
    int numberOfPage;
    Cursor dataRecord;

    public static final int COLUMN_ID = 0;
    public DetailViewPagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        dataRecord = cursor;
        numberOfPage = dataRecord.getCount();
    }

    @Override
    public Fragment getItem(int i) {
        dataRecord.moveToPosition(i);
        Uri uri = MovieContract.MovieEntry.buildMovieUri(dataRecord.getLong(COLUMN_ID));
        return MovieDetailInfoFragment.create(uri);
    }

    @Override
    public int getCount() {
        return numberOfPage;
    }
}
