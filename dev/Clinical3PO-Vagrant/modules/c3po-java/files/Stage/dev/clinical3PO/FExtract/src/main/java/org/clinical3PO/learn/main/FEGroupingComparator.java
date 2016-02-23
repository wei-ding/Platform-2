package org.clinical3PO.learn.main;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * GroupComparator class
 * This class helps to group a unique set of sorted keys & values.
 * @author 3129891
 *
 */
public class FEGroupingComparator extends WritableComparator {

	protected FEGroupingComparator() {
		super(FEDataObjects.class, true);
	}

	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		FEDataObjects key1 = (FEDataObjects) w1;
		FEDataObjects key2 = (FEDataObjects) w2;
		return key1.getKey().compareTo(key2.getKey());
	}
}
