package com.joemerhej.recipook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Joe Merhej on 1/22/17.
 */

// this is the fragments pager adapter
public class MainTabsFragmentsAdapter extends FragmentPagerAdapter
{
    public MainTabsFragmentsAdapter(FragmentManager fm)
    {
        super(fm);
    }

    // getItem is called to instantiate the right fragment for the given page/tab.
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return RecipeListTabFragment.Instance(position);
            case 1:
                return ShoppingListTabFragment.Instance(position);
            case 2:
                return PlaceholderFragment.newInstance(position);
            default:
                return null;
        }
    }

    // return the total quantity of pages/tabs.
    @Override
    public int getCount()
    {
        return 3;
    }

    // set up the page/tab titles.
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Recipes";
            case 1:
                return "Shopping List";
            case 2:
                return "TAB 3";
            default:
                return null;
        }
    }
}
