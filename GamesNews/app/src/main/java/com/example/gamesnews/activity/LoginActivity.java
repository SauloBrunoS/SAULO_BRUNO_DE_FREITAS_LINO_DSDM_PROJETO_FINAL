package com.example.gamesnews.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamesnews.R;
import com.example.gamesnews.authentication.AuthManager;
import com.example.gamesnews.broadcast.NetworkChangeReceiver;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextInputEditText etEmail, etPassword;

    private TextInputLayout tilEmail, tilPassword;

    private MaterialButton btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), filter);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.nome_arquivo_preferencias_do_usuario), MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", "day");

        if (mode.equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        authManager = new AuthManager();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);

        tilEmail.setError("Por favor, insira um e-mail.");
        tilPassword.setError("Por favor, insira uma senha.");

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if (email.isEmpty()) {
                    tilEmail.setError("Por favor, insira um e-mail.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tilEmail.setError("E-mail inválido.");
                } else {
                    tilEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();
                if (password.isEmpty()){
                    tilPassword.setError("Por favor, insira uma senha.");
                } else {
                    tilPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnLogin.setOnClickListener(v -> {
            if (tilEmail.getError() == null && tilPassword.getError() == null) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                authManager.loginUser(email, password, new AuthManager.OnAuthCompleteListener() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        // Lógica após login bem-sucedido
                        Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                        Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intentMain);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Lógica para falha no login
                        Toast.makeText(LoginActivity.this, "Usuário e/ou senha inválidos!", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Falha ao logar usuário: ", e);
                    }
                });
            }
        });

        tvRegister.setOnClickListener(v -> {
            // Abrir a tela de cadastro
            startActivity(new Intent(LoginActivity.this, RegistrarActivity.class));
        });
    }
}
