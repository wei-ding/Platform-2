package org.clinical3PO.common.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class FEMlFlexForm {

	private String classproperty;
	
	private String classificationAlgorithm;	
	
	@NotNull @Min(1)
	private Integer folds;
	
	@NotNull @Min(1)
	private Integer numberOfIterations;
	
	private String classTime;
	private String startTime;
	private String endTime;

	public String getClassProperty() {
		return classproperty;
	}

	public void setClassProperty(String classproperty) {
		this.classproperty = classproperty;
	}
	
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

	public String getClassTime() {
		return classTime;
	}

	public void setClassTime(String classTime) {
		this.classTime = classTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
