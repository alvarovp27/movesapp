package com.example.movesclass;

public class Resultados {

	private String contestado;
	private String verdadera;
	public Resultados(String contestado, String verdadera) {
		super();
		this.contestado = contestado;
		this.verdadera = verdadera;
	}
	public String getContestado() {
		return contestado;
	}
	public void setContestado(String contestado) {
		this.contestado = contestado;
	}
	public String getVerdadera() {
		return verdadera;
	}
	public void setVerdadera(String verdadera) {
		this.verdadera = verdadera;
	}
	
}
