package org.clinical3PO.environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppUtil {

	private static Properties  _instance = null;

	private AppUtil() {
	}

	private synchronized static Properties getProperties() {
		InputStream input = null;

		try {

			String filename = "environment.properties";
			input = AppUtil.class.getClassLoader().getResourceAsStream(filename);
			if(input==null){
				System.out.println("Properties file "+filename+" Missing");
				return _instance;
			}

			_instance = new Properties();
			_instance.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return _instance;
	}

	public static String getProperty(String property) {
		Properties _local = getProperties();
		return(_local != null ? _local.getProperty(property):null);
	}
}