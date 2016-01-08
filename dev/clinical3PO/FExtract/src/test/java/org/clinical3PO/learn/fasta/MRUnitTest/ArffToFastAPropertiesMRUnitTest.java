package org.clinical3PO.learn.fasta.MRUnitTest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.clinical3PO.learn.fasta.ArffToFastAProperties;
import org.junit.Test;

public class ArffToFastAPropertiesMRUnitTest {
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	DataInputStream dis = null;
	boolean pidFlag = false;
	ArffToFastAProperties aftp = new ArffToFastAProperties(pidFlag);
	String path="src/test/resources/fastaDescreteProperties.txt";
	File fs= new File("src/test/resources/fastaDescreteProperties.txt");
	Path propertiesFile = new Path(path);
	
	@Test
	public void ArffpropertiesTest() throws IOException{
		Configuration conf =new Configuration();
		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.jobtracker.address", "local");
		conf.set("c3fe", "src/test/resources/fastaDescreteProperties.txt");
		String propertiesMap= "{diasabp={min=10, max=100, descritize_out=F, descritize_in=E}, creatinine={min=0.7, max=1.3, descritize_out=B, descritize_in=A}, paco2={min=38, max=42, descritize_out=J, descritize_in=I}, nisysabp={min=10, max=100, descritize_out=L, descritize_in=K}, cholesterol={min=4, max=5, descritize_out=H, descritize_in=G}, mg={min=1.5, max=2.0, descritize_out=D, descritize_in=C}}";
		FileSystem localFS = FileSystem.get(propertiesFile.toUri(), conf);
		
		Boolean str= aftp.parsePropertiesFile(localFS.open(propertiesFile));
	
		assertEquals(true,str);
		assertEquals(propertiesMap,aftp.getPropertiesMap().toString());
		System.out.println(aftp.getPropertiesMap());
		assertEquals(pidFlag,aftp.getPidFlag());
		
	}
	
	
}
