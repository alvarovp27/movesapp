
package com.example.movesclass;


public class Segments{
   	private String endTime;
   	private String lastUpdate;
   	private Place place;
   	private String startTime;
   	private String type;

 	public String getEndTime(){
		return this.endTime;
	}
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
 	public String getLastUpdate(){
		return this.lastUpdate;
	}
	public void setLastUpdate(String lastUpdate){
		this.lastUpdate = lastUpdate;
	}
 	public Place getPlace(){
		return this.place;
	}
	public void setPlace(Place place){
		this.place = place;
	}
 	public String getStartTime(){
		return this.startTime;
	}
	public void setStartTime(String startTime){
		this.startTime = startTime;
	}
 	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type = type;
	}
}
