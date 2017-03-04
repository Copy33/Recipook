package com.joemerhej.recipook;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class MainActivity extends AppCompatActivity
{
    // adapter for the different tab fragments
    private MainTabsFragmentAdapter mTabFragmentPagerAdapter;

    // ViewPager is the view that will hold all the fragments
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the adapter that will return a fragment for each of the primary sections of the activity.
        mTabFragmentPagerAdapter = new MainTabsFragmentAdapter(getSupportFragmentManager());

        // set up the ViewPager with the adapter.
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(mTabFragmentPagerAdapter);

        // set up the TabLayout with the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // set up the FloatingActionButton and set a listener that will show the Snackbar when it's clicked
        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Switched to Grid view.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                RecipeListTabFragment.mStaggeredLayoutManager.setSpanCount(2);
            }
        });
    }

}
