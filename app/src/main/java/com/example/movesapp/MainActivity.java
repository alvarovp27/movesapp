package com.example.movesapp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.example.movesclass.Usuarios;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.R.*;
/**
 * Integración de la API oficial de Moves
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint({ "ShowToast", "NewApi" })
public class MainActivity extends ActionBarActivity {


    private static final String CLIENT_ID = "krMl6FcP0kn1Hf00ElX0ai1WIzl65fEf";

    private static final String REDIRECT_URI = "http://www.juegosparanenes.es";

    private static final String CLIENT_SECRET = "7CBa4CNLhEfXqWnykygJ1M0ka7172PEAwwAH_1ulvGg33Zjp_7HlfqfTzNO3RmUL";
    
    private static final int REQUEST_AUTHORIZE = 1;
   
    private static String CODE="";

    private Context contexto = this;
    

    
    private TextView mTextView;

	private int contadorAtras = 0;
    
    public void onBackPressed(){
		contadorAtras ++;
		if(contadorAtras == 1) Toast.makeText(this, "Pulse de nuevo para cerrar la aplicación", Toast.LENGTH_LONG).show();
		if(contadorAtras == 2){
			contadorAtras =0;
            finish();

        }
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Usado para poder enviar datos al servidor
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        final Usuarios user = new Usuarios(this);

        //Comprobar si un usuario está registrado ya, y no lo está, se llama a crear un nuevo usuario
        if(user.checkCreada() == true){
        	Intent bbdd = new Intent(this, NuevoUsuario.class);
        	startActivity(bbdd);
        }else{
            
        
        
	        findViewById(R.id.continueWithoutRefresh).setOnClickListener(new View.OnClickListener() {
	
	            public void onClick(View v) {
	                doRequestAuthInApp();
	            }
	        });
            findViewById(R.id.authorizeInApp).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Intent comenzar = new Intent(contexto, Preguntas.class);

                    if(user.checkDatos() == true){
                        comenzar.putExtra("update",true);
                        doRequestAuthInApp();
                    }else {
                        comenzar.putExtra("update",false);
                        startActivity(comenzar);
                    }
                }

            });
	
	        mTextView = (TextView) findViewById(R.id.textView);
        }
    }
    public void setCode(String code){
    	
    	MainActivity.CODE = code;
    }
    /**
     * Funcion que realizar la llamada a la aplicación de Moves
     *
     */
    public void doRequestAuthInApp() {

        Uri uri = createAuthUri("moves", "app", "/authorize").build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivityForResult(intent, REQUEST_AUTHORIZE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Moves app not installed", Toast.LENGTH_SHORT).show();
        }

    } 


    /**
     * Función posterior a la llamada de la API cuando se llama a la aplicación
     *
     */
    @SuppressLint("NewApi")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    	StrictMode.setThreadPolicy(policy); 
        switch (requestCode) {
            case REQUEST_AUTHORIZE:
                Uri resultUri = data.getData();
                setCode(resultUri.getQueryParameter("code"));
                Toast.makeText(this,
                        (resultCode == RESULT_OK ? "Acceso Autorizado: " : "Failed: ")
                        + "", Toast.LENGTH_LONG).show();
               if(resultCode == RESULT_OK){ 
            	   HttpClient cliente = new DefaultHttpClient();
                   HttpPost peticion = new HttpPost("https://api.moves-app.com/oauth/v1/access_token");
                   try{
                       List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                       parametros.add(new BasicNameValuePair("grant_type", "authorization_code"));
                       parametros.add(new BasicNameValuePair("code", CODE));
                       parametros.add(new BasicNameValuePair("client_id", CLIENT_ID));
                       parametros.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
                       parametros.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
                       peticion.setEntity(new UrlEncodedFormEntity(parametros));
                       HttpResponse respuesta = cliente.execute(peticion);
                       String str_respuesta = EntityUtils.toString(respuesta.getEntity());
                       JSONObject json = new JSONObject(str_respuesta);
                       
                       
                       //Llamamos a la nueva vista Preguntas
                       Intent i = new Intent(this,Preguntas.class);
                       i.putExtra("accessToken", json.getString("access_token"));
                       i.putExtra("expires_in", json.getString("expires_in"));
                       i.putExtra("update",true);
                       startActivity(i);
                   }catch(Exception e){
                       Toast.makeText(this, "Ocurrio un error: " + e.toString() , Toast.LENGTH_LONG).show();
                       mTextView.setText(e.toString());
                   }
                	
                	
                }
            
        }

    }

    /**
     * Funcion para crear la URI para la API
     */
    private Uri.Builder createAuthUri(String scheme, String authority, String path) {
        return new Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(path)
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", getSelectedScopes())
                .appendQueryParameter("state", String.valueOf(SystemClock.uptimeMillis()));
    }

    /**
     * Función para obtener el SCOPE de la llamada a la API
     */
    private String getSelectedScopes() {
        StringBuilder sb = new StringBuilder();
        sb.append("activity");
        sb.append(" location");
        return sb.toString().trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
