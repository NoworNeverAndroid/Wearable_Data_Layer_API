package com.example.nowor_000.sincronizaciondatos;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by nowor_000 on 26/11/2015.
 */
public class ServiceListener  extends WearableListenerService {

    private static final String WEAR_INICIAR_ACTIVIDAD="/iniciar";



    @Override
    public void onMessageReceived(MessageEvent messageEvent){
      //  super.onMessageReceived(messageEvent);


        if (messageEvent.getPath().equals(WEAR_INICIAR_ACTIVIDAD)) {
            final String message = new String(messageEvent.getData());


                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);



        }else {
            super.onMessageReceived(messageEvent);
        }
    }
}
