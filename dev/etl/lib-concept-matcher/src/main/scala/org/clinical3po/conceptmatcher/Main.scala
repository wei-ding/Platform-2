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

import java.nio.file.Path 
import java.nio.file.Paths
import java.io.{File => JFile, FileSystem => JFileSystem, _} //TODO: Scala 2.10 does not like java.io._
import org.junit.Assert
import scala.io.Source

object Main {
  def testSortWords(): Unit = {
    val s = "heart attack and diabetes"
    val matcher = new NameMatcher()
    Assert.assertEquals("and attack diabetes heart", matcher.sortWords(s))
  }
  
  def testStemWords(): Unit = {
    val s = "and attack diabetes heart"
    val matcher = new NameMatcher()
    Assert.assertEquals("attack diabetes heart", matcher.stemWords(s))
  }

  def testBuild(): Unit = {
    val url= getClass.getResource("/lab_dict.csv")
    val input = new JFile(url.getPath())

    lazy val INDEX_DIRECTORY = "index"
    lazy val serviceRootPath = System.getProperty("user.dir")
    lazy val indexPath = new JFile(serviceRootPath, INDEX_DIRECTORY).getAbsolutePath

    val indexFilePath = new JFile(indexPath)
    try{    
        indexFilePath.mkdirs() // make sure path exist
    } catch {
        case ioe: IOException =>
            val sw = new StringWriter
            ioe.printStackTrace(new PrintWriter(sw))
            println(sw.toString)
        case e: Exception => 
            // TODO Auto-generated catch block
            e.printStackTrace();
    }

    val output = Paths.get(indexFilePath.getAbsolutePath())
    val matcher = new NameMatcher()
    matcher.buildIndex(input, output)

  }
  
  def testMapSingleConcept(): Unit = {
    lazy val INDEX_DIRECTORY = "index"
    lazy val serviceRootPath = System.getProperty("user.dir")
    val indexPath = new JFile(serviceRootPath, INDEX_DIRECTORY).getAbsolutePath
    val luceneDir = Paths.get(indexPath)
    val tagger = new NameMatcher()
    val strs = List("10-HYDROXYCARBAZEPINE [MOLES/VOLUME] IN SERUM OR PLASMA", "BP")
        strs.foreach(str => {
        val concepts = tagger.annotateConcepts(str, luceneDir)
        println("Query: " + str)
        if (concepts.length > 0) {
            tagger.printConcepts(concepts)
        }
        //Assert.assertEquals(1, concepts.size)
        //Assert.assertEquals(100.0D, concepts.head._1, 0.1D)
        })
  }

  def testMapMultipleConcepts(): Unit = {
    val luceneDir = Paths.get("%s%s".format(getClass.getResource(""),"/lab_dict.index"))
    val matcher = new NameMatcher()
    val strs = List(
        "Heart Attack and diabetes",
        "carcinoma (small-cell) of lung",
        "asthma side effects")
    strs.foreach(str => {
      val concepts = matcher.annotateConcepts(str, luceneDir)
      println("Query: " + str)
      matcher.printConcepts(concepts)
    })
  }

  def main(args: Array[String]) {
    testSortWords()
    testStemWords()
    testBuild()
    testMapSingleConcept()
    //testMapMultipleConcepts()

  }
}
