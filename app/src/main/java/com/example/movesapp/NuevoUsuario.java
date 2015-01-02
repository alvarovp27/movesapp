package com.example.movesapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.movesclass.Usuarios;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.R.*;

public class NuevoUsuario extends ActionBarActivity {
	private String email,pass;
	private EditText primero,segundo;
	Context contexto = this;
	Button boton;
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
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.usuario);
	        primero = (EditText) findViewById(R.id.email);
	        segundo = (EditText) findViewById(R.id.password);
	        boton = (Button) findViewById(R.id.botonUser);
	        boton.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View arg0) {	
	        		email = primero.getText().toString();
	        		pass = segundo.getText().toString();
	        		if(email != "Email"){
	        			HttpClient httpclient = new DefaultHttpClient();
	        			HttpPost httppost = new HttpPost("http://www.juegosparanenes.es/movesapp/class/NuevoUsuario.php");

	        			try {
	        			    // Add your data
	        			    List<NameValuePair> par = new ArrayList<NameValuePair>();
	        			    par.add(new BasicNameValuePair("email", email));
	        			    par.add(new BasicNameValuePair("pass", pass));
	        			    httppost.setEntity(new UrlEncodedFormEntity(par));

	        			    // Execute HTTP Post Request
	        			    HttpResponse response = httpclient.execute(httppost);
	        			} catch (ClientProtocolException e) {
	        			    // TODO Auto-generated catch block
	        			} catch (IOException e) {
	        			    // TODO Auto-generated catch block
	        			}
	        			Usuarios u = new Usuarios(contexto);
	        			u.insertaUsuario(email, pass);
	        			//Vuelve a la actividad principal
	        			Intent i = new Intent(contexto,MainActivity.class);
	        			startActivity(i);
	        		}
	        	}
	        
	        });
	 }
}	
