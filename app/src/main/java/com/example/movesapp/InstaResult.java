package com.example.movesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movesclass.Resultados;
import com.example.movesclass.Usuarios;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstaResult extends ActionBarActivity {

	private TextView texto;
	//private ArrayList<String> datos = new ArrayList<String>();
	//private ArrayAdapter<String> adaptador;
	//private List<Resultados> r = new ArrayList<Resultados>();
	//private static int correctos;
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
		setContentView(R.layout.activity_insta_result);
		texto = (TextView) findViewById(R.id.respuestaInsta);
		boton = (Button) findViewById(R.id.botonInsta);

		final Context contexto = this;

		String respuestas = getIntent().getStringExtra("JSON");

		JsonParser parser = new JsonParser();
		Gson gson = new Gson();

		JsonArray jArray = parser.parse(respuestas).getAsJsonArray();

        /*for(JsonElement obj : jArray )
        {
            Resultados res = gson.fromJson(obj , Resultados.class);
            listResultados.add(res);
        }*/

		Resultados ultimo = gson.fromJson(jArray.get(jArray.size()-1),Resultados.class);

		if(ultimo.getContestado().equals(ultimo.getVerdadera()))
			texto.setText("BIEN");
		else
			texto.setText("MAL");


		boton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(contexto,Preguntas.class);
				startActivity(i);
			}
		});
	}
}