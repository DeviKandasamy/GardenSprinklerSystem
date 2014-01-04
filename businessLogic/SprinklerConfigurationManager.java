package businessLogic;
import dataStore.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class SprinklerConfigurationManager{
	boolean configurationChanged = false;
	static int currentTemperature = 70;
	static int sprinklerFlowRatePerSec = 1;

	public int sprinklerStatus[] = new int [4*3];//member: 1-d array
	public Sprinkler[] sprinkler = new Sprinkler[4*3];//4 zones, 3 sprinklers in each zone

	SprinklerFileStore sprinklerFileObject = new SprinklerFileStore();
	//Queue for sprinkler events that have to be processed in the ScheduleWorkerThread's next wakeup
	Queue<SprinklerEvent> sprinklerEventQueue=new LinkedList<SprinklerEvent>();
	SprinklerUtils sprinklerUtils = new SprinklerUtils();

	/**
	 *  Function to set the current System Temperature
	 * @param temperature
	 */
	public static void setCurrentTemperature(String temperature)
	{
		currentTemperature = Integer.parseInt(temperature);
	}

	/**
	 * Function to return a string indicating the status of a sprinkler
	 * @param sprinklerId
	 * @return individual sprinkler status
	 */
	public String getSprinklerStatus( String sprinklerId) 
	{
		int sprinklerIndex = sprinklerUtils.getSprinklerIndex(sprinklerId);
		if (sprinkler[sprinklerIndex].isSprinklerActive())
		{
			return "Currently ON";
		} else if (sprinkler[sprinklerIndex].isSprinklerInactive()) {
			return "NOT ON";
		}
		else if (sprinkler[sprinklerIndex].isSprinklerDisabled()) {
			return "DeActivated";
		} else if (sprinkler[sprinklerIndex].isSprinklerBroken()) {
			return "NOT OK";
		} else { 
			return "Unknown";
		}
	}

	/**
	 * Function used to handle enable or disable configurations made on Sprinker or Zone
	 * @param enable
	 * @param category
	 * @param config
	 */
	public void handleEnableDisable(boolean enable, String category, String config)
	{
		int start_offset =0, end_offset = 0, i =0;
		System.out.println("config1: "+ config);
		System.out.println("enable1: "+ enable);
		System.out.println("category1: "+ category);
		if (category == "Entire System") {
			start_offset = 0;
			end_offset=11;
		} else if (category == "Group") {
			start_offset = sprinklerUtils.zoneIndex.get(config) * 3;
			end_offset = start_offset+2;
		} else {
			start_offset = sprinklerUtils.getSprinklerIndex(config);
			end_offset = start_offset;
			System.out.println( "handle enabledisable for single sprinkler : " + start_offset);
		}

		for (i = start_offset; i <= end_offset; i++){
			if (enable) {
				enableSprinkler(i);
			} else {
				disableSprinkler(i);
			}
		}
	}

	/**
	 * Function to handle breaking Sprinklers
	 * @param sprinklerID
	 */
	public void breakSprinkler(String sprinklerID)
	{
		int sprinklerIndex;
		sprinklerIndex = sprinklerUtils.getSprinklerIndex(sprinklerID);		
		sprinkler[sprinklerIndex].breakSprinkler();
		sprinklerStatus[sprinklerIndex] = sprinkler[sprinklerIndex].getStatus();
		sprinkler[sprinklerIndex].setStartTime(-1);
		sprinkler[sprinklerIndex].setEndTime(-1);
	}

	/**
	 *  Function to enable a sprinkler
	 * @param sprinklerIndex
	 */
	private void enableSprinkler ( int sprinklerIndex) {
		//if sprinkler its not sprinkler's start time yet, we need to do nothing
		// if sprinkler needs to be ON right now, we put it in the enableDisableEventQueue so that
		// the Schedule worker thread gets to it right away. 
		if (sprinkler[sprinklerIndex].isSprinklerEnabled()) {
			return;
		}
		if (sprinkler[sprinklerIndex].isSprinklerReadytoStart())
		{
			sprinkler[sprinklerIndex].activateSprinkler();
			sprinkler[sprinklerIndex].setStartTime(sprinklerUtils.getCurrentTime());
			if (sprinklerIndex == 11)
				System.out.println("@enableSprikler: Sprinkler "+sprinklerIndex+" 's start time is " + sprinkler[sprinklerIndex].getStartTime() );
			//Turn this on immediately, we'll be past the enabled sprinkler's start time by the time Schedule Worker thread handles this in the event queue
		} else {
			//SprinklerEvent newSprinklerEvent = new SprinklerEvent(sprinklerIndex, true, false);
			//false - weekly schedule, true - start Event
			//enableDisableEventQueue.add(newSprinklerEvent);
			sprinkler[sprinklerIndex].enableSprinkler();
		}
		sprinklerStatus[sprinklerIndex] = sprinkler[sprinklerIndex].getStatus();
	}

	/**
	 * Function to retrieve Group Status based on status of individual sprinklers
	 * @param zone
	 * @return String status of group
	 */
	public String getGroupStatus(String zone) {
		int false_count = 0, true_count = 0;
		int start_index = sprinklerUtils.zoneIndex.get(zone)*3;
		int end_index;
		int i = 0;
		String status, sprinklerStatus="";
		end_index = start_index + 2;
		System.out.println("@getGroupStatus :: Zone : " + zone);
		for (i = start_index; i <= end_index; i++ ) {
			if (sprinkler[i].isSprinklerActive())
				true_count++;
			else if (sprinkler[i].isSprinklerInactive())
				false_count++;
			System.out.println("@getGroupStatus :: Sprinkler Index : " + i);
			if (sprinkler[i].isSprinklerBroken()) {
				System.out.println("@getGroupStatus ::sprinkler " + i + "is Broken");
				sprinklerStatus += ", " + sprinklerUtils.getSprinklerID(i) + "(Broken) ";
				System.out.println("@getGroupStatus ::sprinklerStaus : " + sprinklerStatus);
			} else if (sprinkler[i].isSprinklerDisabled()) {
				System.out.println("@getGroupStatus ::sprinkler " + i + "is Disabled");
				sprinklerStatus += ", " + sprinklerUtils.getSprinklerID(i) + "(Disabled) ";
				System.out.println("@getGroupStatus ::sprinklerStaus : " + sprinklerStatus);
			}
		}
		if (true_count ==0) {
			status = "OFF"+sprinklerStatus;
		} else if (false_count == 0) {
			status = "ON"+sprinklerStatus;
		} else {
			status = "Partially ON "+sprinklerStatus;
		}
		return status;
	}

	/**
	 * Function to disable a sprinkler
	 * @param sprinklerIndex
	 */
	private void disableSprinkler (int sprinklerIndex){
		String zone;
		int waterVolume;
		zone = sprinklerUtils.zoneCodes[sprinklerIndex/3];
		if (sprinkler[sprinklerIndex].isSprinklerDisabled()) {
			return; // Already disabled
		}
		if (sprinkler[sprinklerIndex].isSprinklerActive())
		{
			waterVolume = sprinklerUtils.getCurrentTime() - sprinkler[sprinklerIndex].getStartTime();
			try {
				sprinklerFileObject.storeWaterConsumptioninFile(waterVolume, zone);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sprinkler[sprinklerIndex].disableSprinkler();
		sprinklerStatus[sprinklerIndex] = sprinkler[sprinklerIndex].getStatus();
	}

	/**
	 * Function to process and apply Daily configurations to a sprinkler or group of sprinklers
	 * @param sprinklerID
	 * @param zone
	 * @param starttime
	 * @param endtime
	 * @param temperature
	 */
	public void  applyDailyConfig(String sprinklerID, String zone, int starttime, int endtime, int temperature )
	{
		int i;
		if (sprinklerID == "ALL"){
			for (i = 0; i < 3; i++)
			{
				String newSprinklerID = String.valueOf(zone.charAt(0)) + Integer.toString(i+1);  
				applyDailyConfigForEach(newSprinklerID, zone, starttime, endtime, temperature );
			}
		} else {
			applyDailyConfigForEach(sprinklerID, zone, starttime, endtime, temperature);
		}
	}

	/**
	 * Function to apply daily configuration for a given Sprinkler
	 * @param sprinklerID
	 * @param zone
	 * @param starttime
	 * @param endtime
	 * @param temperature
	 */
	private void applyDailyConfigForEach(String sprinklerID, String zone, int starttime, int endtime, int temperature )
	{
		int sprinklerId;
		int sprinklerStatus;
		String logLine="";
		sprinklerId = sprinklerUtils.getSprinklerIndex(sprinklerID);
		if (sprinkler[sprinklerId].isSprinklerActive()) 
		{
			sprinkler[sprinklerId].setSprinklerSchedule(sprinkler[sprinklerId].getStartTime(), endtime, temperature);
		} else {
			sprinkler[sprinklerId].setSprinklerSchedule(starttime, endtime, temperature);
		}
		logLine += sprinklerID + "    " + starttime + "    " + "   " + endtime+ "    " + temperature;
		sprinklerFileObject.storeDailyConfiginFileLog(logLine);
		configurationChanged = true;
	}

	/**
	 * Constructor function - Initialize SprinklerStatus to be Activated and OFF
	 */
	public SprinklerConfigurationManager(){
		for(int i=0;i<12;i++) {
			sprinklerStatus[i] = 3;
		}
	}

	/**
	 * Function to initialize sprinklers, read weekly schedule and start the SprinklerScheduleWorker thread 
	 */
	public void initialize() {
		for (int i=0;i<12;i++){
			sprinkler[i] = new Sprinkler();
		}
		readWeeklyScheduleFromFile();
		setUpSprinklerScheduleWorker();
	}

	/**
	 * Function to process weekly
	 */
	public void readWeeklyScheduleFromFile()
	{
		Vector<String> todaySchedule = new Vector<String>();
		String[] words = new String[20];
		String[] timesplit = new String[3];
		int startTime, endTime, sprinklerIndex;
		todaySchedule = sprinklerFileObject.readWeeklyScheduleFromFile();
		Iterator iterator = todaySchedule.iterator();
		while(iterator.hasNext()) {
			String str = (String) iterator.next();
			words = str.split("\t");
			System.out.println(str);
			timesplit = words[2].split(":");
			startTime = (Integer.parseInt(timesplit[0])*3600)+(Integer.parseInt(timesplit[1])*60)+Integer.parseInt(timesplit[2]);
			timesplit = words[3].split(":");
			endTime = (Integer.parseInt(timesplit[0])*3600)+(Integer.parseInt(timesplit[1])*60)+Integer.parseInt(timesplit[2]);
			sprinklerIndex = sprinklerUtils.zoneIndex.get(words[1]);
			sprinkler[3*sprinklerIndex].setSprinklerSchedule(startTime, endTime);
			sprinkler[3*sprinklerIndex + 1].setSprinklerSchedule(startTime, endTime);
			sprinkler[3*sprinklerIndex + 2].setSprinklerSchedule(startTime, endTime);
		}
	}

	public String getGroupWaterConsumption (String zone)
	{
		return sprinklerFileObject.getGroupConsumptionFromFile(zone);
	}

	public String getSprinklerZone(String sprinklerID)
	{
		return sprinklerUtils.getSprinklerZone(sprinklerID);
	}

	/**
	 * Function to process events from the event queue 
	 */
	void processEventQueue() {
		SprinklerEvent sprinkEvent;
		while ((sprinkEvent = sprinklerEventQueue.poll())!= null) {
			System.out.println("Processing event for Sprinkler : " + sprinkEvent.sprinklerIndex + " at " + sprinklerUtils.getCurrentTime());
			switchOnOffSprinkler(sprinkEvent.sprinklerIndex, sprinkEvent.eventType);
		}
	}

	/**
	 * Function to fetch the next earliest ON/OFF event from the set of Sprinklers
	 * @return sprinkler ID
	 */
	int getNextEventFromSprinklers() {
		int i, currentTime, nextEventTime=86400, leastNextEventTime=86400; 
		for (i=0 ; i < 12; i++) 
		{
			currentTime = sprinklerUtils.getCurrentTime();
			nextEventTime = sprinkler[i].nextSprinklerEventTime(currentTime, currentTemperature);
			if ((nextEventTime >= 0) && (nextEventTime < leastNextEventTime)) {
				leastNextEventTime = nextEventTime;
			}
		}
		return leastNextEventTime;
	}

	/**
	 * Function to schedule the next sprinkler event for execution by adding it to the Sprinkler Queue
	 * @param leastNextEventTime
	 */
	void scheduleNextSprinklerEvents(int leastNextEventTime)
	{
		int i, currentTime;
		//3. For sprinklers with this earliest nextEvent time, create SprinklerEvent objects and enqueue into SprinklerEventQueue
		for (i=0; i < 12; i++) {
			currentTime = sprinklerUtils.getCurrentTime();
			if (leastNextEventTime == sprinkler[i].nextSprinklerEventTime(currentTime, currentTemperature))
			{
				SprinklerEvent newSprinklerEvent = new SprinklerEvent(i, sprinkler[i].nextSprinklerEventType(currentTime)); 
				//create a SprinklerEvent and add it to the Sprinkler Event Queue
				System.out.println("Adding an event for Sprinkler " + i );
				sprinklerEventQueue.add(newSprinklerEvent);
			}
		}
	}

	/**
	 * Function to encapsulate the operations of processing event queue's current events and scheduling the next set of 
	 * events based on sprinkler schedule configurations
	 * @return next sprinkler event time
	 */
	int handleSprinklerEvents(){
		SprinklerEvent sprinkEvent;
		int currentTime;
		int leastNextEventTime = 86400, i;
		//1. Process events at the start of sprinklerEventQueue
		processEventQueue();
		//2. Scan through sprinklerDaily to see the earliest nextEvent time
		leastNextEventTime = getNextEventFromSprinklers();
		// 3. Schedule Next Sprinkler Events
		scheduleNextSprinklerEvents(leastNextEventTime);
		//4. Return least Next Event time
		return leastNextEventTime;
	}

	/**
	 * Function that is executed by Schedule Worker Thread that is responsible for processing events and scheduling events based
	 * on sprinkler schedule
	 */
	void setUpSprinklerScheduleWorker(){
		Thread sprinklerScheduleWorker = new Thread() {
			public void run() {
				int timeToSleep = 0, nextEventTime, currTime, i;
				while (true) {
					nextEventTime = handleSprinklerEvents();
					currTime = sprinklerUtils.getCurrentTime();
					timeToSleep = nextEventTime -  currTime;
					System.out.println( "@setUpSprinklerScheduleWorker :: timeToSleep : " + timeToSleep);
					try {
						for (i =0; i <= timeToSleep; i++) {
							sleep(1000);
							if (configurationChanged) {
								System.out.println("@setUpSprinklerScheduleWorker:Configuration has Changed, time for action");
								SprinklerEvent sprinkEvent;
								while ((sprinkEvent = sprinklerEventQueue.poll())!= null);
								nextEventTime = getNextEventFromSprinklers();
								scheduleNextSprinklerEvents(nextEventTime);
								timeToSleep = nextEventTime - sprinklerUtils.getCurrentTime();
								i=0;
								System.out.println(" New time To sleep " + timeToSleep);
								configurationChanged = false;
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		sprinklerScheduleWorker.start();
	}

	/**
	 * Function to switch the sprinkler ON of OFF and do the necessary post-processing
	 * @param sprinklerIndex
	 * @param sprinklerEventType
	 */
	void switchOnOffSprinkler (int sprinklerIndex, boolean sprinklerEventType)
	{
		if (sprinklerEventType == false) //Sprinkler OFF
		{
			sprinkler[sprinklerIndex].deactivateSprinkler();
			computeStoreWaterConsumption(sprinklerIndex);
		} else {
			sprinkler[sprinklerIndex].activateSprinkler();
		}
		sprinklerStatus[sprinklerIndex] =  sprinkler[sprinklerIndex].getStatus();
	}

	/**
	 * Function to compute Water consumption based on Sprinkler's start and end times in its configuration
	 * @param sprinklerIndex
	 */
	public void computeStoreWaterConsumption(int sprinklerIndex)
	{
		int waterVolume = 0;
		String zone;
		waterVolume = sprinkler[sprinklerIndex].getEndTime() - sprinkler[sprinklerIndex].getStartTime();
		zone = sprinklerUtils.zoneCodes[sprinklerIndex/3];
		try {
			sprinklerFileObject.storeWaterConsumptioninFile(waterVolume, zone);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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