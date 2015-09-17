/*
 *  Copyright 2015 United States Department of Veterans Affairs,
 *		Health Services Research & Development Service
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package gov.va.research.red.ex.hadoop;

import gov.va.research.red.MatchedElement;
import gov.va.research.red.ex.REDExtractor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author doug
 *
 */
public class REDExMapper extends Mapper<Text, Text, Text, MatchedElementWritable> {

	private Configuration conf;
	private REDExtractor rex;

	@Override
	protected void setup(
			Mapper<Text, Text, Text, MatchedElementWritable>.Context context)
			throws IOException, InterruptedException {
		super.setup(context);
		conf = context.getConfiguration();
		String redexModelFilename = conf.get("redex.model.file");
		FileSystem fs = FileSystem.get(conf);
		Path redexModelPath = new Path(redexModelFilename);
		try (Reader r = new InputStreamReader(fs.open(redexModelPath))) {
			rex = REDExtractor.load(r);			
		}
	}

	@Override
	protected void map(
			Text key,
			Text value,
			Mapper<Text, Text, Text, MatchedElementWritable>.Context context)
			throws IOException, InterruptedException {
		List<MatchedElement> matches = rex.extract(value.toString());
		if (matches != null) {
			for (MatchedElement me : matches) {
				context.write(key, new MatchedElementWritable(me));
			}
		}
	}

	@Override
	protected void cleanup(
			Mapper<Text, Text, Text, MatchedElementWritable>.Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
	}
	
	
}
