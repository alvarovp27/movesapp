package com.example.movesclass;

public class Respuestas {
	
	private int Verdadero;
	private int Segmento;
	private int Falso1;
	private int SegmentoFalso1;
	private int Falso2;
	private int SegmentoFalso2;

	public Respuestas(int verdadero, int segmento, int falso1,
			int segmentoFalso1, int falso2, int aleatoriofalso2) {
		super();
		this.Verdadero = verdadero;
		this.Segmento = segmento;
		this.Falso1 = falso1;
		this.SegmentoFalso1 = segmentoFalso1;
		this.Falso2 = falso2;
		this.SegmentoFalso2 = aleatoriofalso2;
	}

	public int getVerdadero() {
		return Verdadero;
	}

	public void setVerdadero(int verdadero) {
		Verdadero = verdadero;
	}

	public int getSegmento() {
		return Segmento;
	}

	public void setSegmento(int segmento) {
		Segmento = segmento;
	}

	public int getFalso1() {
		return Falso1;
	}

	public void setFalso1(int falso1) {
		Falso1 = falso1;
	}

	public int getSegmentoFalso1() {
		return SegmentoFalso1;
	}

	public void setSegmentoFalso1(int segmentoFalso1) {
		SegmentoFalso1 = segmentoFalso1;
	}

	public int getFalso2() {
		return Falso2;
	}

	public void setFalso2(int falso2) {
		Falso2 = falso2;
	}

	public int getSegmentoFalso2() {
		return SegmentoFalso2;
	}

	public void setSegmentoFalso2(int aleatoriofalso2) {
		this.SegmentoFalso2 = aleatoriofalso2;
	}
	
}
