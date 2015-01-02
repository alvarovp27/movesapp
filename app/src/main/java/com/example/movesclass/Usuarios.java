package com.example.movesclass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
 
public class Usuarios extends SQLiteOpenHelper {
 
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE IF NOT EXISTS Users (email TEXT, pass TEXT)";
 
    public Usuarios(Context contexto) {
        super(contexto,"Usuarios",null, 1);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        db.execSQL(sqlCreate);
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
    
    public void insertaUsuario(String email, String pass){
    	SQLiteDatabase db = getWritableDatabase();
    	db.execSQL("INSERT INTO Users VALUES ('"+
                           email+"', '"+pass+"')");
        db.close();
    }
    /*
    public void dropUsuarios(){
    	SQLiteDatabase db = getWritableDatabase();
    	db.execSQL("TRUNCATE Usuarios");
    	db.close();
    }*/
    
    public void borrar(){
    	SQLiteDatabase db = getWritableDatabase();
    	db.execSQL("DELETE FROM Users WHERE email='manueldavid123@gmail.com'");
    	db.close();
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
	       //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
 
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
		
	}
}