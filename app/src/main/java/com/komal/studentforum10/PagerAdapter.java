package com.komal.studentforum10;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs) {

        super(fm);
        this.mNoOfTabs = NumberOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 1:
                NewThreadFragment newThreadFragment = new NewThreadFragment();
                return  newThreadFragment;

            case 0:
                NewPostFragment newPostFragment = new NewPostFragment();
                return newPostFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
