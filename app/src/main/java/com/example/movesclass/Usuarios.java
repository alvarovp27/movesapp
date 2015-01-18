package com.example.movesclass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
 
public class Usuarios extends SQLiteOpenHelper {
 
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE IF NOT EXISTS Users (email TEXT, pass TEXT)";
    String sqlData = "CREATE TABLE IF NOT EXISTS Datos (result TEXT)";
 
    public Usuarios(Context contexto) {
        super(contexto,"Usuarios",null, 1);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        db.execSQL(sqlCreate);
        db.execSQL(sqlData);
    }
    
    public Boolean checkCreada(){
    	SQLiteDatabase db = getReadableDatabase();
    	Boolean salida = false;
        Cursor cursor = db.rawQuery("SELECT * FROM Users", null);
        if(cursor.getCount() == 0){
        	salida = true;
        }
        cursor.close();
        db.close();
        return salida;    	
    }
    public Boolean checkDatos(){
        SQLiteDatabase db = getReadableDatabase();
        Boolean salida = false;
        Cursor cursor = db.rawQuery("SELECT * FROM Datos", null);
        if(cursor.getCount() == 0){
            salida = true;
        }
        Log.e("CheckDatos",Integer.toString(cursor.getCount()));
        cursor.close();
        db.close();
        return salida;
    }
    
    public void insertaUsuario(String email, String pass){
    	SQLiteDatabase db = getWritableDatabase();
    	db.execSQL("INSERT INTO Users VALUES ('"+
                           email+"', '"+pass+"')");
        db.close();
    }

    /*public void dropUsuarios(){
    	SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE * FROM Usuarios");
    	db.execSQL("TRUNCATE Usuarios");
        db.close();
    }*/
    
    public void insertaDatos(String json){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Datos", null);
        Log.e("Contador Cursor",Integer.toString(cursor.getCount()));
        if(cursor.getCount() > 0){
            db.execSQL("DELETE FROM Datos");
        }
        Log.e("InsertaDatos","Me dispongo a actualizar");

        db.execSQL("INSERT INTO Datos VALUES ('" + json + "')");
    }

    public String obtieneDatos() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT result FROM Datos", null);
        String json ="";
        while (cursor.moveToNext()) {
            json = cursor.getString(0);
        }
        Log.e("ObtieneDatos", json);
        return json;
    }
    
    public String obtieneUsuario(){
    	SQLiteDatabase db = getReadableDatabase();
    	String user = null;
    	Cursor cursor = db.rawQuery("SELECT * FROM Users", null);
    	 while (cursor.moveToNext()){
    		 user = cursor.getString(0);
    	 }
    	return user;
    }
    
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       //Se elimina la versi�n anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        db.execSQL("DROP TABLE IF EXISTS Datos");
        //Se crea la nueva versi�n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlData);
		
	}
}