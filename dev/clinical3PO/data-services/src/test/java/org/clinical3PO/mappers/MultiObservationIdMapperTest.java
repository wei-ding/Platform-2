package org.clinical3PO.mappers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.clinical3PO.hadoop.mappers.MultiObservationIdMapper;
import org.junit.Before;
import org.junit.Test;

public class MultiObservationIdMapperTest {
	private MapDriver<LongWritable, Text, Text, Text> mapDriver;

	@Before
	public void setUp() {

		// Setup the mapper
		MultiObservationIdMapper multiObservationIdMapper = new MultiObservationIdMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();

		// Setup up the configuration, pass patientid that we are searching
		Configuration conf = mapDriver.getConfiguration();
		conf.set("personId","138312");
		conf.set("observationId", "8,16");
		//conf.set("deathPatients_list","132539,138312");
		//conf.set("inputParam", "Creatinine~132539,132540,132541,132543,132545,132547#BUN~132539,132540,132541,132543,132545,132547#GCS~132539,132540,132541,132543,132545,132547#HCO3~132539,132540,132541,132543,132545,132547#k~132539,132540,132541,132543,132545,132547#Na~132539,132540,132541,132543,132545,132547#Platelets~132539,132540,132541,132543,132545,132547#Temp~132539,132540,132541,132543,132545,132547#WBC~132539,132540,132541,132543,132545,132547");
		//conf.set("observationDetails", "BUN~6~Blood urea nitrogen~mg/dL#Creatinine~8~Serum creatinine~mg/dL#GCS~11~Glasgow Coma Score~No Unit#HCO3~13~Seum bicarbonate~mmol/L#K~16~Serum potassium~mEq/L#Na~21~Serum sodium~mEq/L#Platelets~28~Platelets~cells/nL#Temp~32~Temperature~C#WBC~36~White blood cell count~cells/nL");
		conf.set("inputParam", "Creatinine~132539,132540,132541,132543,138312,132545,132547~ff0000#k~138312,132539~ff0000#");
		conf.set("observationDetails", "Creatinine~8~Serum creatinine~mg/dL#k~16~Serum potassium~mEq/L#");
		//conf.set("deathFile", "DEATH,132539,2013-01-06,43,NULL,NULL");
		conf.set("observation", "Creatinine,k");
				
		mapDriver.setMapper(multiObservationIdMapper);
	}
	@Test
	public void MultiObservationIdRecord() throws IOException {
		String input = "OBSERVATION;2676118;138312;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";	
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		input="OBSERVATION;2676118;138312;8;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		mapDriver.withInput(new LongWritable(1234567),new Text(input));
		mapDriver.withOutput(new Text("k~138312"), new Text("01/01/14;06:08;4.9;"));
		mapDriver.withOutput(new Text("Creatinine~138312"), new Text("01/01/14;06:08;4.9;"));
		
		mapDriver.runTest();
	}
	/*@Test
	public void DeadMultiObservationIdRecord() throws IOException {
		String input = "OBSERVATION;2676118;132539;8;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		mapDriver.withOutput(new Text("Creatinine~132539"), new Text("01/01/14;06:08;4.9;"));
		
		mapDriver.runTest();
	}*/
	@Test
	public void InvalidMultiObservationIdRecord() throws IOException {
		String input = "OBSERVATION;2676118;1385612;8;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		//mapDriver.withOutput(new Text("Creatinine~138312"), new Text("01/01/14;06:08;4.9;Alive;"));
		
		mapDriver.runTest();
	}
}
