package com.example.gamesnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gamesnews.R;
import com.example.gamesnews.adapter.FullCoverageNewsAdapter;
import com.example.gamesnews.adapter.NewsAdapter;
import com.example.gamesnews.model.NewsResponse;
import com.example.gamesnews.model.NewsResult;
import com.example.gamesnews.retrofit.FullCoverageNewsCallback;
import com.example.gamesnews.retrofit.NewsCallback;
import com.example.gamesnews.retrofit.NewsFetcher;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FullCoverageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvFullCoverage;
    private FullCoverageNewsAdapter newsAdapter;
    private TextView tvFullCoverageTitle;
    private ProgressBar pbFullCoverage;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String storyToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_coverage);

        // Inicializar DrawerLayout e NavigationView
        drawerLayout = findViewById(R.id.drawer_layout_full_coverage);
        NavigationView navigationView = findViewById(R.id.nav_view_full_coverage);
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

        // Inicializar os componentes da UI
        rvFullCoverage = findViewById(R.id.rv_full_coverage);
        tvFullCoverageTitle = findViewById(R.id.tv_full_coverage_title);
        pbFullCoverage = findViewById(R.id.pb_full_coverage);
        rvFullCoverage.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_full_coverage);

        tvFullCoverageTitle.setVisibility(View.GONE);

        // Receber os dados do NewsResponse via Intent
        storyToken = getIntent().getStringExtra("story_token");
        fetchNews(storyToken);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews(storyToken);
                // Usar Handler para definir o refresh como falso após 2 segundos (2000 milissegundos)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void fetchNews(String storyToken) {
        NewsFetcher newsFetcher = new NewsFetcher();

        newsFetcher.fetchFullCoverageNews(this,null, null, null, null, null, storyToken, new FullCoverageNewsCallback() {
            @Override
            public void onSuccess(NewsResponse newsResponse) {
                pbFullCoverage.setVisibility(View.GONE);
                newsAdapter = new FullCoverageNewsAdapter(newsResponse.getNewsResults(), FullCoverageActivity.this);
                rvFullCoverage.setAdapter(newsAdapter);
                tvFullCoverageTitle.setText(newsResponse.getTitle());
                tvFullCoverageTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable exception) {
                pbFullCoverage.setVisibility(View.GONE);
                Log.e("FullCoverageActivity", "Erro ao realizar requisição à API do Google: ", exception);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        }
        if(item.getItemId() == R.id.nav_populares){
            Intent intent = new Intent(FullCoverageActivity.this, GamePagerActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_favoritos){
            Intent intentFavoritos = new Intent(FullCoverageActivity.this, FavoriteNewsActivity.class);
            startActivity(intentFavoritos);
        }
        if(item.getItemId() == R.id.nav_opcoes){
            Intent intentOpcoes = new Intent(this, OptionsActivity.class);
            startActivity(intentOpcoes);
        }
        if (item.getItemId() == R.id.nav_logout) {
            // Realiza o logout do Firebase
            FirebaseAuth.getInstance().signOut();

            Intent intentLogin = new Intent(FullCoverageActivity.this, LoginActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        fetchNews(storyToken);
    }
}
