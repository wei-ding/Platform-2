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

package org.clinical3po.namematcher

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
import LuceneFieldHelpers._
import LuceneText._

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
    val matcher = new NameMatcher()
    Assert.assertEquals("and attack diabetes heart", matcher.sortWords(s))
  }
  
  def testStemWords(): Unit = {
    val s = "and attack diabetes heart"
    val matcher = new NameMatcher()
    Assert.assertEquals("attack diabetes heart", matcher.stemWords(s))
  }

  def buildIndex1(inputFile: JFile, 
      luceneDir: Path): Unit = {
    // read through input file and write out to lucene
    val index = new ReadableLuceneIndex
        with WritableLuceneIndex
        with LuceneStandardAnalyzer 
        with DefaultFSLuceneDirectory 

    val counter = new AtomicLong(0L)
    val linesReadCounter = new AtomicLong(0L)
    Source.fromFile(inputFile)
        .getLines()
        .foreach(line => {
      val linesRead = linesReadCounter.incrementAndGet()
      if (linesRead % 1000 == 0) println("%d lines read".format(linesRead))
      val Array(conceptid, conceptname) = line
        .replace("\",\"", "\t")
        .replaceAll("\"", "")
        .split("\t")
      val strNorm = normalizeCasePunct(conceptname)
      val strSorted = sortWords(strNorm)
      val strStemmed = stemWords(strNorm)
      val fid = counter.incrementAndGet()
      val fdoc = new Document
      fdoc.addIndexedStoredField("id", fid.toString)
      fdoc.addIndexedStoredField("conceptid", conceptid)
      fdoc.addIndexedStoredField("conceptname", conceptname)
      fdoc.addIndexedStoredField("conceptname_norm", strNorm)
      fdoc.addIndexedStoredField("conceptname_sorted", strSorted)
      fdoc.addIndexedStoredField("conceptname_stemmed", strStemmed)
      fdoc.addIndexedOnlyField("optional_int", Option(42))
      fdoc.addStoredOnlyField("long_value", 12345678L)
      index.addDocument(fdoc)
 
      //if (fid % 1000 == 0) writer.commit()
    })
  }


  def testBuild(): Unit = {
    val url= getClass.getResource("/lab_dict.csv")
    val input = new JFile(url.getPath())
    val output = Paths.get("%s%s".format(getClass.getResource(""),"/lab_dict.index"))
    buildIndex1(input, output)
  }
  
  def testMapSingleConcept(): Unit = {
    val index = new ReadableLuceneIndex
        with WritableLuceneIndex
        with LuceneStandardAnalyzer 
        with DefaultFSLuceneDirectory 

    val strs = List("10-HYDROXYCARBAZEPINE [MOLES/VOLUME] IN SERUM OR PLASMA", "BP")
    strs.foreach(str => {
        val queryParser = index.queryParserForDefaultField("conceptname_norm")
        val query = queryParser.parse(str)
        val results = index.searchTopDocuments(query, 1)
        println("Query: " + str)
        println("Result: " + results)
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
