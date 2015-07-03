package com.example.movesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.movesclass.Resultados;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;


public class InstaResult extends ActionBarActivity {

    private Button seguir;
    private TextView esCorrecta;

    //private List<Resultados> listResultados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_result);

        //final Context context = this;

        //seguir = (Button) findViewById(R.id.botonInsta);
        //esCorrecta = (TextView) findViewById(R.id.respuestaInsta);

        //esCorrecta.setText("Holakase");

    }
}
