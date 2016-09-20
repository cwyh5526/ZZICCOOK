package com.example.user.zziccook.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.user.zziccook.Fragment.Recipe.RecipeAddFragment;
import com.example.user.zziccook.Fragment.Tab2Fragment;
import com.example.user.zziccook.Fragment.Tab5Fragment;
import com.example.user.zziccook.Fragment.Tab1Fragment;
import com.example.user.zziccook.Fragment.Tab4Fragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab1Fragment tab1 = new Tab1Fragment();
              // MeasureFragment tab1 = new MeasureFragment();
                return tab1;
            case 1:
                Tab2Fragment tab2 = new Tab2Fragment();
 //               RecipeListFragment tab2 = new RecipeListFragment();
                return tab2;
            case 2:
                RecipeAddFragment tab3 = new RecipeAddFragment();
                return tab3;
            case 3:
                Tab4Fragment tab4 = new Tab4Fragment();
                 //FavoriteFragment tab4 = new FavoriteFragment();
                return tab4;
            case 4:
                Tab5Fragment tab5 = new Tab5Fragment();
                return tab5;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object item) {
            return POSITION_NONE; //이거 해줘야 list가 refresh 됨!!

    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

