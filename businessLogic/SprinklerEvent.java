package businessLogic;

class SprinklerEvent{
	int sprinklerIndex;
	boolean eventType; 
	// 1 to start, 0 to stop
	//0 for weeklySchedule, 1 for dailySchedule

	SprinklerEvent(int sprinklerID, boolean eventID){
		sprinklerIndex = sprinklerID;
		eventType = eventID;
	}

	int getSprinklerIndex(){
		return sprinklerIndex;
	}

	boolean getEventType(){
		return eventType; 
	}
}