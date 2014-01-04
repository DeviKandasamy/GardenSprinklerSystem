package junit;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import businessLogic.Sprinkler;
import businessLogic.SprinklerConfigurationManager;

public class SprinklerTestCases {

	int result;
	String value;

	/**
	 * To convert time to seconds
	 *  
	 */
	public static int getCurrentTime()
	{
		Calendar now = Calendar.getInstance();
		int currentTime = 0;
		int h = now.get(Calendar.HOUR_OF_DAY);
		int m = now.get(Calendar.MINUTE);
		int s = now.get(Calendar.SECOND);
		currentTime = 3600*h + 60*m + s;
		return currentTime;
	}

	@Test
	/**
	 * 1. Testing of the method 
	 * SprinklerConfigurationManager:convertTimetoSec[class:Method]
	 */
	public void testTime() {	
		SprinklerConfigurationManager sprinklerObject = new SprinklerConfigurationManager();
		result = sprinklerObject.convertTimetoSec("1","56","24");
		System.out.println("TestCase1: result="+result);
		assertEquals(6984, result);
	}

	@Test
	/*
	 * 2. Testing of the method 
	 * SprinklerConfigurationManager:getSprinklerStatus[class:method]
	 * Initialize - method is invoked to set the default weekly schedule
	 * There could be two outputs
	 * 	1. NOT ON - when the N1 sprinkler is enabled but not active
	 *  2. ON - when the N1 sprinkler is enabled but active
	 */	
	public void testSpinklerStatus() 
	{
		SprinklerConfigurationManager sprinklerObject1 = new SprinklerConfigurationManager();
		sprinklerObject1.initialize();
		value = sprinklerObject1.getSprinklerStatus("N1");
		System.out.println("TestCase2: value="+value);
		assertEquals("NOT ON",value);
	}

	@Test	
	/*
	 * 3. Testing of the method 
	 * SprinklerConfigurationManager:testhandleEnableDisable[class:method]
	 * Initialize - method is invoked to set the default weekly schedule
	 * There could be two outputs
	 * 	1. NOT ON - when the N1 sprinkler is enabled but not active
	 *  2. ON - when the N1 sprinkler is enabled but active
	 */
	public void testhandleEnableDisable(){
		SprinklerConfigurationManager sprinklerObject2 = new SprinklerConfigurationManager();
		sprinklerObject2.initialize();
		sprinklerObject2.handleEnableDisable(false, "IndividualSprinkler", "N1");
		value = sprinklerObject2.getSprinklerStatus("N1");
		System.out.println("TestCase3: value="+value);
		assertEquals("DeActivated",value);
	}

	@Test
	/*
	 * 4. Testing the method
	 * Sprinkler:getStatus[class:method]
	 * The constructor initializes the value to 3
	 * verification of the getStatus method 
	 */
	public void testSprinklerStatus(){
		Sprinkler Sprobject = new  Sprinkler();
		result = Sprobject.getStatus();
		System.out.println("TestCase4: result="+result);
		assertEquals(3,result); 		
	}

	@Test
	/*
	 * 5. Testing the method
	 * SprinklerConfigurationManager:breakSprinkler[class:method]
	 * Break the sprinkler on the north zone N1
	 * Check the status of the sprinkler
	 */
	public void testbreakSprinkler(){
		SprinklerConfigurationManager sprinklerObject3 = new SprinklerConfigurationManager();
		sprinklerObject3.initialize();
		sprinklerObject3.breakSprinkler("N1");
		value = sprinklerObject3.getSprinklerStatus("N1");
		System.out.println("TestCase5: value="+value);
		assertEquals("NOT OK",value); 
	}

	@Test
	/*
	 * 6. Testing the method
	 * SprinklerConfigurationManager:getGroupStatus[class:method]
	 * Initial status of all sprinkler should be OFF/ON based on the weekly schedule
	 * If the schedule time is same as current time, then its ON else OFF
	 */
	public void testGroupStatus(){
		SprinklerConfigurationManager sprinklerObject4 = new SprinklerConfigurationManager();
		sprinklerObject4.initialize();
		value = sprinklerObject4.getGroupStatus("North");
		System.out.println("TestCase6: value="+value);
		assertEquals("OFF",value);
	}

	@Test
	/*
	 * 7. Testing the method
	 * SprinklerConfigurationManager:breakSprinkler[class:method]
	 * SprinklerConfigurationManager:getGroupStatus[class:method]
	 * Break a sprinkler and then test the group status 
	 */
	public void testBrokenStatus(){
		SprinklerConfigurationManager sprinklerObject5 = new SprinklerConfigurationManager();
		sprinklerObject5.initialize();
		sprinklerObject5.breakSprinkler("N2");
		value = sprinklerObject5.getGroupStatus("North");
		System.out.println("TestCase7: value="+value);
		assertEquals("OFF, N2(Broken) ",value);
	}

	@Test
	/*
	 * 8. Testing the method
	 * Apply Schedule - SprinklerConfigurationManager:applyDailyConfig[class:method]
	 * Water consumption - SprinklerConfigurationManager:getGroupWaterConsumption[class:method]
	 * 	1. The sprinkler is configured to run for 7 seconds
	 *  2. The water consumption for 7 seconds is current water consumption of the N1 sprinkler plus 7
	 */
	public void testScheduleandWaterConsumption() throws InterruptedException{
		int starttime;
		int endtime;
		int waitTime;
		int waterConsumption;
		String oldwaterUsage, newwaterUsage;;
		SprinklerConfigurationManager sprinklerObject6 = new SprinklerConfigurationManager();
		sprinklerObject6.initialize();
		starttime = getCurrentTime() + 1;
		endtime = starttime + 7; // Run the sprinkler for 7 seconds
		oldwaterUsage =sprinklerObject6.getGroupWaterConsumption("North");
		System.out.println("TestCase8: waterUsage before scheduling sprinkler N1 ="+oldwaterUsage);
		sprinklerObject6.applyDailyConfig("N1", "North", starttime, endtime, 62);
		waitTime = endtime - starttime + 2 ; // 2 extra seconds of sleep
		Thread.sleep(waitTime*1000);
		newwaterUsage =sprinklerObject6.getGroupWaterConsumption("North");
		System.out.println("TestCase8: waterUsage after scheduling sprinkler N1 ="+newwaterUsage);
		waterConsumption = Integer.parseInt(newwaterUsage) - Integer.parseInt(oldwaterUsage);
		assertEquals(7,waterConsumption);
	}
}
