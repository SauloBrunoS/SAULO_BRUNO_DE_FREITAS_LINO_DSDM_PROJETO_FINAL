package com.example.gamesnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gamesnews.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class WebViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private WebView webView;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.wv_news_site);

        // Configurações da WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  // Ativa o JavaScript

        webView.setWebViewClient(new WebViewClient());  // Abre as páginas na WebView em vez do navegador

        // Pega a URL da Intent
        String url = getIntent().getStringExtra("url");

        // Carrega a URL na WebView
        if (url != null) {
            webView.loadUrl(url);
        }

        drawerLayout = findViewById(R.id.drawer_layout_web_view);
        NavigationView navigationView = findViewById(R.id.nav_view_web_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar o botão de hambúrguer (toggle)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Substituir o comportamento do botão "voltar" usando OnBackPressedDispatcher
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);  // Desabilita o callback atual para permitir o comportamento padrão
                }
            }
        };

        // Adicionar o callback no OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        }
        if (item.getItemId() == R.id.nav_populares) {
            Intent intentGamePager = new Intent(this, GamePagerActivity.class);
            startActivity(intentGamePager);
        }
        if(item.getItemId() == R.id.nav_favoritos){
            Intent intentFavoritos = new Intent(WebViewActivity.this, FavoriteNewsActivity.class);
            startActivity(intentFavoritos);
        }
        if(item.getItemId() == R.id.nav_opcoes){
            Intent intentOpcoes = new Intent(this, OptionsActivity.class);
            startActivity(intentOpcoes);
        }
        if (item.getItemId() == R.id.nav_logout) {
            // Realiza o logout do Firebase
            FirebaseAuth.getInstance().signOut();

            Intent intentLogin = new Intent(WebViewActivity.this, LoginActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);  // Fecha o Drawer se estiver aberto
            } else {
                drawerLayout.openDrawer(GravityCompat.START);  // Abre o Drawer se estiver fechado
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
