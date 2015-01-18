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

import com.example.movesclass.Places;
import com.example.movesclass.Resultados;
import com.example.movesclass.Usuarios;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Results extends ActionBarActivity {
	private String jsonPreguntas;
	private TextView texto;
	private ListView lista;
	private ArrayList<String> datos = new ArrayList<String>();
	private ArrayAdapter<String> adaptador;
	private List<Resultados> r = new ArrayList<Resultados>();
	private static int correctos;
	private Button boton;
	Context contexto = this;
	private int contadorAtras = 0;
	@Override
	public void onBackPressed(){
		contadorAtras ++;
		if(contadorAtras == 1) Toast.makeText(this, "Pulse de nuevo para cerrar la aplicación", Toast.LENGTH_LONG).show();
		if(contadorAtras == 2){
			contadorAtras =0;
            finish();
		}
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		texto = (TextView) findViewById(R.id.Resultados);
		lista = (ListView) findViewById(R.id.listView1);
		boton = (Button) findViewById(R.id.botonIntento);
		boton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(contexto, Preguntas.class);
                i.putExtra("update",false);
				startActivity(i);
				
			}
		});
		jsonPreguntas = getIntent().getStringExtra("JSON");
		Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(jsonPreguntas).getAsJsonArray();
        for(JsonElement obj : jArray )
        {
            Resultados cse = gson.fromJson( obj , Resultados.class);
            r.add(cse);
        }
        
        int pregunta = 1;
        
        for(Resultados ind : r){
        	String elemento = "\nPregunta "+pregunta+"\n" +
        			"Correcta: "+ind.getVerdadera()+"\n" +
        			"Contestada: "+ind.getContestado()+"\n";
        	datos.add(elemento);
        	
        	Boolean esCorrecto = false;
        	
        	if(ind.getContestado().equals(ind.getVerdadera())) {
        		correctos++;
        		esCorrecto = true;
        	}
        	pregunta++;
        }
		texto.setText("Has contestado correctamente "+correctos+" de 10");
		adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datos);
		lista.setAdapter(adaptador);
		
		
		
		//Se mandan los datos al servidor
		String resultados = "";
		for(String recorre : datos ){
			resultados += recorre;	
		}
		Usuarios user = new Usuarios(this);
		String usuario = user.obtieneUsuario();
		Log.e("adfafd",usuario);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.juegosparanenes.es/movesapp/class/AddFila.php");

		try {
		    // Add your data
		    List<NameValuePair> par = new ArrayList<NameValuePair>();
		    par.add(new BasicNameValuePair("email", usuario));
		    par.add(new BasicNameValuePair("res", resultados));
		    httppost.setEntity(new UrlEncodedFormEntity(par));

		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
	}
}
