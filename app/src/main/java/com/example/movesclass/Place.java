
package com.example.movesclass;


public class Place{
   	private String foursquareId;
   	private Number id;
   	private Location location;
   	private String name;
   	private String type;

 	public String getFoursquareId(){
		return this.foursquareId;
	}
	public void setFoursquareId(String foursquareId){
		this.foursquareId = foursquareId;
	}
 	public Number getId(){
		return this.id;
	}
	public void setId(Number id){
		this.id = id;
	}
 	public Location getLocation(){
		return this.location;
	}
	public void setLocation(Location location){
		this.location = location;
	}
 	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
 	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type = type;
	}
}
