package com.disparta;

public class Users {

	int id;
	String name;
	String place;
	String email;
	String about;
	String username;
	String phoneNumber;
	String profilePicture;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public String getNumber() {
		return phoneNumber;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPlace() {
		return place;
	}
	public String getAbout() {
		return about;
	}
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	
	
	public void setPlace(String place) {
		this.place = place;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	public void setNumber(String Number) {
		this.phoneNumber = Number;
	}
}
