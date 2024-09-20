package com.example.gamesnews.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.gamesnews.R;
import com.example.gamesnews.authentication.AuthManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class RegistrarActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etRegisterEmail, etRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        authManager = new AuthManager();

        etRegisterEmail = findViewById(R.id.et_register_email);
        etRegisterPassword = findViewById(R.id.et_register_password);
        tilEmail = findViewById(R.id.til_register_email);
        tilPassword = findViewById(R.id.til_register_password);
        MaterialButton btnRegister = findViewById(R.id.btn_register);

        tilEmail.setError("Por favor, insira um e-mail.");
        tilPassword.setError("Por favor, insira uma senha.");

        etRegisterEmail.addTextChangedListener(new TextWatcher() {
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

        etRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
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

        btnRegister.setOnClickListener(v -> {
            if (tilEmail.getError() == null && tilPassword.getError() == null) {

                String email = etRegisterEmail.getText().toString().trim();
                String password = etRegisterPassword.getText().toString();

                if(password.contains(" ")){
                    Toast.makeText(this, "Senha não pode conter espaços", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(this, "Senha deve ter pelo menos 6 caracteres", Toast.LENGTH_LONG).show();
                    return;
                }

                authManager.registerUser(email, password, new AuthManager.OnAuthCompleteListener() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        // Lógica após cadastro bem-sucedido
                        Toast.makeText(RegistrarActivity.this, "Cadastro bem-sucedido", Toast.LENGTH_SHORT).show();
                        Intent intentMain = new Intent(RegistrarActivity.this, MainActivity.class);
                        startActivity(intentMain);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Lógica para falha no cadastro
                        Toast.makeText(RegistrarActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
                        Log.e("RegistrarActivity", "Falha ao registrar usuário: ", e);
                    }
                });
            }
        });
    }
}
