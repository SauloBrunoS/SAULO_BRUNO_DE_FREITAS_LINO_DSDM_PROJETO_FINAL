package com.example.gamesnews.activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gamesnews.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RadioButton rbDay, rbNight;
    private AutoCompleteTextView actvCountry, actvLanguage;
    private SharedPreferences sharedPreferences;
    private static final String MODE_KEY = "mode";
    private static final String COUNTRY_KEY = "country";
    private static final String LANGUAGE_KEY = "language";
    private DrawerLayout drawerLayout;
    private final ArrayList<JSONObject> countryList = new ArrayList<>();
    private final ArrayList<JSONObject> languageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        String PREFS_NAME = getString(R.string.nome_arquivo_preferencias_do_usuario);
        rbDay = findViewById(R.id.rb_day);
        rbNight = findViewById(R.id.rb_night);
        actvCountry = findViewById(R.id.actv_country);
        actvLanguage = findViewById(R.id.actv_language);
        Button btnSave = findViewById(R.id.btn_save);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar DrawerLayout e NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_options);
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

        // Load country and language options
        loadCountryAndLanguageOptions();

        // Load saved preferences
        loadPreferences();

        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadCountryAndLanguageOptions() {
        try {
            // Carregar lista de países com código
            String countriesJson = loadJSONFromAsset("google-countries.json");
            JSONArray countriesArray = new JSONArray(countriesJson);
            for (int i = 0; i < countriesArray.length(); i++) {
                countryList.add(countriesArray.getJSONObject(i));
            }

            // Adicionar nomes de países ao AutoCompleteTextView
            ArrayList<String> countryNames = new ArrayList<>();
            for (JSONObject country : countryList) {
                countryNames.add(country.getString("country_name"));
            }
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,R.layout.dropdown_item, countryNames);
            actvCountry.setAdapter(countryAdapter);
            actvCountry.setThreshold(1);

            // Carregar lista de línguas com código
            String languagesJson = loadJSONFromAsset("google-languages.json");
            JSONArray languagesArray = new JSONArray(languagesJson);
            for (int i = 0; i < languagesArray.length(); i++) {
                languageList.add(languagesArray.getJSONObject(i));
            }

            // Adicionar nomes de línguas ao AutoCompleteTextView
            ArrayList<String> languageNames = new ArrayList<>();
            for (JSONObject language : languageList) {
                languageNames.add(language.getString("language_name"));
            }
            ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, languageNames);
            actvLanguage.setAdapter(languageAdapter);
            actvLanguage.setThreshold(1);

        } catch (Exception e) {
            Log.e("OptionsActivity", "Erro ao carregar países ou línguas", e);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Salvar o modo
        if (rbDay.isChecked()) {
            editor.putString(MODE_KEY, "day");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (rbNight.isChecked()) {
            editor.putString(MODE_KEY, "night");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Salvar o código do país e da língua
        String selectedCountryName = actvCountry.getText().toString();
        String selectedLanguageName = actvLanguage.getText().toString();

        String countryCode = getCountryCodeByName(selectedCountryName);
        String languageCode = getLanguageCodeByName(selectedLanguageName);

        if (countryCode != null) {
            editor.putString(COUNTRY_KEY, countryCode);
        }

        if (languageCode != null) {
            editor.putString(LANGUAGE_KEY, languageCode);
        }

        editor.apply();

        Toast.makeText(this, "Configurações salvas", Toast.LENGTH_SHORT).show();
    }

    // Funções para obter os códigos
    private String getCountryCodeByName(String countryName) {
        for (JSONObject country : countryList) {
            try {
                if (country.getString("country_name").equals(countryName)) {
                    return country.getString("country_code");
                }
            } catch (Exception e) {
                Log.e("OptionsActivity", "Erro ao obter código do país", e);
            }
        }
        return null;
    }

    private String getLanguageCodeByName(String languageName) {
        for (JSONObject language : languageList) {
            try {
                if (language.getString("language_name").equals(languageName)) {
                    return language.getString("language_code");
                }
            } catch (Exception e) {
                Log.e("OptionsActivity", "Erro ao obter código da língua", e);
            }
        }
        return null;
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            Log.e("OptionsActivity", "Erro ao carregar JSON", ex);
            return null;
        }
        return json;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        }
        if(item.getItemId() == R.id.nav_populares){
            Intent intent = new Intent(this, GamePagerActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_favoritos){
            Intent intentFavoritos = new Intent(this, FavoriteNewsActivity.class);
            startActivity(intentFavoritos);
        }
        if(item.getItemId() == R.id.nav_opcoes){
            Intent intentOpcoes = new Intent(this, OptionsActivity.class);
            startActivity(intentOpcoes);
        }
        if (item.getItemId() == R.id.nav_logout) {
            // Realiza o logout do Firebase
            FirebaseAuth.getInstance().signOut();

            Intent intentLogin = new Intent(this, LoginActivity.class);
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

    private void loadPreferences() {
        // Carregar o modo salvo
        String mode = sharedPreferences.getString(MODE_KEY, "day");
        if (mode.equals("day")) {
            rbDay.setChecked(true);
        } else if (mode.equals("night")) {
            rbNight.setChecked(true);
        }

        // Carregar o código de país e idioma salvos
        String savedCountryCode = sharedPreferences.getString(COUNTRY_KEY, "br");
        String savedLanguageCode = sharedPreferences.getString(LANGUAGE_KEY, "pt-br");

        // Obter os nomes correspondentes aos códigos
        String countryName = getCountryNameByCode(savedCountryCode);
        String languageName = getLanguageNameByCode(savedLanguageCode);

        // Definir o nome do país e idioma nos campos AutoCompleteTextView
        if (countryName != null) {
            actvCountry.setText(countryName, false);  // Define o nome do país
        }

        if (languageName != null) {
            actvLanguage.setText(languageName, false);  // Define o nome do idioma
        }
    }

    // Função para obter o nome do país a partir do código
    private String getCountryNameByCode(String countryCode) {
        for (JSONObject country : countryList) {
            try {
                if (country.getString("country_code").equals(countryCode)) {
                    return country.getString("country_name");
                }
            } catch (Exception e) {
                Log.e("OptionsActivity", "Erro ao obter nome do país", e);
            }
        }
        return null;
    }

    // Função para obter o nome do idioma a partir do código
    private String getLanguageNameByCode(String languageCode) {
        for (JSONObject language : languageList) {
            try {
                if (language.getString("language_code").equals(languageCode)) {
                    return language.getString("language_name");
                }
            } catch (Exception e) {
                Log.e("OptionsActivity", "Erro ao obter nome da língua", e);
            }
        }
        return null;
    }

}
