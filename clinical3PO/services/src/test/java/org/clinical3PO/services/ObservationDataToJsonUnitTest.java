package org.clinical3PO.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import org.clinical3PO.services.json.ObservationDataToJson;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:/clinical3PO-services-test.xml",
        "classpath*:/environment-test.xml"
})

public class ObservationDataToJsonUnitTest {
	
	@Autowired
	ObservationDataToJson generateJSONTest;
	
	@Rule
	  public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void jsonConversionPatientTest() throws IOException {
		File tempFile = tempFolder.newFile("patient");
		String patientContent = "HCT;01/01/14;05:58;25.3;%;";
		
		FileWriter fw = new FileWriter(tempFile.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(patientContent);
		bw.close();
		
		String convJsonIn = tempFile.getAbsolutePath(); 
		String patientID = "139060";
		String jsonBuffer="";

		jsonBuffer = generateJSONTest.generatePatientViewDataJson(convJsonIn, patientID);

		String expectedPatientOutput = "{\"name\":\"139060\",\"children\":[{\"name\":\"Category1\",\"children\":[{\"name\":\"Mg\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Na\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"K\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Glucose\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category2\",\"children\":[{\"name\":\"Urine\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Creatinine\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"BUN\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Albumin\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"ALT\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"AST\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category3\",\"children\":[{\"name\":\"PaCO2\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"PaO2\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"pH\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"HCO3\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Lactate\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category4\",\"children\":[{\"name\":\"MechVent\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"RespRate\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"SaO2\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"FiO2\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category5\",\"children\":[{\"name\":\"HR\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"TropI\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"TropT\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Cholesterol\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category6\",\"children\":[{\"name\":\"GCS\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Temp\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Weight\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category7\",\"children\":[{\"name\":\"Platelets\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"WBC\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"HCT\",\"children\":[{\"name\":\"Time: 5.9666667 hrs, Value: 25.3 %\",\"size\":2000}]},{\"name\":\"ALP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"Bilirubin\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]},{\"name\":\"Category8\",\"children\":[{\"name\":\"SysABP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"NISysABP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"DiasABP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"NIDiasABP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"MAP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]},{\"name\":\"NIMAP\",\"children\":[{\"name\":\"No data for this attribute\",\"size\":2000}]}]}]}";

		Assert.assertEquals(jsonBuffer,expectedPatientOutput);
	}
	
	@Test
	public void jsonConversionObservationTest() throws IOException {
		File tempFile = tempFolder.newFile("observation");
		String observationContent = "138312	01/01/14;06:00;77;bpm;Heart rate;Alive;\n138312	01/01/14;15:00;80;bpm;Heart rate;Alive;\n138319	01/02/14;05:00;70;bpm;Heart rate;Alive;";
		
		FileWriter fw = new FileWriter(tempFile.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(observationContent);
		bw.close();
		
		String convJsonIn = tempFile.getAbsolutePath(); 
		String parameters = "HR~138312,138319";
		String jsonBuffer="";

		jsonBuffer = generateJSONTest.generateObservationDataJSON(convJsonIn, parameters);

		String expectedObservationOutput = "[{\"patient_number\":\"Patient_1\",\"patient_id\":\"138312\",\"attribute\":\"HR\",\"color\":\"114f00\",\"time_series\":[{\"time\":\"6.0\",\"value\":\"77\"},{\"time\":\"15.0\",\"value\":\"80\"}]},{\"patient_number\":\"Patient_2\",\"patient_id\":\"138319\",\"attribute\":\"HR\",\"color\":\"114f00\",\"time_series\":[{\"time\":\"29.0\",\"value\":\"70\"}]}]";

		Assert.assertEquals(expectedObservationOutput,jsonBuffer);
	}
	
	@Test
	public void jsonConversionMultiObservationTest() throws IOException {
		File tempFile = tempFolder.newFile("multiObservation");
		String observationContent = "HCT~135882      01/02/14;16:48;32.6;%;Hematocrit;\nHCT~135882      01/01/14;15:31;32.3;%;Hematocrit;\nHCT~139060      01/01/14;05:58;25.3;%;Hematocrit;\nWBC~135882      01/02/14;16:48;4.7;cells/nL;White blood cell count;\nWBC~135882      01/01/14;15:31;4.5;cells/nL;White blood cell count;";	
		FileWriter fw = new FileWriter(tempFile.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(observationContent);
		bw.close();
		
		String convJsonIn = tempFile.getAbsolutePath(); 
		String jsonParameters = "HCT~135882,139060~ff0000#WBC~135882~0000ff";
		String jsonBuffer="";

		jsonBuffer = generateJSONTest.generateMultiObservationDataJSON(convJsonIn,jsonParameters);

		String expectedObservationOutput = "[{\"patient_number\":\"Patient_1\",\"patient_id\":\"135882\",\"attribute\":\"WBC\",\"color\":\"0000ff\",\"time_series\":[{\"time\":\"15.516666\",\"value\":\"4.5\"},{\"time\":\"40.8\",\"value\":\"4.7\"}]},{\"patient_number\":\"Patient_2\",\"patient_id\":\"135882\",\"attribute\":\"HCT\",\"color\":\"ff0000\",\"time_series\":[{\"time\":\"15.516666\",\"value\":\"32.3\"},{\"time\":\"40.8\",\"value\":\"32.6\"}]},{\"patient_number\":\"Patient_3\",\"patient_id\":\"139060\",\"attribute\":\"HCT\",\"color\":\"ff0000\",\"time_series\":[{\"time\":\"5.9666667\",\"value\":\"25.3\"}]}]";

		Assert.assertEquals(expectedObservationOutput,jsonBuffer);
	}
}
