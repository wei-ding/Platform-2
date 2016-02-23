package org.clinical3PO.learn.main;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Partitioner class
 * Decides which record is to be sent to n-reducer.
 * @author 3129891
 *
 */
public class FECorePartitioner extends Partitioner<FEDataObjects, NullWritable>{

	@Override
	public int getPartition(FEDataObjects key, NullWritable value, int numPartitions) {
		return ((key.getKey().hashCode() & Integer.MAX_VALUE) % numPartitions);
	}	
}
