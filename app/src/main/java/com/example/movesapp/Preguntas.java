package com.example.movesapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movesclass.Places;
import com.example.movesclass.Resultados;
import com.example.movesclass.Segments;
import com.example.movesclass.Usuarios;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import android.support.v7.app.ActionBarActivity;

import static java.util.Random.*;

public class Preguntas extends ActionBarActivity {

	private final double SEMANAS_EN_MILISEG = 1.65343915*Math.pow(10,-9);
	private Iterable<Segments> haceMenosDeUnMes;

	private TextView pregunta;
	private TextView tituloPreguntas;
	private RadioButton r1,r2,r3;
	private Button button;
	private String access_token;
	private String expires_in;
	private static String str_respuesta;
    public static int date ;
	private static Date fecha;
	private static Date inicio;
	private static Date fin;
    private static Calendar comienzoActual;
	private static int contador = 1;
	private int contadorAtras = 0;
	private static int aleat=0,aseg=0,af1=0,af2=0,asegf1=0,asegf2=0,salir=0;
	private static String verdadero = null,falso1 = null,falso2 = null;
	private static List<Resultados> lista = new ArrayList<Resultados>();
	private static ArrayList<Places> segments = new ArrayList<Places>();
    private Boolean actualizar;

	private static List<String> enunciados = new ArrayList<>();
	private static List<Segments> preguntasAMostrar = new ArrayList<>();
	private static int punteroAPregunta=0;

	private static List<Segments> sextoMes = new ArrayList<>();
	private static List<Segments> quintoMes = new ArrayList<>();
	private static List<Segments> cuartoMes = new ArrayList<>();
	private static List<Segments> terceroMes = new ArrayList<>();
	private static List<Segments> segundoMes = new ArrayList<>();
	private static List<Segments> primerMes = new ArrayList<>();

	private ProgressDialog pdialog= null;

	private CargaPreguntasAsync cpa= new CargaPreguntasAsync();

