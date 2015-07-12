package org.clinical3PO.common.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;



public class CrossValidationForm {
		
	private String classificationAlgorithm;	
	
	@NotNull @Min(1)
	private Integer folds;
	
	@NotNull @Min(1)
	private Integer numberOfIterations;

	public String getClassificationAlgorithm() {
		return classificationAlgorithm;
	}

	public void setClassificationAlgorithm(String classificationAlgorithm) {
		this.classificationAlgorithm = classificationAlgorithm;
	}

	public Integer getFolds() {
		return folds;
	}

	public void setFolds(Integer folds) {
		this.folds = folds;
	}

	public Integer getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(Integer numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

}
