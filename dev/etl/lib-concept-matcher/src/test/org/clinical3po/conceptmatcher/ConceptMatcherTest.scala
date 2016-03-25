/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.clinical3po.conceptmatcher

import org.junit.Test
import java.nio.file.Path 
import java.nio.file.Paths
import org.junit.Assert

class ConceptMatcherTest {

  @Test
  def testSortWords(): Unit = {
    val s = "heart attack and diabetes"
    val matcher = new NameMatcher()
    Assert.assertEquals("and attack diabetes heart", matcher.sortWords(s))
  }
  
  @Test
  def testStemWords(): Unit = {
    val s = "and attack diabetes heart"
    val matcher = new NameMatcher()
    Assert.assertEquals("attack diabetes heart", matcher.stemWords(s))
  }

  @Test
  def testBuild(): Unit = {
    val input = new File("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.csv")
    val output = Paths.get("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.index")
    val matcher = new NameMatcher()
    matcher.buildIndex(input, output)
  }
  
  @Test
  def testMapSingleConcept(): Unit = {
    val luceneDir = Paths.get("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.index")
    val matcher = new NameMatcher()
    val strs = List("Lung Cancer", "Heart Attack", "Diabetes")
    strs.foreach(str => {
      val concepts = matcher.annotateConcepts(str, luceneDir)
      Console.println("Query: " + str)
      matcher.printConcepts(concepts)
      Assert.assertEquals(1, concepts.size)
      Assert.assertEquals(100.0D, concepts.head._1, 0.1D)
    })
  }

  @Test
  def testMapMultipleConcepts(): Unit = {
    val luceneDir = Paths.get("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.index")
    val matcher = new NameMatcher()
    val strs = List(
        "Heart Attack and diabetes",
        "carcinoma (small-cell) of lung",
        "asthma side effects")
    strs.foreach(str => {
      val concepts = matcher.annotateConcepts(str, luceneDir)
      Console.println("Query: " + str)
      matcher.printConcepts(concepts)
    })
  }
}
