package com.joemerhej.recipook;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class MainActivity extends AppCompatActivity
{
    // adapter for the different tab fragments
    private MainTabsFragmentsAdapter mTabFragmentPagerAdapter;

    // ViewPager is the view that will hold all the fragments
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the adapter that will return a fragment for each of the primary sections of the activity.
        mTabFragmentPagerAdapter = new MainTabsFragmentsAdapter(getSupportFragmentManager());

        // set up the ViewPager with the adapter.
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(mTabFragmentPagerAdapter);

        // set up the TabLayout with the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

}
