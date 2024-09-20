package com.example.gamesnews.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gamesnews.fragment.GameFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String[] gameTitles;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String[] gameTitles) {
        super(fragmentActivity);
        this.gameTitles = gameTitles;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Cria um fragment específico para cada posição com o título do jogo correspondente
        return GameFragment.newInstance(gameTitles[position]);
    }

    @Override
    public int getItemCount() {
        return gameTitles.length;  // Número de páginas é igual ao número de jogos
    }
}

