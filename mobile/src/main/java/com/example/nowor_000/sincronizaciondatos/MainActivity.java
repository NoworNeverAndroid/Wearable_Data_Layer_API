package com.example.nowor_000.sincronizaciondatos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    /**
     * sincronizacion
     */

    private static final String KEY_CONTADOR="com.example.key.contador";
    private static final String ITEM_CONTADOR="/contador";
    private  int contador;

    /**
     * sincronizacion
     */
    private static final String WEAR_INICIAR_ACTIVIDAD= "/iniciar";
    private static final String WEAR_ENVIAR_TEXTO= "/enviar_texto";


    /**
     * CLIENTE
     */
    private GoogleApiClient apiClient;

    Button btnEnviarTexto;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        /**
         *API CLIENT es clave para establecer la comunicación
         */

        apiClient=new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        addViews();

        /**
         * sincronizacion
         */
        final TextView textoContador=(TextView) findViewById(R.id.textoContador);
        textoContador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador++;
                textoContador.setText(Integer.toString(contador));

                PutDataMapRequest putDataMapReq = PutDataMapRequest.create(ITEM_CONTADOR);
                putDataMapReq.getDataMap().putInt(KEY_CONTADOR, contador);

                PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                PendingResult<DataApi.DataItemResult> resultado = Wearable.DataApi.putDataItem(apiClient, putDataReq);

                /**
                 * ESTA LINEA SE ME OLVIDO EN EL VIDEOTUTORIAL
                 */
                enviarMensaje(ITEM_CONTADOR, Integer.toString(contador));
            }


        });

        /**
         * sincronizacion
         */
    }





    private void addViews() {

        editText=(EditText)findViewById(R.id.edtitText);

        btnEnviarTexto =(Button) findViewById(R.id.btnEnviarTexto);
        btnEnviarTexto.setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnEnviarTexto:

                String text = editText.getText().toString();

                if ( !TextUtils.isEmpty(text) ){
                    enviarMensaje(WEAR_ENVIAR_TEXTO, text);
                    editText.getText().clear();
                    editText.requestFocus();


                }else {
                    enviarMensaje(WEAR_ENVIAR_TEXTO, "\n->Escribe un mensaje, en el EditText<-");
                }
                break;



        }

    }



    //<editor-fold desc="ENVIAR MENSAJE EditText">
    private void enviarMensaje(final String path, final String texto){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodos=Wearable.NodeApi.getConnectedNodes(apiClient).await();

                for (Node nodo: nodos.getNodes()){
                    Wearable.MessageApi.sendMessage(apiClient, nodo.getId(), path, texto.getBytes())
                            .setResultCallback(
                                    new ResultCallback<MessageApi.SendMessageResult>() {
                                        @Override
                                        public void onResult(MessageApi.SendMessageResult resultado) {
                                            if (!resultado.getStatus().isSuccess()) {
                                                Log.e("sincronizacion", "Error al enviar mensaje. Codigo" + resultado.getStatus().getStatusCode());
                                            }
                                        }
                                    }
                            );
                }

                }
        }).start();

    }






    //</editor-fold>







    //<editor-fold desc="CICLO DE VIDA">
    @Override
        protected void onStart(){
            super.onStart();
            apiClient.connect();
        }


    @Override
    protected void onStop(){

        if(apiClient!=null && apiClient.isConnected()) {
            apiClient.disconnect();
        }

        super.onStop();


    }
    //</editor-fold>


    //<editor-fold desc="METODOS API DATA LAYER">
    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //</editor-fold>


    //<editor-fold desc="MENU">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>



}
