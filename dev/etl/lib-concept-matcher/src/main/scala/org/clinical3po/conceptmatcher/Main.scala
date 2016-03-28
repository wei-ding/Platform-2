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
import org.apache.lucene.document.Document

import java.io.StringReader
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern

import scala.Array.canBuildFrom
import scala.Array.fallbackCanBuildFrom
import scala.collection.mutable.ArrayBuffer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.document.Document
import LuceneFieldHelpers._
import LuceneText._
import java.util.Calendar

object Main {
  val punctPattern = Pattern.compile("\\p{Punct}")
  val spacePattern = Pattern.compile("\\s+")
  

  def normalizeCasePunct(str: String): String = {
    val str_lps = punctPattern
      .matcher(str.toLowerCase())
      .replaceAll(" ")
    spacePattern.matcher(str_lps).replaceAll(" ")
  }

  def sortWords(str: String): String = {
    val words = str.split(" ")
    words.sortWith(_ < _).mkString(" ")
  }
  
  def stemWords(str: String): String = {
    val stemmedWords = ArrayBuffer[String]()
    val tokenStream = new StandardAnalyzer().tokenStream(
      "str_stemmed", new StringReader(str))
    val ctattr = tokenStream.addAttribute(
      classOf[CharTermAttribute])    
      tokenStream.reset()
    while (tokenStream.incrementToken()) {
      stemmedWords += ctattr.toString()
    }
    stemmedWords.mkString(" ")
  }
  
  def testSortWords(): Unit = {
    val s = "heart attack and diabetes"
    val matcher = new ConceptMatcher()
    Assert.assertEquals("and attack diabetes heart", matcher.sortWords(s))
  }
  
  def testStemWords(): Unit = {
    val s = "and attack diabetes heart"
    val matcher = new ConceptMatcher()
    Assert.assertEquals("attack diabetes heart", matcher.stemWords(s))
  }

  def buildIndex1(inputFile: JFile, 
      luceneDir: Path): Unit = {
    // read through input file and write out to lucene
    val index = new ReadableLuceneIndex
        with WritableLuceneIndex
        with LuceneStandardAnalyzer 
        with DefaultFSLuceneDirectory 

    println(inputFile)
    val counter = new AtomicLong(0L)
    val linesReadCounter = new AtomicLong(0L)
    Source.fromFile(inputFile)
        .getLines()
        .foreach(line => {
          val linesRead = linesReadCounter.incrementAndGet()
          println("%d lines read".format(linesRead))
          if (linesRead % 1000 == 0) println("%d lines read".format(linesRead))
            val Array(conceptid, conceptname,conceptcode, conceptclass, conceptlevel, vocabularyid,vocabularyname) = line
              .replace("\",\"", "\t")
              .replaceAll("\"", "")
              .split("\t")
        val strNorm = normalizeCasePunct(conceptname)
        //println("%s %s".format("normalizeCasePunct",Calendar.getInstance.getTime))
        val strSorted = sortWords(strNorm)
        //println("%s %s".format("sortwords",Calendar.getInstance.getTime))
        val strStemmed = stemWords(strNorm)
        //println("%s %s".format("stemWords",Calendar.getInstance.getTime))
        val fid = counter.incrementAndGet()
        //println("%s %s".format("fid",Calendar.getInstance.getTime))
        val fdoc = new Document
        //println("%s %s".format("new fdoc",Calendar.getInstance.getTime))
        fdoc.addIndexedStoredField("id", fid.toString)
        //println("%s %s".format("addIdx_id",Calendar.getInstance.getTime))
        fdoc.addIndexedStoredField("conceptid", conceptid)
        //println("%s %s".format("addIdx_conceptid",Calendar.getInstance.getTime))
        fdoc.addIndexedStoredField("conceptname", conceptname.toLuceneText)
        //println("%s %s".format("addIdx_conceptname",Calendar.getInstance.getTime))
        //fdoc.addIndexedStoredField("conceptname_norm", strNorm)
        //fdoc.addIndexedStoredField("conceptname_sorted", strSorted)
        //fdoc.addIndexedStoredField("conceptname_stemmed", strStemmed)
        //fdoc.addIndexedOnlyField("optional_int", Option(42))
        //fdoc.addStoredOnlyField("long_value", 12345678L)
        index.addDocument(fdoc)
        //println("%s %s".format("addDoc",Calendar.getInstance.getTime))
  
        if (fid % 1000 == 0) index.commit()
    })
  }

  def testBuild(): Unit = {
    val url= getClass.getResource("/Observation.csv")
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

    buildIndex1(input, output)
  }
  
  def testMapSingleConcept_scala(): Unit = {
    val index = new ReadableLuceneIndex
        with WritableLuceneIndex
        with LuceneStandardAnalyzer 
        with DefaultFSLuceneDirectory 

    val strs = List("10-HYDROXYCARBAZEPINE [MOLES/VOLUME] IN SERUM OR PLASMA", "BP")
    strs.foreach(str => {
        val queryParser = index.queryParserForDefaultField("conceptname")
        val query = queryParser.parse(str)
        val results = index.searchTopDocuments(query, 1)
        println("Query: " + str)
        println("Result: " + results)
        //Assert.assertEquals(1, concepts.size)
        //Assert.assertEquals(100.0D, concepts.head._1, 0.1D)
    })
  }

  def testMapSingleConcept_java(): Unit = {
    lazy val INDEX_DIRECTORY = "index"
    lazy val serviceRootPath = System.getProperty("user.dir")
    lazy val indexPath = new JFile(serviceRootPath, INDEX_DIRECTORY).getAbsolutePath
    val luceneDir = Paths.get(indexPath)
    val tagger = new ConceptMatcher()
    val strs = List("10-HYDROXYCARBAZEPINE [MOLES/VOLUME] IN SERUM OR PLASMA", "BP")
        strs.foreach(str => {
        val concepts = tagger.annotateConcepts(str, luceneDir)
        println("Query: " + str)
        tagger.printConcepts(concepts)
        Assert.assertEquals(1, concepts.size)
        Assert.assertEquals(100.0D, concepts.head._1, 0.1D)
        })
  }

  def testMapMultipleConcepts(): Unit = {
    val luceneDir = Paths.get("%s%s".format(getClass.getResource(""),"/lab_dict.index"))
    val matcher = new ConceptMatcher()
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
    //testSortWords()
    //testStemWords()
    testBuild()
    testMapSingleConcept_scala()
    //testMapMultipleConcepts()

  }
}
