package com.lit.litnotes.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lit.litnotes.Fragment.NotesFragment;
import com.lit.litnotes.Fragment.TableFragment;
import com.lit.litnotes.Fragment.TasksFragment;

public class HomeViewPageAdapter extends FragmentPagerAdapter {

    public HomeViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new NotesFragment();
                break;
            case 1:
                fragment = new TasksFragment();
                break;
            case 2:
                fragment = new TableFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
