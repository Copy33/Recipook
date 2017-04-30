package com.joemerhej.recipook;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
    private MainTabsFragmentsAdapter mTabFragmentPagerAdapter;

    // ViewPager is the view that will hold all the fragments
    private ViewPager mViewPager;

    // Floating action button shared by all tab fragments
    private com.github.clans.fab.FloatingActionButton mMainFab;

    // interface for fab listener
    public interface OnMainFabClickListener
    {
        void onMainFabClick();
    }

    // fab click listener that every fragment will implement
    private OnMainFabClickListener mMainFabClickListener;


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

        // set up the FloatingActionButton
        mMainFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.main_activity_fab);
        mMainFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mMainFabClickListener.onMainFabClick();
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        mMainFab.setImageResource(R.drawable.ic_add_white_24dp);
                        RecipeListTabFragment recipeFragment = (RecipeListTabFragment) mTabFragmentPagerAdapter.getItem(position);
                        setMainFabClickListener(recipeFragment.getMainFabClickListener());
                        return;
                    case 1:
                        mMainFab.setImageResource(R.drawable.ic_clear_white_24dp);
                        ShoppingListTabFragment shoppingFragment = (ShoppingListTabFragment) mTabFragmentPagerAdapter.getItem(position);
                        setMainFabClickListener(shoppingFragment.getMainFabClickListener());
                        return;
                    case 2:
                        mMainFab.setImageResource(R.drawable.ic_add_white_24dp);
                        return;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    // setter for the fab click listener
    public void setMainFabClickListener(final OnMainFabClickListener mainFabClickListener)
    {
        mMainFabClickListener = mainFabClickListener;
    }

}
