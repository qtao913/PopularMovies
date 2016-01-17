package com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by qlzh727 on 1/15/16.
 */
public class DetailViewPagerAdapter extends FragmentStatePagerAdapter {
    int numberOfPage;
    public DetailViewPagerAdapter(FragmentManager fm, int numberOfPage) {
        super(fm);
        this.numberOfPage = numberOfPage;
    }

    @Override
    public Fragment getItem(int i) {
        return new MovieDetailInfoFragment();
    }

    @Override
    public int getCount() {
        return numberOfPage;
    }
}
