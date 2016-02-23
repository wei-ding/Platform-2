package org.clinical3PO.learn.main;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * Comparator Class
 * This class helps to sort keys and its corresponding values from low to high.
 * @author 3129891
 *
 */
public class FESortComparator extends WritableComparator {
	
	protected FESortComparator() {
		super(FEDataObjects.class, true);
	}
	
	@Override
	public int compare(WritableComparable o1, WritableComparable o2) {
		
		FEDataObjects fe1 = (FEDataObjects) o1;
		FEDataObjects fe2 = (FEDataObjects) o2;
		
		int result = fe1.getKey().compareTo(fe2.getKey());	// check if the keys are same/low/high with the other
		if(0 == result) {		// if they are equal then check if the values are same/high/low among the same keys.
			result = fe1.getValue().compareTo(fe2.getValue());
		}
		return result;
	}
}
