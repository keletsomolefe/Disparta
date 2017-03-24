package com.disparta;

import java.util.ArrayList;
import java.util.List;

public class Chats {
    int imageID;
    String name;
	String message_id;
	String LatestMsg;
	String image;
	String messageDate;
	String newMessage;

    Chats(String theName, String theCurrMsg,String message_id, String image, String messageDate, String newmessage){
        this.LatestMsg = theCurrMsg;
		this.image = image;
		this.message_id = message_id;
        this.name = theName;
		this.messageDate = messageDate;
		this.newMessage = newmessage;

	}

	public String getName() {
		return name;
	}

	public String getMessage_id() {
		return message_id;
	}

	public  String getLatestMsg() {
		return  LatestMsg;
	}

	public String getImage() {
		return  image;
	}

	public String getNewMessage() {return newMessage;}

	public String getMessageDate() {return messageDate;}

	private List<Chats> chats;
	
	private void initializeData() {
		chats = new ArrayList<>();
		/*chats.add( new Chats("John", "I dunno but we can add that too"));
		chats.add(new Chats("Matt", "I dunno"));
		chats.add(new Chats("Harry", "I miss that game."));
		chats.add(new Chats("Rose", "Hello"));*/
	}
	

}
