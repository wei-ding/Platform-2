package org.clinical3po.namematch

import java.io.File
import java.nio.file.Path 
import java.nio.file.Paths
import org.junit.Assert

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
    val input = new File("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.csv")
    val output = Paths.get("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.index")
    val matcher = new NameMatcher()
    matcher.buildIndex(input, output)
  }
  
  def testMapSingleConcept(): Unit = {
    val luceneDir = Paths.get("/home/frey/codebase/Clinical3PO/Stage/dev/etl/namematch/src/main/resources/lab_dict.index")
    val matcher = new NameMatcher()
    val strs = List("10-HYDROXYCARBAZEPINE [MOLES/VOLUME] IN SERUM OR PLASMA", "BP")
    strs.foreach(str => {
      val concepts = matcher.annotateConcepts(str, luceneDir)
      Console.println("Query: " + str)
      matcher.printConcepts(concepts)
      Console.println("Result: " + concepts)
      //Assert.assertEquals(1, concepts.size)
      //Assert.assertEquals(100.0D, concepts.head._1, 0.1D)
    })
  }

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

  def main(args: Array[String]) {
    //testSortWords()
    //testStemWords()
    //testBuild()
    testMapSingleConcept()
    testMapMultipleConcepts()

  }
}
