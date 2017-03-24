package com.disparta;

public class people {
	
	private String Messageid;
	private String title;
	private String currMsg;

	
	public people(String messageID, String Title, String CurrMsg){
		Messageid=messageID;
		title=Title;
		currMsg = CurrMsg;
	}
	
	
	public String getMessageID(){return Messageid;}
	public String getTitle(){return title;}
	public String getCurrMsg() {return currMsg;}

}
