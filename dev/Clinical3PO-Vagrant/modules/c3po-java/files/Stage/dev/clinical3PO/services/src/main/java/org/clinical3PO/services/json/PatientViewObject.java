package org.clinical3PO.services.json;

public class PatientViewObject {
	private String name;                // 138319
    private CategoryObject[] children;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CategoryObject[] getChildren() {
		return children;
	}

	public void setChildren(CategoryObject[] children) {
		this.children = children;
	}

    public static class CategoryObject{
    	private String name;			// Category1
    	private ConceptsInCategory[] children;
    	
    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}
    	
    	public ConceptsInCategory[] getChildren() {
    		return children;
    	}

    	public void setChildren(ConceptsInCategory[] children) {
    		this.children = children;
    	}
    }
    
    public static class ConceptsInCategory{
    	private String name;    		// SysABP
    	private TimeValue[] children; 
    	
    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}
    	
    	public TimeValue[] getChildren() {
    		return children;
    	}

    	public void setChildren(TimeValue[] children) {
    		this.children = children;
    	}
    }
    
    public static class TimeValue{
    	private String name;    		//time:12.00,value:10
    	private int size=2000;
    	
    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}
    }
    
    
}
