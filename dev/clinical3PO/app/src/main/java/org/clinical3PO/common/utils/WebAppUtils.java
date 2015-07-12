package org.clinical3PO.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class WebAppUtils {
	public static Map<String,String> getObservationIds() {
		
		Map<String,String> observationIdsMap = new LinkedHashMap<String,String>();
		
		observationIdsMap.put("Albumin","Albumin");
		observationIdsMap.put("ALP","Alkaline phosphatase");
		observationIdsMap.put("ALT","Alanine transaminase");
		observationIdsMap.put("AST","Aspartate transaminase");
		observationIdsMap.put("Bilirubin","Bilirubin");
		observationIdsMap.put("BUN","Blood urea nitrogen");
		observationIdsMap.put("Cholesterol","Cholesterol");
		observationIdsMap.put("Creatinine","Serum creatinine");
		observationIdsMap.put("DiasABP","Invasive diastolic arterial blood pressure");
		observationIdsMap.put("FiO2","Fractional inspired O2");
		observationIdsMap.put("GCS","Glasgow Coma Score");
		observationIdsMap.put("Glucose","Serum glucose");
		observationIdsMap.put("HCO3","Seum bicarbonate");
		observationIdsMap.put("HCT","Hematocrit");
		observationIdsMap.put("HR","Heart Rate");
		observationIdsMap.put("K","Serum potassium");
		observationIdsMap.put("Lactate","Lactate");
		observationIdsMap.put("Mg","Serum magnesium");
		observationIdsMap.put("MAP","Invasive mean arterial blood pressure");
		observationIdsMap.put("MechVent","Mechanical ventilation respiration");
		observationIdsMap.put("Na","Serum sodium");
		observationIdsMap.put("NIDiasABP","Non-invasive diastolic arterial blood pressure");
		observationIdsMap.put("NIMAP","Non-invasive mean arterial blood pressure");
		observationIdsMap.put("NISysABP","Non-invasive systolic arterial blood pressure");
		observationIdsMap.put("PaCO2","Partial pressure of arterial CO2");
		observationIdsMap.put("PaO2","Partial pressure of arterial O2");
		observationIdsMap.put("pH","Arterial pH");
		observationIdsMap.put("Platelets","Platelets");
		observationIdsMap.put("RespRate","Respiration rate");
		observationIdsMap.put("SaO2","O2 saturation in hemoglobin");
		observationIdsMap.put("SysABP","Invasive systolic arterial blood pressure");
		observationIdsMap.put("Temp","Temperature");
		observationIdsMap.put("TropI","Troponin-I");
		observationIdsMap.put("TropT","Troponin-T");
		observationIdsMap.put("Urine","Urine");
		observationIdsMap.put("WBC","White blood cell count");
		observationIdsMap.put("Weight","Weight");
		observationIdsMap.put("Height","Height");
		
		return observationIdsMap;
	}
}
