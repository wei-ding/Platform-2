package org.clinical3PO.services.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.services.json.PatientViewObject.CategoryObject;
import org.clinical3PO.services.json.PatientViewObject.ConceptsInCategory;
import org.clinical3PO.services.json.PatientViewObject.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;


public class ObservationDataToJson {

	private static final Logger logger = LoggerFactory.getLogger(ObservationDataToJson.class);

	@Autowired
	private EnvironmentType envType;
	private String category;
	Map<String,List<String>> categoryAndConceptsRaw = new HashMap<String,List<String>>();
	private String colorOfDeath, colorOfSurvival, colorOfMissingData;

	public ObservationDataToJson(String category, String c1, String c2, String c3) {

		colorOfDeath = c1;
		colorOfSurvival = c2;
		colorOfMissingData = c3;

		BufferedReader reader=null;

		try {
			reader = new BufferedReader(new FileReader(category));

			String line;
			while ((line = reader.readLine()) != null){
				line = line.replaceAll("\\s","");
				line = line.replaceAll("\\[", "");
				line = line.replaceAll("\\]", "");
				String[] parts = line.split("=");
				String conceptKey = parts[0];
				String[] conceptValue = parts[1].split(",");
				categoryAndConceptsRaw.put(conceptKey, Arrays.asList(conceptValue));				
			}
		}
		catch (IOException e) {
			logger.error(e.toString());
		}
		finally{
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
	}

	public String generateObservationDataJSON (String fileName, String jsonParameters){


		File file = new File(fileName);

		ArrayList<String> listOfPatients2 = new ArrayList<String>();
		ArrayList<String> listOfObservations2 = new ArrayList<String>();
		ArrayList<String> listOfColors2 = new ArrayList<String>();
		ArrayList<ArrayList<TimeSeries>> timeSeriesObjectsAll = new ArrayList<ArrayList<TimeSeries>>();

		Map<String,List<String>> queryTimeSeriesMap = new HashMap<String,List<String>>();
		Map<String,String> patientColorMap = new HashMap<String,String>();

		final String observationID = jsonParameters.split("~")[0];
		String[] patientIDs = jsonParameters.split("~")[1].split(",");

		for (int i=0; i<patientIDs.length; i++){
			List<String> empty = new ArrayList<String>();			
			queryTimeSeriesMap.put(patientIDs[i]+";"+observationID, empty);
		}

		BufferedReader reader=null;
		try {
			long startTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse("01/01/14;00:00").getTime();         // Starting time = 01/01/14;00:00
			reader = new BufferedReader(new FileReader(file));

			String line;
			while ((line = reader.readLine()) != null) {
				String queryTimeSeriesKey = line.split("\\s+")[0]+";"+observationID;    
				String queryValue = (line.split(";")[2]);
				//			String queryUnits = (line.split(";")[3]);

				String dateAndTime = (line.split("\\s+")[1]).split(";")[0]+";"+line.split(";")[1];
				long currentTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse(dateAndTime).getTime();
				float diffTimeInHours = (float) (currentTime-startTime)/(1000*60*60);
				String timeInHrs = Float.toString(diffTimeInHours);

				String queryTimeSeriesValue = timeInHrs+";"+queryValue;	
				List<String> temp = new ArrayList<String>();

				if (queryTimeSeriesMap.containsKey(queryTimeSeriesKey)){
					temp = queryTimeSeriesMap.get(queryTimeSeriesKey);
					temp.add(queryTimeSeriesValue);

				}
				else{
					temp.add(queryTimeSeriesValue);
				}
				queryTimeSeriesMap.put(queryTimeSeriesKey, temp);
				patientColorMap.put(queryTimeSeriesKey, line.split(";")[5]);
			}

			reader.close();

			for (String keys:queryTimeSeriesMap.keySet()){
				String[] parts = keys.split(";");
				listOfPatients2.add(parts[0]);
				listOfObservations2.add(parts[1]);
				if (patientColorMap.containsKey(keys)){
					listOfColors2.add(patientColorMap.get(keys));
				}
				else{
					listOfColors2.add(colorOfMissingData); 						// Patient data not found
					logger.info("Patient data not found for "+keys);
				}

				ArrayList<TimeSeries> timeSeriesObjectsSingle = new ArrayList<TimeSeries>();
				List<String> timeSeriesSingleString = queryTimeSeriesMap.get(keys);
				for (String time:timeSeriesSingleString){
					TimeSeries individualTimeSeries = new TimeSeries();
					individualTimeSeries.setTime(time.split(";")[0]);
					individualTimeSeries.setValue(time.split(";")[1]);
					timeSeriesObjectsSingle.add(individualTimeSeries);

				}
				Collections.sort(timeSeriesObjectsSingle);
				timeSeriesObjectsAll.add(timeSeriesObjectsSingle);
			}
			int colorCounter=0;
			for (String colors:listOfColors2){
				if (colors.equals("Death")){
					listOfColors2.set(colorCounter, colorOfDeath);
				}
				if (colors.equals("Alive")){
					listOfColors2.set(colorCounter, colorOfSurvival);
				}
				colorCounter++;
			}

			return getJsonData(listOfPatients2, listOfObservations2, listOfColors2, timeSeriesObjectsAll);
		} 

		catch (IOException e) {			
			logger.error(e.toString());
		} catch (ParseException e) {			
			logger.error(e.toString());
		}
		finally{
			try {
				reader.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		return "exception";
	}

	public String generateMultiObservationDataJSON (String fileName, String jsonParameters){

		File file = new File(fileName);

		ArrayList<String> listOfPatients2 = new ArrayList<String>();
		ArrayList<String> listOfObservations2 = new ArrayList<String>();
		ArrayList<String> listOfColors2 = new ArrayList<String>();
		ArrayList<ArrayList<TimeSeries>> timeSeriesObjectsAll = new ArrayList<ArrayList<TimeSeries>>();

		Map<String,List<String>> queryTimeSeriesMap = new HashMap<String,List<String>>();    //Will fail if same patient and observation are queried for different colors

		//Populate keys from JSON parameters

		Map<String,String> patientColorMap = new HashMap<String,String>();
		String[] queryParameters = jsonParameters.split("#");

		for (int i=0; i<queryParameters.length; i++){
			String[] parts = queryParameters[i].split("~");
			String[] patients = parts[1].split(",");
			for (int j=0; j<patients.length; j++){
				patientColorMap.put(patients[j]+";"+parts[0], parts[2]);
				List<String> empty = new ArrayList<String>();
				queryTimeSeriesMap.put(patients[j]+";"+parts[0], empty);
			}			
		}
		for (String keys:queryTimeSeriesMap.keySet()){
			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
				logger.debug("Query Keys: " + keys);
			}

		}

		BufferedReader reader = null;
		try {
			long startTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse("01/01/14;00:00").getTime();			// Starting date = 1st Jan 2014 for all patients
			reader = new BufferedReader(new FileReader(file));

			String line;
			while ((line = reader.readLine()) != null) {
				String queryTimeSeriesKey = line.split("\\s+")[0].split("~")[1]+";"+line.split("\\s+")[0].split("~")[0];    // only change b/w multi and single observation
				String queryValue = (line.split(";")[2]);
				//String queryUnits = (line.split(";")[3]);

				String dateAndTime = (line.split("\\s+")[1]).split(";")[0]+";"+line.split(";")[1];
				long currentTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse(dateAndTime).getTime();
				float diffTimeInHours = (float) (currentTime-startTime)/(1000*60*60);
				String timeInHrs = Float.toString(diffTimeInHours);

				String queryTimeSeriesValue = timeInHrs+";"+queryValue;	
				List<String> temp = new ArrayList<String>();

				if (queryTimeSeriesMap.containsKey(queryTimeSeriesKey)){
					temp = queryTimeSeriesMap.get(queryTimeSeriesKey);
					temp.add(queryTimeSeriesValue);

				}
				else{
					temp.add(queryTimeSeriesValue);
				}
				queryTimeSeriesMap.put(queryTimeSeriesKey, temp);
			}

			reader.close();

			for (String keys:queryTimeSeriesMap.keySet()){
				String[] parts = keys.split(";");
				listOfPatients2.add(parts[0]);
				listOfObservations2.add(parts[1]);
				listOfColors2.add(patientColorMap.get(keys));

				ArrayList<TimeSeries> timeSeriesObjectsSingle = new ArrayList<TimeSeries>();
				List<String> timeSeriesSingleString = queryTimeSeriesMap.get(keys);
				for (String time:timeSeriesSingleString){
					TimeSeries individualTimeSeries = new TimeSeries();
					individualTimeSeries.setTime(time.split(";")[0]);
					individualTimeSeries.setValue(time.split(";")[1]);
					timeSeriesObjectsSingle.add(individualTimeSeries);

				}
				Collections.sort(timeSeriesObjectsSingle);
				timeSeriesObjectsAll.add(timeSeriesObjectsSingle);
			}


			String jsonOut2 = getJsonData(listOfPatients2, listOfObservations2, listOfColors2, timeSeriesObjectsAll);

			return jsonOut2;
		}

		catch (IOException e) {
			logger.error(e.toString());			
		} catch (ParseException e) {
			logger.error(e.toString());			
		}
		finally{
			try {
				reader.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		return "exception";	
	}

	private String getJsonData(ArrayList<String> patients, ArrayList<String> observations, ArrayList<String> colors, ArrayList<ArrayList<TimeSeries>> listOfTSData){

		ObservationDataObject[] completeData = new ObservationDataObject[patients.size()];
		int j;
		for (int i=0;i<patients.size();i++){
			ObservationDataObject data=new ObservationDataObject();
			j=i+1;
			data.setPatient_number("Patient_"+j);
			data.setPatient_id(patients.get(i));
			data.setAttribute(observations.get(i));
			data.setColor(colors.get(i));

			ArrayList<TimeSeries> tsList = listOfTSData.get(i);
			TimeSeries[] ts = tsList.toArray(new TimeSeries[tsList.size()]);			
			data.setTimeSeries(ts);		

			completeData[i]=data;
		}

		Gson gson = new Gson();  
		String jsonData = gson.toJson(completeData);		    

		return jsonData;

	}

	public String generatePatientViewDataJson (String fileName, String patientID){

		Map<String,List<String>> conceptAndTimeValues = new HashMap<String,List<String>>();
		Map<String,List<String>> categoryAndConcepts = new TreeMap<String,List<String>>();
		categoryAndConcepts = new TreeMap<String,List<String>>(categoryAndConceptsRaw);

		BufferedReader reader1=null;
		try {
			reader1 = new BufferedReader(new FileReader(fileName));

			String line;
			long startTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse("01/01/14;00:00").getTime();

			while ((line = reader1.readLine()) != null){
				line = line.replaceAll("\\s","");
				String[] parts = line.split(";");
				List<String> temp = new ArrayList<String>();

				long currentTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse(parts[1]+";"+parts[2]).getTime();
				float diffTimeInHours = (float) (currentTime-startTime)/(1000*60*60);

				if(!conceptAndTimeValues.containsKey(parts[0])){
					temp.add("Time: "+Float.toString(diffTimeInHours)+" hrs,"+" Value: "+parts[3]+" "+parts[4]);					
				}
				else{
					temp = conceptAndTimeValues.get(parts[0]);
					temp.add("Time: "+Float.toString(diffTimeInHours)+" hrs,"+" Value: "+parts[3]+" "+parts[4]);					
				}
				conceptAndTimeValues.put(parts[0], temp);
			}
		}
		catch (IOException e) {
			logger.error(e.toString());
		} catch (ParseException e) {
			logger.error(e.toString());
		}
		finally{
			try {
				reader1.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}

		PatientViewObject patientView = new PatientViewObject();
		patientView.setName(patientID);

		List<CategoryObject> category = new ArrayList<CategoryObject>();


		for(String conceptKey:categoryAndConcepts.keySet()){
			CategoryObject categoryLocal = new CategoryObject();			
			categoryLocal.setName(conceptKey);		

			List<ConceptsInCategory> concepts = new ArrayList<ConceptsInCategory>();

			for (String conceptValue:categoryAndConcepts.get(conceptKey)){
				ConceptsInCategory conceptsInCategory = new ConceptsInCategory();
				conceptsInCategory.setName(conceptValue);

				List<TimeValue> timeValues = new ArrayList<TimeValue>();

				if (conceptAndTimeValues.get(conceptValue) != null){


					for (String time:conceptAndTimeValues.get(conceptValue)){
						TimeValue timeValue = new TimeValue();
						timeValue.setName(time);
						timeValues.add(timeValue);
					}
				}
				else{
					TimeValue timeValue = new TimeValue();
					timeValue.setName("No data for this attribute");
					timeValues.add(timeValue);
				}
				conceptsInCategory.setChildren(timeValues.toArray(new TimeValue[timeValues.size()]));
				concepts.add(conceptsInCategory);
			}

			categoryLocal.setChildren(concepts.toArray(new ConceptsInCategory[concepts.size()]));

			category.add(categoryLocal);
		}

		patientView.setChildren(category.toArray(new CategoryObject[category.size()]));
		Gson gson = new Gson();  
		String jsonData = gson.toJson(patientView);


		return jsonData;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
