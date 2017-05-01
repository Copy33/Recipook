package com.joemerhej.recipook;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class MainActivity extends AppCompatActivity
{
    // tab titles of the main activity
    private ArrayList<String> mTabTitles = new ArrayList<>();

    // ViewPager is the view that will hold all the fragments
    private ViewPager mViewPager;

    // adapter for the different tab fragments
    private MainTabsFragmentsAdapter mTabFragmentPagerAdapter;

    // Floating action button shared by all tab fragments
    private com.github.clans.fab.FloatingActionButton mMainFab;

    // starting tab position
    private int mStartingTabPosition;

    // interface for fab listener
    public interface OnMainFabClickListener
    {
        void onMainFabClick();
    }

    // fab click listener that every fragment will implement
    private OnMainFabClickListener mMainFabClickListener;


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ACTIVITY CREATE FUNCTION
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartingTabPosition = getIntent().getIntExtra("TabToShow", 0);

        mTabTitles.add("Recipe List");
        mTabTitles.add("Shopping List");
        mTabTitles.add("tab 3");

        // create the adapter that will return a fragment for each of the primary sections of the activity.
        mTabFragmentPagerAdapter = new MainTabsFragmentsAdapter(getSupportFragmentManager(), mTabTitles);

        // set up the ViewPager with the adapter.
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(mTabFragmentPagerAdapter);
        mViewPager.setCurrentItem(mStartingTabPosition);

        // set up the TabLayout with the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // set up the viewpager on page change listener
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                setupMainFab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });

        // set up the main fab
        mMainFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.main_activity_fab);
        setupMainFab(mStartingTabPosition);
        mMainFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // if the click listener is not set, set it to the listener of the recipe page (landing page)
                if (mMainFabClickListener == null)
                {
                    setupMainFab(mStartingTabPosition);
                }

                // if the click listener is set, call the interface onMainFabClick function;
                mMainFabClickListener.onMainFabClick();
            }
        });
    }

    void setupMainFab(int tabPosition)
    {
        Fragment fragment = mTabFragmentPagerAdapter.getFragment(tabPosition);

        switch (tabPosition)
        {
            case 0:
                mMainFab.setImageResource(R.drawable.ic_add_white_24dp);
                if (fragment != null)
                    setMainFabClickListener(((RecipeListTabFragment) fragment).mMainFabClickListener);
                return;
            case 1:
                mMainFab.setImageResource(R.drawable.ic_clear_white_24dp);
                if (fragment != null)
                    setMainFabClickListener(((ShoppingListTabFragment) fragment).mMainFabClickListener);
                return;
            case 2:
                mMainFab.setImageResource(R.drawable.ic_add_white_24dp);
                return;

        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // SETTER FOR MAIN FAB LISTENER
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void setMainFabClickListener(final OnMainFabClickListener mainFabClickListener)
    {
        mMainFabClickListener = mainFabClickListener;
    }

}


























