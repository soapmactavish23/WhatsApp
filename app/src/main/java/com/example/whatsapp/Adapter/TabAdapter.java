package com.example.whatsapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.whatsapp.fragment.ContatosFragment;
import com.example.whatsapp.fragment.ConversasFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] tituloTabs = {"Conversas", "Contatos"};

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new ConversasFragment();
                break;
            case 1:
                fragment = new ContatosFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tituloTabs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tituloTabs[position];
    }
}
