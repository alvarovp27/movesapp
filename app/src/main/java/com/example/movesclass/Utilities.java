package com.example.movesclass;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Manuel on 15/1/15.
 */
public class Utilities {

    public static int generaIntAleatorio(int limSup){
        Random r = new Random();
        return r.nextInt(limSup+1);
    }

    public static double generaDoubleAleatorio(){
        int num = generaIntAleatorio(101);
        double doub = (double) num;
        return doub/100.0;
    }

    public int restaFecha(int fecha){
        if(fecha%100 == 1) {
            fecha = fecha - 89;
        }else{
            fecha--;
        }
        return fecha;
    }
    public String convierteMes(String mes){
        String res = "";
        switch (mes){
            case "0":
                res = "01";
                break;
            case "1":
                res = "02";
                break;
            case "2":
                res = "03";
                break;
            case "3":
                res = "04";
                break;
            case "4":
                res = "05";
                break;
            case "5":
                res = "06";
                break;
            case "6":
                res = "07";
                break;
            case "7":
                res = "08";
                break;
            case "8":
                res = "09";
                break;
            case "9":
                res = "10";
                break;
            case "10":
                res = "11";
                break;
            case "11":
                res = "12";
                break;

        }
        return res;
    }
    public String getDiaSemana(Date d){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        String dia = "";
        switch(cal.get(Calendar.DAY_OF_WEEK)){
            case 1:
                dia = "(Domingo)";
                break;
            case 2:
                dia = "(Lunes)";
                break;
            case 3:
                dia = "(Martes)";
                break;
            case 4:
                dia = "(Miercoles)";
                break;
            case 5:
                dia = "(Jueves)";
                break;
            case 6:
                dia = "(Viernes)";
                break;
            case 7:
                dia = "(Sabado)";
                break;

        }
        return dia;
    }
}
