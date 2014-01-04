package businessLogic;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Sprinkler {

	SprinklerSchedule sprinklerSchedule  = new SprinklerSchedule();
	int sprinklerStatus; //0 - Broken, 1- Disabled, 2- Enabled, 3-Inactive, 4-Active
	int currentTemp = 70;
	SprinklerUtils sprinklerUtils = new SprinklerUtils();

	public Sprinkler () {
		sprinklerStatus = 3;
	}

	boolean isSprinklerEnabled (){
		if (sprinklerStatus >= 2){
			return true;
		} else {
			return false;

		}
	}

	boolean isSprinklerDisabled (){
		if (sprinklerStatus == 1){
			return true;
		} else {
			return false;

		}
	}

	boolean isSprinklerActive(){
		if (sprinklerStatus == 4){
			return true;
		} else {
			return false;

		}
	}

	boolean isSprinklerInactive (){
		if (sprinklerStatus == 3){
			return true;
		} else {
			return false;

		}
	}

	boolean isSprinklerBroken (){
		if (sprinklerStatus == 0){
			return true;
		} else {
			return false;
		}
	}

	void setStartTime(int timeinsec) {
		sprinklerSchedule.setStartTime(timeinsec);
	}

	void setEndTime(int timeinsec) {
		sprinklerSchedule.setEndTime(timeinsec);
	}

	int getStartTime() {
		return sprinklerSchedule.getStartTime();
	}

	int getEndTime() {
		return sprinklerSchedule.getEndTime();
	}

	void setSprinklerSchedule(int starttime, int endtime, int temperature){
		sprinklerSchedule.setSprinklerSchedule(starttime, endtime, temperature);
	}

	void setSprinklerSchedule(int starttime, int endtime){
		sprinklerSchedule.setSprinklerSchedule(starttime, endtime);
	}

	boolean nextSprinklerEventType( int currentTime) {
		return sprinklerSchedule.nextSprinklerEventType(currentTime);
	}

	int nextSprinklerEventTime(int currentTime, int temperature) {
		if (isSprinklerDisabled() || isSprinklerBroken()) {
			return -1;
		}
		return sprinklerSchedule.nextSprinklerEventTime(currentTime, temperature);
	}

	void activateSprinkler(){
		if (!isSprinklerBroken()) {
			sprinklerStatus = 4;
		}
	}

	void deactivateSprinkler() {
		if (!isSprinklerBroken()) {
			sprinklerStatus = 3;
		}
	}

	void enableSprinkler() {
		if ( !isSprinklerBroken ()) {
			sprinklerStatus = 3;
		}
	}

	void disableSprinkler() {
		if (!isSprinklerBroken()) {
			sprinklerStatus = 1;
		}
	}

	void breakSprinkler() {
		sprinklerStatus = 0;
	}

	boolean isSprinklerReadytoStart() {
		if (sprinklerStatus >=1 && currentTemp >= sprinklerSchedule.thresTemp)
		{
			if (sprinklerSchedule.startTime <= sprinklerUtils.getCurrentTime() && sprinklerSchedule.endTime > sprinklerUtils.getCurrentTime() )
			{     
				return true;
			}
			else {
				return false;
			} 
		}else {
			return false;
		}
	}
	public int getStatus() { 
		return sprinklerStatus; 
	}
}

/*
 * SprinklerUtils - class
 */
class SprinklerUtils
{
	String zoneCodes[] = {"North", "South", "West", "East"};
	Map <String, Integer> zoneIndex = new HashMap<String, Integer>();

	public int getSprinklerIndex(String sprinklerId)
	{
		int sprinklerIndex = 0;

		if ((sprinklerId.charAt(0)) == 'N' )
			sprinklerIndex = 0 ;
		else if (sprinklerId.charAt(0) == 'S')
			sprinklerIndex = 3;
		else if (sprinklerId.charAt(0) == 'W')
			sprinklerIndex = 6;
		else  if (sprinklerId.charAt(0) == 'E')
			sprinklerIndex = 9;
		sprinklerIndex += sprinklerId.charAt(1) - '0' - 1;

		return sprinklerIndex;
	}

	SprinklerUtils() {
		zoneIndex.put("North", 0);
		zoneIndex.put("South", 1);
		zoneIndex.put("West", 2);
		zoneIndex.put("East", 3);

	}

	public String getSprinklerID(int sprinklerIndex) 
	{
		String sprinklerID = null;
		int zoneIndex = sprinklerIndex/3;

		switch (zoneIndex) {
		case 0 : sprinklerID = "N"; break;
		case 1: sprinklerID = "S";break;
		case 2: sprinklerID = "W";break;
		case 3: sprinklerID = "E"; break;
		default : System.out.println("Error!! Unknown Zone Index");
		}
		sprinklerID = sprinklerID + (sprinklerIndex%3 +1);
		return sprinklerID;
	}

	public String getSprinklerZone(String sprinklerId)
	{
		return zoneCodes[getSprinklerIndex(sprinklerId)/3];
	}

	public static int getCurrentTime()
	{
		Calendar now = Calendar.getInstance();
		int currentTime = 0;
		int h = now.get(Calendar.HOUR_OF_DAY);
		int m = now.get(Calendar.MINUTE);
		int s = now.get(Calendar.SECOND);
		//System.out.println("" + h + ":" + m + ":" + s);
		currentTime = 3600*h + 60*m + s;
		return currentTime;
	}

	/**
	 * Convert time in hh::mm::ss format to seconds
	 * @param hour
	 * @param minute
	 * @param seconds
	 * @return time in seconds
	 */
	public static int convertTimetoSec(String hour, String minute, String seconds)
	{
		int h, m, s;
		int timeinsec = 1000;

		h = Integer.parseInt(hour);
		m = Integer.parseInt(minute);
		s = Integer.parseInt(seconds);

		timeinsec = 3600*h + 60*m + s;

		return timeinsec;	
	}
}
