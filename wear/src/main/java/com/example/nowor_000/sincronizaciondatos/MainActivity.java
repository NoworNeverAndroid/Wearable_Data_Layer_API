package com.example.nowor_000.sincronizaciondatos;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends WearableActivity  implements DataApi.DataListener, MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * sincronizacion
     */

    private static final String KEY_CONTADOR="com.example.key.contador";
    private static final String ITEM_CONTADOR="/contador";
    private  int contador;
    /**
     * sincronizacion
     */



    private static final String WEAR_ENVIAR_TEXTO="/enviar_texto";

    private TextView textView;


    /**
     *CLIENTE
     */
       GoogleApiClient apiClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        textView=(TextView) findViewById(R.id.textViewEspera);


         apiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)//nos notifica cuando estamos conectados
            .addOnConnectionFailedListener(this)// ofrece el resultado del error
            .build();


        /**
         * ((SINCRONIZACION)) cuando iniciamos la activity, vamos a obtener el contador del MOBILE y vamos a inicializar el del WEAR con el mismo numero
         */

        PendingResult<DataItemBuffer> resultado= Wearable.DataApi.getDataItems(apiClient);
        resultado.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {

                for (DataItem dataItem : dataItems) {

                    if (dataItem.getUri().getPath().equals(ITEM_CONTADOR)) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);

                        contador = dataMapItem.getDataMap().getInt(KEY_CONTADOR);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.textoContador)).setText(Integer.toString(contador));

                            }
                        });
                    }
                }
                dataItems.release();
            }
        });



    }

    @Override
    public void onConnected(Bundle bundle) {
       Wearable.MessageApi.addListener(apiClient, this);
        Wearable.DataApi.addListener(apiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onDataChanged(DataEventBuffer eventos) {
        for (DataEvent event : eventos) {
            if (event.getType() == DataEvent.TYPE_CHANGED){
                DataItem item =event.getDataItem();
                if(item.getUri().getPath().equals(ITEM_CONTADOR)){
                    DataMap dataMap= DataMapItem.fromDataItem(item).getDataMap();

                    contador= dataMap.getInt(KEY_CONTADOR);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {//(ACTUALIZACION)CADA CLICK EN TEXTVIEW DEL MOBILE VAMOS A ACTUALIZAR EL TEXTVIEW DEL WEAR
                            ((TextView) findViewById(R.id.textoContador)).setText(Integer.toString(contador));
                        }
                    });
                }
            } else if(event.getType()==DataEvent.TYPE_DELETED){//algun item a sido borrado

            }



        }
    }


    @Override
    public void onMessageReceived(final MessageEvent mensaje) {



        if(mensaje.getPath().equalsIgnoreCase(WEAR_ENVIAR_TEXTO)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                      textView.setText(textView.getText()+"\n"+ new String(mensaje.getData())+"\n");
                }
            });
        }
    }









    //<editor-fold desc="CICLO DE VIDA">
    @Override
    protected void onStart(){
        super.onStart();
        apiClient.connect();
    }


    @Override
    protected void onStop(){
       Wearable.MessageApi.removeListener(apiClient, this);
        Wearable.DataApi.removeListener(apiClient,this);

        if(apiClient!=null && apiClient.isConnected()) {
            apiClient.disconnect();
        }

        super.onStop();


    }
    //</editor-fold>



}


