package org.clinical3po.namematch

import org.junit.Test
import java.io.File
import java.nio.file.Path 
import java.nio.file.Paths
import org.junit.Assert

class NameMatcherTest {

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
