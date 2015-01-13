package com.example.movesapp;
import java.util.ArrayList;
import java.util.List;

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
 * Demonstrates app-to-app and browser-app-browser integration with Moves API authorize flow.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint({ "ShowToast", "NewApi" })
public class MainActivity extends ActionBarActivity {


    private static final String CLIENT_ID = "krMl6FcP0kn1Hf00ElX0ai1WIzl65fEf";

    private static final String REDIRECT_URI = "http://www.juegosparanenes.es";

    private static final String CLIENT_SECRET = "7CBa4CNLhEfXqWnykygJ1M0ka7172PEAwwAH_1ulvGg33Zjp_7HlfqfTzNO3RmUL";
    
    private static final int REQUEST_AUTHORIZE = 1;
   
    private static String CODE="";
    
    

    
    private TextView mTextView;

	private int contadorAtras = 0;
    
    public void onBackPressed(){
		contadorAtras ++;
		if(contadorAtras == 1) Toast.makeText(this, "Pulse de nuevo para cerrar la aplicación", Toast.LENGTH_LONG).show();
		if(contadorAtras == 2){
			contadorAtras =0;
			Intent salida=new Intent( Intent.ACTION_MAIN); 
			System.exit(0);
		}
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Usuarios user = new Usuarios(this);
        //user.borrar();
      //  user.dropUsuarios();
        
        if(user.checkCreada() == true){
        	Intent bbdd = new Intent(this, NuevoUsuario.class);
        	startActivity(bbdd);
        }else{
            
        
        
	        findViewById(R.id.authorizeInApp).setOnClickListener(new View.OnClickListener() {
	
	            public void onClick(View v) {
	                doRequestAuthInApp();
	            }
	        });
	
	        mTextView = (TextView) findViewById(R.id.textView);
        }
    }
    public void setCode(String code){
    	
    	MainActivity.CODE = code;
    }
    /**
     * App-to-app. Creates an intent with data uri starting moves://app/authorize/xxx (for more
     * details, see documentation link below) to be handled by Moves app. When Moves receives this
     * Intent it opens up a dialog asking for user to accept the requested permission for your app.
     * The result of this user interaction is delivered to 
     * {@link #onActivityResult(int, int, android.content.Intent) }
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
     * Handle the result from Moves authorization flow. The result is delivered as an uri documented
     * on the developer docs (see link below).
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
                       startActivity(i);
                   }catch(Exception e){
                       Toast.makeText(this, "Ocurrio un error: " + e.toString() , Toast.LENGTH_LONG).show();
                       mTextView.setText(e.toString());
                   }
                	
                	
                }
            
        }

    }

    /**
     * Helper method for building a valid Moves authorize uri.
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
