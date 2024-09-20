package com.example.gamesnews.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.gamesnews.activity.ErrorActivity;
import com.example.gamesnews.utils.NetworkUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean isErrorActivityVisible = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!NetworkUtils.isNetworkAvailable(context) && !isErrorActivityVisible) {
            // Mostra a tela de erro apenas se ela ainda não estiver visível
            Intent errorIntent = new Intent(context, ErrorActivity.class);
            errorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(errorIntent);
            isErrorActivityVisible = true;
        }
    }

    public static void resetErrorActivityFlag() {
        isErrorActivityVisible = false;
    }
}

