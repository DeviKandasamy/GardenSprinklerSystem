package businessLogic;

class SprinklerSchedule{
	// By Current, we mean schedule for the day - could be weekly or daily configured by user on the UI	
	//Mapping individual sprinkler to its schedule	
	//update schedule
	//data members: declaration,

	int startTime, endTime, thresTemp;

	boolean dailyScheduleType;//0 for weeklySchedule, 1 for dailySchedule
	SprinklerSchedule(){
		startTime =0;
		endTime = 0;
		thresTemp = 0;
		dailyScheduleType = false;
	}

	//This function should be called only in the workflow for creating daily schedule
	void setSprinklerSchedule(int starttime, int endtime, int temperature){
		startTime = starttime;
		endTime = endtime;
		thresTemp = temperature;
		dailyScheduleType = true;
	}

	//This function should be called only in the workflow for creating a schedule based on the weekly schedule
	void setSprinklerSchedule(int starttime, int endtime){
		startTime = starttime;
		endTime = endtime;
		thresTemp = 40; // Default low threshold for weekly schedules
		dailyScheduleType = false;
	}

	// This function returns the time of the next event (ON or OFF) based on the current time
	int nextSprinklerEventTime(int currentTime, int temperature) {
		//isSprinklerEnabled?
		if (startTime == 0 || endTime ==0 ){
			return -1;
		}
		else if ( temperature < thresTemp ){
			return -1;
		}else if (currentTime < startTime){
			return startTime;
		} else if (currentTime <endTime){
			return endTime;
		} else {
			return -1;
		}
	}

	// This function returns the start time of the sprinkler schedule
	public int getStartTime()
	{
		return startTime;
	}
	public int getEndTime()
	{
		return endTime;
	}

	// This function sets the start time and end time of Sprinkler schedules
	public void setStartTime(int timeinsec)
	{
		startTime = timeinsec;
	}
	public void setEndTime(int timeinsec)
	{
		endTime = timeinsec;
	}

	boolean nextSprinklerEventType( int currentTime) {
		if (currentTime >= startTime && currentTime <= endTime){
			return false; //Stop Event
		} else {
			return true; //Start Event
		}
	}
}

