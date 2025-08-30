package org.taxivyapar.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VPadaptor extends FragmentPagerAdapter {
    ArrayList<Fragment> fragmentArrayList=new ArrayList<>();
    ArrayList<String> framtitle=new ArrayList<>();

    public VPadaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addfram(Fragment fragment,String string){
        fragmentArrayList.add(fragment);
        framtitle.add(string);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return framtitle.get(position);
    }
    public void clearFragments() {
        fragmentArrayList.clear();
        framtitle.clear();
        notifyDataSetChanged();
    }
}
