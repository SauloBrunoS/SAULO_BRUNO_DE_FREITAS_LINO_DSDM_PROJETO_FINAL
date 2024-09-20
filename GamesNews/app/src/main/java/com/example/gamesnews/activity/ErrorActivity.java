package com.example.gamesnews.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gamesnews.R;
import com.example.gamesnews.broadcast.NetworkChangeReceiver;
import com.example.gamesnews.utils.NetworkUtils;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        findViewById(R.id.btn_retry).setOnClickListener(v -> {
            // Tentar novamente verificando a conexão
            if (NetworkUtils.isNetworkAvailable(this)) {
                // Se a conexão estiver disponível, feche a tela de erro
                finish();
                NetworkChangeReceiver.resetErrorActivityFlag();
            } else {
                // Se ainda não há conexão, você pode mostrar uma mensagem de erro
                Toast.makeText(this, "Ainda sem conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
