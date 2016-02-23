package org.clinical3PO.common.form.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.math.NumberUtils;

public class ObservationPatientIdValidator implements
		ConstraintValidator<ObservationPatientId, String> {

	@Override
	public boolean isValid(String patientIds, ConstraintValidatorContext ctx) {

		if (patientIds == null)
			return false;
		
		if (!patientIds.contains(",")) {
			if (!NumberUtils.isNumber(patientIds)) {
				return false;
			} 
			return true;
			
		} else {
			
			// Split and find the individuals patient ids
			String[] str = patientIds.split(",");
			
			for (String num : str) {

				if (num == null) {
					return false;
				}

				if (!NumberUtils.isNumber(num)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void initialize(ObservationPatientId param) {
	}

}
