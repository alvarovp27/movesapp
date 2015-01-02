
package com.example.movesclass;

import java.util.List;

public class Places{
   	private String date;
   	private String lastUpdate;
   	private List<Segments> segments;

 	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date = date;
	}
 	public String getLastUpdate(){
		return this.lastUpdate;
	}
	public void setLastUpdate(String lastUpdate){
		this.lastUpdate = lastUpdate;
	}
 	public List<Segments> getSegments(){
		return this.segments;
	}
	public void setSegments(List<Segments> segments){
		this.segments = segments;
	}
}
