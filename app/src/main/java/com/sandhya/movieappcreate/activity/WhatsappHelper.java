package com.sandhya.movieappcreate.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class WhatsappHelper extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null && data.getPath().startsWith("/MOVIE APP CREATE")){
                // Handle link redirection

            }
        }
    }
}