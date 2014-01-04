package dataStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class SprinklerFileStore
{
	String CommonFilePath="C:/Users/Devi/Documents/Java/December13/SprinklerSystem/src/dataStore/";
	public SprinklerFileStore() {		
	}

	/**
	 * Function to obtain the weekly water consumption of a zone 
	 * @param key 
	 * @return String Value
	 */
	public String getGroupConsumptionFromFile(String key) {
		FileReader fr;
		String fileLine;
		String words [] = new String [2];
		String value = null;
		try {
			File inputFile = new File(CommonFilePath+ "weeklyWaterConsumption.txt");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));       
			try {
				while ((fileLine = reader.readLine()) != null) {
					String trimmedLine = fileLine.trim();
					if(trimmedLine.startsWith(key)) {	                    
						words = fileLine.split("\t");
						value= words[1];
						break;	                        
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        	            
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return value;
	}


	/**
	 * Function to store daily configuration made to a log file
	 * @param dailyConfigLogLine
	 */	
	public void storeDailyConfiginFileLog(String dailyConfigLogLine)
	{
		File configLogFile = new File(CommonFilePath+"dailyConfigLogFile.txt");
		//BufferedWriter  writer = new BufferedWriter(new FileWriter(configLogFile));
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(configLogFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.write(dailyConfigLogLine+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * Function to store Water consumption in a file log.
	 * @param waterVolume
	 * @param zone
	 * @throws IOException
	 */
	public void storeWaterConsumptioninFile(int waterVolume, String zone) throws IOException 
	{
		FileReader fr;
		String fileLine;
		String words [] = new String [2];		
		try {
			File tempFile = new File(CommonFilePath + "weeklyWaterConsumptiontmp.txt");
			File inputFile = new File(CommonFilePath + "weeklyWaterConsumption.txt");
			File graphFile = new File(CommonFilePath + "graphweeklyWaterConsumption.txt");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter  writer = new BufferedWriter(new FileWriter(tempFile));
			BufferedWriter  writer1 = new BufferedWriter(new FileWriter(graphFile));			
			while ((fileLine = reader.readLine()) != null) {
				String trimmedLine = fileLine.trim();
				if(!trimmedLine.startsWith(zone)) {
					writer.write(fileLine+"\n");
					writer1.write(fileLine+"\n");
				} else
				{
					words = fileLine.split("\t");
					waterVolume = waterVolume + Integer.parseInt(words[1]); 
					System.out.println("@storeWaterConsumptioninFile ::waterVolume: "+ waterVolume);
					fileLine = zone + "\t" + Integer.toString(waterVolume);
					writer.write(fileLine+"\n");
					writer1.write(fileLine+"\n");
				}
			}
			reader.close();
			writer.close();
			writer1.close();
			if(!inputFile.delete())
			{
				return;
			}
			if(!tempFile.renameTo(inputFile))
				System.out.println("WARN : Could not rename the tmp file to weeklyWaterConsumption");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}

	/**
	 * Function to read weekly schedules from a file and process them.
	 */
	public Vector<String> readWeeklyScheduleFromFile()
	{
		String str="";
		String[] words = new String [20];
		String [] timesplit = new String[3];
		int j=0, count=0, i=0, startTime, endTime, sprinklerIndex;
		String DATE_FORMAT_NOW = "yyyy-MM-dd";
		Calendar cal = Calendar.getInstance();
		//SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		SimpleDateFormat f = new SimpleDateFormat("EEEE");
		String day=f.format(cal.getTime());
		Vector<String> todaySchedule = new Vector<String>();
		try {
			FileReader fr = new FileReader(CommonFilePath + "weeklySchedule.txt");
			// Format of this file - Day Group StartTime EndTime  Temperature
			BufferedReader br = new BufferedReader(fr);
			br.readLine();
			br.readLine();
			while ((str = br.readLine()) != null) {
				System.out.println(str);
				words = str.split("\t");
				count = words.length + j;
				if (day.equals(words[0])) {  // Populate SprinklerDaily with configs only for the given day of the week
					todaySchedule.add(str);
				}
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return todaySchedule;
	}

}