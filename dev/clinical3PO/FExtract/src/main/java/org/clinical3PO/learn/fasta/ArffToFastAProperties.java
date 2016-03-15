package org.clinical3PO.learn.fasta;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class ArffToFastAProperties {

	private boolean pidFlag = false;
	private Map<String, HashMap<String, String>> propertiesMap = null; 

	public ArffToFastAProperties() {}
	public ArffToFastAProperties(boolean pidFlag) {
		this.pidFlag = pidFlag;
		propertiesMap = new HashMap<String, HashMap<String,String>>();
	}

	/**
	 * Method reads and load the properties file(xml format) into map.
	 * 
	 * @param propsFile
	 * @return boolean
	 */
	private boolean loadProperties(FSDataInputStream propsFile) {

		boolean flag = true;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(propsFile);

			NodeList nodeList = document.getDocumentElement().getChildNodes();

			int count = nodeList.getLength();
			for(int i = 0; i < count; i++) {

				Node node = nodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) node;
					String attribute = node.getAttributes().getNamedItem("name").getNodeValue();
					propertiesMap.put(attribute, new HashMap<String, String>());
					propertiesMap.get(attribute).put("min", eElement.getElementsByTagName("min").item(0).getTextContent());
					propertiesMap.get(attribute).put("max", eElement.getElementsByTagName("max").item(0).getTextContent());
					propertiesMap.get(attribute).put("descritize_in", eElement.getElementsByTagName("descritize_in").item(0).getTextContent());
					propertiesMap.get(attribute).put("descritize_out", eElement.getElementsByTagName("descritize_out").item(0).getTextContent());
				}
			}
			propertiesMap = Collections.unmodifiableMap(propertiesMap);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.print("Properties are not loaded properly" +e);
			flag = false;
		}
		return flag; 
	}

	public boolean parsePropertiesFile(FSDataInputStream file) {
		return loadProperties(file);
	}

	public Map<String, HashMap<String, String>> getPropertiesMap() {
		return propertiesMap;
	}

	public boolean getPidFlag() {
		return pidFlag;
	}
}