	@Override
	public void onBackPressed(){
		contadorAtras++;
		if(contadorAtras == 1) Toast.makeText(this, "Pulse de nuevo para cerrar la aplicación", Toast.LENGTH_LONG).show();
		if(contadorAtras == 2){
			contadorAtras =0;
			Intent salida=new Intent( Intent.ACTION_MAIN); 
			finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preguntas);
		final Context contexto = this;
		access_token = getIntent().getStringExtra("accessToken");
		expires_in = getIntent().getStringExtra("expires_in");
        actualizar = getIntent().getBooleanExtra("update",false);
		
		pregunta = (TextView) findViewById(R.id.pregunta);
		tituloPreguntas = (TextView) findViewById(R.id.tituloPreguntas);
		r1 = (RadioButton) findViewById(R.id.radio0);
		r2 = (RadioButton) findViewById(R.id.radio1);
		r3 = (RadioButton) findViewById(R.id.radio2);
		button = (Button) findViewById(R.id.siguiente);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				contador++;
				if(contador > 10){
					compruebaChecked();
					contador = 1;

					punteroAPregunta=0;
					preguntasAMostrar=new ArrayList<Segments>();

					sextoMes = new ArrayList<>();
					quintoMes = new ArrayList<>();
					cuartoMes = new ArrayList<>();
					terceroMes = new ArrayList<>();
					segundoMes = new ArrayList<>();
					primerMes = new ArrayList<>();

					enunciados = new ArrayList<String>();

					String terminado = "";
					terminado = new Gson().toJson(lista);
					lista.clear();
					Intent i = new Intent(contexto, Results.class);
					i.putExtra("JSON", terminado);
                    i.putExtra("Datos", str_respuesta);
					startActivity(i);
				}else{
				
					//try {
					compruebaChecked();
					String respHastaElMomento=new Gson().toJson(lista);

					Intent i = new Intent(contexto, InstaResult.class);
					i.putExtra("JSON", respHastaElMomento);
					//i.putExtra("Datos", str_respuesta);
					//i.putExtra("JSON","hola");
					startActivity(i);

						//ejecutar(str_respuesta);
					/*} catch (IOException e) {
						e.printStackTrace();
					}*/
				}
			}
            });
		
        Usuarios u = new Usuarios(this);
        //Los datos se guardan en la Base de Datos. Si existen datos en la base de datos, se obtienen, sino...
        if(!u.checkDatos()){
            str_respuesta = u.obtieneDatos();
            Log.i("Datos","He entrado en CheckDatos");
        }else if(u.checkDatos() || actualizar == true){             //... Si hay datos y el botón de actualizar ha sido pulsado, entonces los datos se obtienen de la API
            HttpClient client = new DefaultHttpClient();
            comienzoActual = new GregorianCalendar();
            String dateAux1 = Integer.toString(comienzoActual.get(Calendar.YEAR));
            String dateAux2 = Integer.toString(comienzoActual.get(Calendar.MONTH));
            String mesConvertido = convierteMes(dateAux2);
            String dateString = dateAux1+mesConvertido;

            //Obtenemos la fecha Actual
            date = Integer.parseInt(dateString);

            Boolean cent = true;
            int cont = 0;

            //Se obtienen los datos de los ultimos 6 meses
            do {
                HttpGet request = new HttpGet("https://api.moves-app.com/api/1.1/user/places/daily/" + date + "?access_token=" + access_token);
                HttpResponse respuesta;
                String recorre = "";
                try {
                    respuesta = client.execute(request);
                    recorre = EntityUtils.toString(respuesta.getEntity());

                    //  str_respuesta = str_respuesta.substring(0, str_respuesta.length()-1)+",";
                    //   String cadenaNueva2 = r.substring(1, str_respuesta2.length());
                    //  str_respuesta = "";
                    //   str_respuesta = cadenaNueva + cadenaNueva2;
                    //  Log.d("JSON",cadenaNueva);
                    //  Log.d("JSON2",cadenaNueva2);
                } catch (Exception e) {
                    Toast.makeText(this, "Ocurrio un error llamando a la API: " + e.toString(), Toast.LENGTH_LONG).show();
                    //pregunta.setText(e.toString());
                }

                String error = recorre.substring(2, 7);
                Log.d("Cadena error", error);
                if (error.equals("error") || recorre == "" || cont == 5) { //Solamente cuenta 5 meses
                    cent = false;
                } else {
                    if (str_respuesta == "" || str_respuesta == null) {
                        str_respuesta = recorre;
                    } else {
                        str_respuesta = str_respuesta.substring(0, str_respuesta.length() - 1) + ",";
                        String cadenaNueva2 = recorre.substring(1, recorre.length());
                        str_respuesta += cadenaNueva2;
                    }
                    Log.e("text",str_respuesta);
                    int auxfecha = restaFecha(date);
                    date = auxfecha;
                    Log.d("Fecha", Integer.toString(date));
                    cont++;
                }

            } while (cent);

            String usuario = u.obtieneUsuario();

            u.insertaDatos(str_respuesta);
        }
        Log.e("Datos",str_respuesta.toString());
		try {
			ejecutar(str_respuesta);

		} catch (IOException e) {
			
			e.printStackTrace();
		}
        

		
	}

    /**
     * Funcion que proporciona las preguntas y las respuestas para que sean contestadas.
     * */
	@SuppressLint("ShowToast")
	public void ejecutar(String respuesta) throws IOException{
		tituloPreguntas.setText("Pregunta " + contador + " de 10");
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(respuesta).getAsJsonArray();

        

        for(JsonElement obj : jArray )
        {
            Places cse = gson.fromJson( obj , Places.class);
            segments.add(cse);
        }
        SimpleDateFormat ffecha =  new SimpleDateFormat("dd 'de' MMMM", new Locale("es_ES"));
		SimpleDateFormat fHora =  new SimpleDateFormat("HH:mm", new Locale("es_ES"));

		
		//Obtenemos los lugares

		if(preguntasAMostrar.isEmpty()){

			pdialog = new ProgressDialog(Preguntas.this);
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdialog.setMessage("Procesando...");
			pdialog.setCancelable(true);
			pdialog.setMax(100);

            cpa = new CargaPreguntasAsync();

			try {
				preguntasAMostrar=cpa.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}


			System.out.println("Acabo de cargar las preguntas");
        }else{
            System.out.println("¡Paso de cargar preguntas!");
        }




		Segments actual = preguntasAMostrar.get(punteroAPregunta);
        punteroAPregunta++;

		Date fechaInicio = new Date();
		Date fechaFin = new Date();

		try {
			fechaInicio = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ",Locale.FRANCE).parse(actual.getStartTime());
			fechaFin = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ",Locale.FRANCE).parse(actual.getEndTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar inicio = new GregorianCalendar();
		inicio.setTime(fechaInicio);

		Calendar fin = new GregorianCalendar();
		fin.setTime(fechaFin);

		//Comprobar las dos fechas
		int diaInicio = inicio.get(Calendar.DAY_OF_YEAR);
		int diaFin = fin.get(Calendar.DAY_OF_YEAR);
		boolean fechaFinDiaSiguiente = false;
		boolean fechaFinOtroDia = false;
		if(diaFin-diaInicio==1){fechaFinDiaSiguiente=true;}
		if(diaFin-diaInicio>1){fechaFinOtroDia=true;}

		Calendar hoy = Calendar.getInstance();
		int diaHoy = hoy.get(Calendar.DAY_OF_YEAR);
		boolean empiezaHoy = false;
		boolean empezoAyer = false;
		boolean empezoAnteAyer = false;
		if(diaHoy-diaInicio == 0){empiezaHoy=true;}
		if(diaHoy-diaInicio == 1){empezoAyer=true;}
		if(diaHoy-diaInicio == 2){empezoAnteAyer=true;}


		//Tratar cada parámetro por separado y convertirlos a cadena
		//Añadir un método que calcule el nombre del día de la semana y mes
		String diaSemInicio = getDiaSemana(inicio);
		String diaSemFin = getDiaSemana(fin);
		String mesInicio = getMes(inicio);
		String mesFin = getMes(fin);
		String horaInicio = getHora(inicio);
		String horaFin = getHora(fin);


		//Comprobar si la fecha fin es en un día distinto de la fecha inicio. En tal caso,
		//indicar también el día fin

		//Tener el detalle de poner ¿Dónde estuvo ayer...? si la fecha se corresponde a la de
		//ayer, e ídem con el día de hoy

		String preg = "";

		if(empiezaHoy)
			preg+="¿Dónde ha estado hoy desde las "+horaInicio+" hasta las "+horaFin+" ";
		else if(empezoAyer)
			preg+="¿Dónde estuvo ayer desde las "+horaInicio+" hasta las "+horaFin+" ";
		else if (empezoAnteAyer)
			preg+="¿Dónde estuvo antes de ayer desde las "+horaInicio+" hasta las "+horaFin+" ";
		else
			preg +="¿Dónde estuvo el "+diaSemInicio+", "+inicio.get(Calendar.DAY_OF_MONTH)+
					" de "+mesInicio+" desde las "+horaInicio+" hasta las "+horaFin+" ";

		if(fechaFinDiaSiguiente)
			preg+="del día siguiente?";
		else if (fechaFinOtroDia)
			preg+="del "+diaSemFin+", "+fin.get(Calendar.DAY_OF_MONTH)+
					" de "+mesFin+"?";
		else
			preg+="?";



		/*pregunta.setText("¿Dónde estuvo el día "+inicio.get(Calendar.DAY_OF_MONTH)+" de "+(inicio.get(Calendar.MONTH)+1)+
				" desde las "+inicio.get(Calendar.HOUR_OF_DAY)+":"+inicio.get(Calendar.MINUTE)
				+" hasta las "+fin.get(Calendar.HOUR_OF_DAY)+":"+fin.get(Calendar.MINUTE)+"?");*/
		pregunta.setText(preg);
		enunciados.add(preg);
		verdadero=actual.getPlace().getName();

        System.out.println("Voy a obtener los falsos:");
		List<String> falsos = dameFalsos(actual);
        System.out.println("Terminé de obtener los falsos!!");

		falso1=falsos.get(0);
		falso2=falsos.get(1);

		int preguntas = (int)Math.round(Math.random()*3);
		int verdadera = 0;
        //Se pone la pregunta verdadera en un lugar diferente
		switch(preguntas){
            case 0:
                r1.setText(verdadero);
                r2.setText(falso1);
                r3.setText(falso2);
                verdadera = 1;
                break;
            case 1:
                r1.setText(falso1);
                r2.setText(verdadero);
                r3.setText(falso2);
                verdadera = 2;
                break;
            case 2:
                r1.setText(falso2);
                r2.setText(falso1);
                r3.setText(verdadero);
                verdadera = 3;
                break;
            case 3:
                r1.setText(verdadero);
                r2.setText(falso1);
                r3.setText(falso2);
                verdadera = 1;
                break;
		}

		
	}

	private void compruebaChecked(){
		// Se inserta en una lista la pregunta con la respuesta contestada
		if(r1.isChecked()){
			Resultados res = new Resultados(r1.getText().toString(),verdadero);
			lista.add(res);
		}
		if(r2.isChecked()){
			Resultados res = new Resultados(r2.getText().toString(),verdadero);
			lista.add(res);
		}
		if(r3.isChecked()){
			Resultados res = new Resultados(r3.getText().toString(),verdadero);
			lista.add(res);
		}
	}

	public int aleatorio(int tipo,ArrayList<Places> segmentos,int seleccionado, int anterior){
		int i = 0;
		switch(tipo){
		case 0: //aleatorio principal
		
				i = (int)Math.round(Math.random()*(segmentos.size()-1));
				if(i < 0) i = 0;
		        if(i>=segmentos.size()) i = segmentos.size()-1;
			break;
			
		case 1: //fechas de la pregunta
				if(segmentos.get(seleccionado).getSegments() != null){
					i = (int)Math.round(Math.random()*(segmentos.get(seleccionado).getSegments().size()-1));
					if(i < 0) i = 0;
			        if(i>segmentos.get(seleccionado).getSegments().size())
                        i = segmentos.get(seleccionado).getSegments().size()-1;
				}
			break;
		
		case 2: //segmento aleatorio de falso 1
	
				i = (int)Math.round(Math.random()*(segmentos.size()-1));
				if(i < 0) i = 0;
		
			break;
			
		case 3: //fecha para pregunta falsa
			
				i = (int)Math.round(Math.random()*(segmentos.get(seleccionado).getSegments().size()-1));
				if(i < 0) i = 0;
			
			break;
		}
		
		return i;
	}

	public int aleatorio2(int limSup){
		Random r = new Random();
		return r.nextInt(limSup+1);
	}

	public List<Segments> cargaPreguntas(){
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		List<Segments> res = new ArrayList<>();
		Iterable<List<Segments>> aux = Iterables.transform(segments, new Function<Places, List<Segments>>() {
			@Override
			public List<Segments> apply(Places places) {
				return places.getSegments();
			}
		});

		//List<Segments> segments2 = new ArrayList<>();
		for(List<Segments> ls : aux){
			try{
				for(Segments s:ls){
					if(s.getPlace().getName()!=null){
						Calendar seisMeses = Calendar.getInstance();
						Calendar cincoMeses = Calendar.getInstance();
						Calendar cuatroMeses = Calendar.getInstance();
						Calendar tresMeses = Calendar.getInstance();
						Calendar dosMeses = Calendar.getInstance();
						Calendar unMes = Calendar.getInstance();
						Calendar hoy = Calendar.getInstance();

						seisMeses.add(Calendar.MONTH,-6);
						cincoMeses.add(Calendar.MONTH,-5);
						cuatroMeses.add(Calendar.MONTH,-4);
						tresMeses.add(Calendar.MONTH,-3);
						dosMeses.add(Calendar.MONTH,-2);
						unMes.add(Calendar.MONTH,-1);

						Date st = new Date();

						try {
							st = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ",Locale.FRANCE).parse(s.getStartTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Calendar startTime = new GregorianCalendar();
						startTime.setTime(st);

						if(startTime.after(seisMeses) && startTime.before(cincoMeses))
							sextoMes.add(s);
						else if(startTime.after(cincoMeses) && startTime.before(cuatroMeses))
							quintoMes.add(s);
						else if(startTime.after(cuatroMeses) && startTime.before(tresMeses))
							cuartoMes.add(s);
						else if(startTime.after(tresMeses) && startTime.before(dosMeses))
							terceroMes.add(s);
						else if(startTime.after(dosMeses) && startTime.before(unMes))
							segundoMes.add(s);
						else if(startTime.after(unMes) && startTime.before(hoy))
							primerMes.add(s);
					}
				}
			} catch(Exception e){
				e.getStackTrace();
			}
		}

		for(int i = 0; i<10; i++){



			cpa.onProgressUpdate((i+1)*100);


			Segments actual = null;
			do{
				System.out.println("Estoy iterando");

				int mes = mesAElegir();
				System.out.println("Mes elejido "+mes);
				int random;

				try{
					switch(mes){
						case 1:
							random = aleatorio2(primerMes.size());
							actual = primerMes.get(random);
							System.out.println("Primer mes ******************* ");
							break;
						case 2:
							random = aleatorio2(segundoMes.size());
							actual = segundoMes.get(random);
							System.out.println("Sgdo mes ******************* ");
							break;
						case 3:
							random = aleatorio2(terceroMes.size());
							actual = terceroMes.get(random);
							System.out.println("Tercer mes ******************* ");
							break;
						case 4:
							random = aleatorio2(cuartoMes.size());
							actual = cuartoMes.get(random);
							break;
						case 5:
							random = aleatorio2(quintoMes.size());
							actual = quintoMes.get(random);
							break;
						case 6:
							random = aleatorio2(sextoMes.size());
							actual = sextoMes.get(random);
							break;
						default:
							random = aleatorio2(primerMes.size());
							actual = primerMes.get(random);
							break;
					}
				}catch(Exception e) {
					e.getStackTrace();
				}
			}while(actual == null || res.contains(actual));

			res.add(actual);
		}

		//System.out.println("Tamaño de la lista de segmentos: ******** "+segments2.size());

		//res = new ArrayList<>();
		//Map<String,Integer> contadorApariciones = new HashMap<>();

		Multiset<String> contadorApariciones = HashMultiset.create();


		//Ordeno la lista de preguntas a mostrar de más recientes a más lejanas en el tiempo

		Collections.sort(res, new Comparator<Segments>() {
			@Override
			public int compare(Segments s1, Segments s2) {
				Date fechaS1 = null;
				Date fechaS2 = null;
				try {
					fechaS1 = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.FRANCE).parse(s1.getStartTime());
					fechaS2 = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.FRANCE).parse(s2.getStartTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (fechaS1 == null || fechaS2 == null)
					return 0;
				else
					return fechaS2.compareTo(fechaS1);
			}
		});
		return res;
	}

	private class CargaPreguntasAsync extends AsyncTask<Void, Integer,List<Segments>>{

		@Override
		protected List<Segments> doInBackground(Void... params) {
			System.out.println("ESTOY DENTRO DEL HILO!!!!!**********************************");

			List<Segments> res = cargaPreguntas();

			pdialog.dismiss();

			return res;
		}

		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();

			pdialog.setProgress(progreso);
		}


		@Override
		protected void onPreExecute() {
			pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					CargaPreguntasAsync.this.cancel(true);
				}
			});
			pdialog.setProgress(0);
			pdialog.show();
		}
	}

	private int mesAElegir(){
		int res = 1;

		double random = generaDoubleAleatorio();

		System.out.println("El numero "+random+" ha tenido suerte**************");

		if(random<=0.07)
			res=6;
		else if (random<=0.12)
			res=5;
		else if(random<=0.17)
			res=4;
		else if(random<=0.22)
			res=3;
		else if (random<=0.27)
			res=2;

		return res;
	}


	/**Probabilidad de aceptar un segmento en función del número de veces que haya
	 * aparecido*/
	private double funcionAceptacionRepeticion(int numApar){
		double res =1.0;

		switch(numApar){
			case 0:
				res=1.0;
				break;
			case 1:
				res=0.7;
				break;
			case 2:
				res=0.6;
				break;
			case 3:
				res=0.3;
				break;
			case 4:
				res=0.2;
				break;
			case 5:
				res=0.1;
				break;
			case 6:
				res=0.05;
				break;
			default:
				res=0.0;
				break;
		}

		return res;
	}

	/**Probabilidad de aceptar un segmento en función de su fecha de inicio.*/
	private double funcionAceptacionTemporal(Segments segments){

		Date fechaInicio = new Date();

		try {
			fechaInicio = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ",Locale.FRANCE).parse(segments.getStartTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		double res = 1.0;
		Calendar hoy = Calendar.getInstance();
		Calendar fInicio = new GregorianCalendar();

		fInicio.setTime(fechaInicio);

		long miliseg = hoy.getTimeInMillis() - fInicio.getTimeInMillis();
		double x = SEMANAS_EN_MILISEG * miliseg;

		int semanas= (int) x;

		switch(semanas){
			case 0:
				res=1.0;
				break;
			case 1:
				res=0.8;
				break;
			case 2:
				res=0.7;
				break;
			case 3:
				res=0.5;
				break;
			case 4:
				res = 0.5;
				break;
			default:
				if(semanas>4 && semanas<=8) //Hace un mes
					res = 0.3;
				else if(semanas>8 && semanas<=12) //Hace dos meses
					res = 0.2;
				else if(semanas>12 && semanas<=16) //Hace tres meses
					res=0.15;
				else if(semanas>16 && semanas <=20) //Hace cuatro meses
					res = 0.1;
				else if(semanas>20 && semanas<=24) //hace cinco meses
					res = 0.05;
				else if(semanas>24) //El resto de la eternidad
					res=0.01;
				break;
		}
		return res;
	}

	/**Comprueba si la cantidad de segmentos en el último mes es adecuada para aplicar
	 * la función de aceptación temporal */
	private boolean cumpleAceptacionTemporal(List<Segments> ls){
		haceMenosDeUnMes = Iterables.filter(ls, new Predicate<Segments>() {
			@Override
			public boolean apply(Segments segments) {
				boolean res = false;
				Calendar haceUnMes = Calendar.getInstance();
				haceUnMes.add(Calendar.MONTH,-1);
				Date segmentDate = new Date();

				try {
					segmentDate = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ",Locale.FRANCE).parse(segments.getStartTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				Calendar fechaSegmento = new GregorianCalendar();
				fechaSegmento.setTime(segmentDate);

				//Devuelve true si ocurrio antes de hace un mes
				return fechaSegmento.after(haceUnMes);
			}
		});
		return Iterables.size(haceMenosDeUnMes)>=10;
	}

	/** Comprueba si, en el último mes, existe una variedad de lugares visitados
	 * lo suficientemente buena como para aplciar la función de aceptación por
	 * repetición de lugar*/
	private boolean cumpleVariedadLugares(){
		boolean res = true;

		Multiset<String> apariciones = HashMultiset.create();

		Iterable<String> lugaresHaceMenosDeUnMes = Iterables.transform(haceMenosDeUnMes, new Function<Segments, String>() {
			@Override
			public String apply(Segments segments) {
				return segments.getPlace().getName();
			}
		});

		for(String s:lugaresHaceMenosDeUnMes)
			apariciones.add(s);

		int menosDeTresVeces = 0;
		int masDeTresVeces = 0;
		if(apariciones.elementSet().size()<10){
			for(String s:apariciones){
				if(apariciones.count(s)<=3){menosDeTresVeces++;}
				if(apariciones.count(s)>3){masDeTresVeces++;}
			}

			if(menosDeTresVeces<4 && masDeTresVeces<4)
					res = false;
		}
		return res;
	}


	private double generaDoubleAleatorio(){
		int num = aleatorio2(101);
		double doub = (double) num;
		return doub/100.0;
	}




	/**
	 * Calcula las opciones falsas teniendo en cuenta la opción verdadera.
	 * */
	public List<String> dameFalsos(Segments segmento){
		List<String> res = new ArrayList<>();

        boolean sigue = true;
		Integer randomDayFalse;
		Integer randomSegmentFalse;

		Segments escogido1=null;

		do{
            while(escogido1==null || sigue){
                System.out.println("dentro bucle 1");
                //sigue = false;
                try{
                    randomDayFalse=aleatorio(0,segments,0,0);
                    randomSegmentFalse = aleatorio(1, segments, randomDayFalse, 0);
                    escogido1 = segments.get(randomDayFalse).getSegments().get(randomSegmentFalse);
                    System.out.println("Estoy eligiendo falso 1..."+escogido1.getPlace().getName());
					sigue=escogido1.getPlace().getName()==null;
                }catch(Exception e){
                    e.getStackTrace();
                }
            }
            String verdadero=segmento.getPlace().getName();
            String falso1 = escogido1.getPlace().getName();
            sigue = falso1.equals(verdadero) || falso1.length()<2;
		}while(sigue);

		res.add(escogido1.getPlace().getName());

		Segments escogido2 = null;

		do{
            while(escogido2==null || sigue){
                System.out.println("dentro bucle 2");
                //sigue = false;
                try{
                    randomDayFalse=aleatorio(0,segments,0,0);
                    randomSegmentFalse = aleatorio(1, segments, randomDayFalse, 0);
                    escogido2 = segments.get(randomDayFalse).getSegments().get(randomSegmentFalse);
                    System.out.println("Estoy eligiendo falso 2..."+escogido2.getPlace().getName());
					sigue=escogido2.getPlace().getName()==null;
					System.out.println("sigue falso2: "+sigue);
                } catch(Exception e){
                    e.getStackTrace();
                }
            }

            String verdadero =segmento.getPlace().getName();
            String falso2 = escogido2.getPlace().getName();

            sigue = falso2.equals(verdadero)
                    || res.contains(falso2) || falso2.length()<2;
		}while(sigue);

		res.add(escogido2.getPlace().getName());

		return res;
	}

    /**
     * Función con la que obtenemos los lugares de la API de Moves
     * */

	@Deprecated
	public void getLugares(){
		
		do{
			
			aleat = aleatorio(0,segments,0,0); 
			aseg = aleatorio(1,segments,aleat,0);
			try{
				verdadero = segments.get(aleat).getSegments().get(aseg).getPlace().getName();
			}catch(Exception e){
				Log.e("Verdadero" ,"Fallo en verdadero");
			}
			if(verdadero == null || verdadero.isEmpty()) {
				Log.e("nullVerdadero","nulo");
				verdadero = "";
			}
		}while( verdadero.length() < 2);
		
		
		do{
			af1 = aleatorio(2,segments,0,0);
			asegf1 = aleatorio(1,segments,af1,0);
			try{
			falso1=segments.get(af1).getSegments().get(asegf1).getPlace().getName();
			}catch(Exception e){
				//Log.e("Falso1","nulo");
			}
			if(falso1 == null || falso1.isEmpty()){
				Log.e("nullFalso1","nulo"); 
				falso1 = "";
			}
		}while(falso1.length()<2 || falso1.equals(verdadero));
		
		do{
			af2 = aleatorio(2,segments,0,af1);	
			asegf2 = aleatorio(1,segments,af2,asegf1);	
			try{
			falso2 = segments.get(af2).getSegments().get(asegf2).getPlace().getName();
			}catch(Exception e){
				Log.e("Fallo","Fallo en falso2");
			}
			if(falso2 == null || falso2 == ""){
				Log.e("nullFalso2","nulo");
				falso2 = "";
			}
		}while(falso2.length()<2 || (falso2.equals(falso1) || falso2.equals(verdadero)));
		
	}
    public int restaFecha(int fecha){
        if(fecha%100 == 1) {
            fecha = fecha - 89;
        }else{
            fecha--;
        }
        return fecha;
    }

    /**
     * Función para convertir un mes en dato numérico tal y como lo realizar Moves. P.e. Enero = 01
     * */
    private String getMes(Calendar date){
		int mes = date.get(Calendar.MONTH);
        String res = "";
        switch (mes){
            case 0:
                res = "enero";
                break;
            case 1:
                res = "febrero";
                break;
            case 2:
                res = "marzo";
                break;
            case 3:
                res = "abril";
                break;
            case 4:
                res = "mayo";
                break;
            case 5:
                res = "junio";
                break;
            case 6:
                res = "julio";
                break;
            case 7:
                res = "agosto";
                break;
            case 8:
                res = "septiembre";
                break;
            case 9:
                res = "octubre";
                break;
            case 10:
                res = "noviembre";
                break;
            case 11:
                res = "diciembre";
                break;

        }
        return res;
    }

	private String getDiaSemana(Calendar cal){
		String dia = "";
		switch(cal.get(Calendar.DAY_OF_WEEK)){
			case 1:
				dia = "domingo";
				break;
			case 2:
				dia = "lunes";
				break;
			case 3:
				dia = "martes";
				break;
			case 4:
				dia = "miércoles";
				break;
			case 5:
				dia = "jueves";
				break;
			case 6:
				dia = "viernes";
				break;
			case 7:
				dia = "sábado";
				break;

		}
		return dia;
	}

	/**
	 * Devuelve un String que representa la hora de la fecha que recibe como
	 * parámetro.
	 * */
	private String getHora(Calendar date){
		String res = "";

		int horas = date.get(Calendar.HOUR_OF_DAY);
		if(horas<10)
			res+="0"+horas;
		else
			res+=horas;

		int minutos = date.get(Calendar.MINUTE);
		if(minutos<10)
			res+=":0"+minutos;
		else
			res+=":"+minutos;

		return res;
	}



	/**
	 * Método necesario para hacer una llamada a la API
	 * */
	private String convierteMes(String mes){
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


}
