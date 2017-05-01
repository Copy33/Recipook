package com.joemerhej.recipook;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/22/17.
 */

// this is the fragments pager adapter
public class MainTabsFragmentsAdapter extends FragmentPagerAdapter
{
    private final SparseArray<WeakReference<Fragment>> mInstantiatedFragments = new SparseArray<>();
    private ArrayList<String> mTabTitles;


    public MainTabsFragmentsAdapter(FragmentManager fm, ArrayList<String> TabTitles)
    {
        super(fm);
        mTabTitles = TabTitles;
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

    // create a fragment instance at the current position (with help of getItem above)
    @Override
    public Object instantiateItem(final ViewGroup container,final int position)
    {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mInstantiatedFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    // remove a fragment, from the sparse array, once it's destroyed
    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object)
    {
        mInstantiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getFragment(final int position)
    {
        final WeakReference<Fragment> wr = mInstantiatedFragments.get(position);
        return wr == null ? null : wr.get();
    }

    // return the total quantity of pages/tabs.
    @Override
    public int getCount()
    {
        return mTabTitles.size();
    }

    // set up the page/tab titles.
    @Override
    public CharSequence getPageTitle(int position)
    {
        return mTabTitles.get(position);
    }
}
