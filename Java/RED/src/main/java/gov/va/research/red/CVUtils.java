/*
 *  Copyright 2014 United States Department of Veterans Affairs,
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
package gov.va.research.red;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author doug
 *
 */
public class CVUtils {
	/**
	 * @param folds Number of folds (partitions) for cross-validation.
	 * @param snippets The snippets to partition.
	 * @return <code>folds</code> lists of snippets, partitioned evenly.
	 */
	public static List<List<Snippet>> partitionSnippets(int folds,
			List<Snippet> snippets) {
		List<List<Snippet>> partitions = new ArrayList<>(folds);
		for (int i = 0; i < folds; i++) {
			partitions.add(new ArrayList<Snippet>());
		}
		Iterator<Snippet> snippetIter = snippets.iterator();
		int partitionIdx = 0;
		while (snippetIter.hasNext()) {
			if (partitionIdx >= folds) {
				partitionIdx = 0;
			}
			List<Snippet> partition = partitions.get(partitionIdx);
			partition.add(snippetIter.next());
			partitionIdx++;
		}
		return partitions;
	}

	/**
	 * Determines if a collection of strings contains a given string, ignoring case differences.
	 * @param strings a collection of strings
	 * @param string a string
	 * @return <code>true</code> if <code>string</code> is contained in <code>strings</code> where performing a case insentitive comparison.
	 */
	public static boolean containsCI(final Collection<String> strings,
			final String string) {
		for (String s : strings) {
			if (s.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean containsAnyCI(final Collection<String> strings1, final Collection<String> strings2, boolean allowOverMatches) {
		for (String s1 : strings1) {
			String lcs1 = s1.toLowerCase();
			for (String s2 : strings2) {
				String lcs2 = s2.toLowerCase();
				if (allowOverMatches) {
					if (lcs1.contains(lcs2) || lcs2.contains(lcs1)) {
						return true;
					}
				} else {
					if (lcs1.equals(s2)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
