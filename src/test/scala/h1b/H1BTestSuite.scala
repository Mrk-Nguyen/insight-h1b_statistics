package h1b

import scala.io.Source

/**
  * Unit tests for various functions for the H1BStats class
  * @author Mark Nguyen
  */
object H1BTestSuite extends App {

    println("\nH1BStats Test Suite.....................................")
    val h1btestsuite  = new H1BTestSuite

    println("\n--->Running importCSV Tests")
    h1btestsuite.testImportCSV()

    println("\n--->Running writeTopOccupations Tests")
    h1btestsuite.testWriteTopOccupations()

    println("\n--->Running writeTopStates Tests")
    h1btestsuite.testWriteTopStates()

    println("\n\n***All tests successfully completed***")

}

class H1BTestSuite {

  val inputDNE = "resources/input/doesnotexist.csv"
  val inputBase = "resources/input/h1b_input.csv"
  val input2008 = "resources/input/h1b_input2008.csv"
  val input2009 = "resources/input/h1b_input2009.csv"
  val input2010 = "resources/input/h1b_input2010.csv"
  val input2011 = "resources/input/h1b_input2011.csv"
  val input2012 = "resources/input/h1b_input2012.csv"
  val input2013 = "resources/input/h1b_input2013.csv"
  val input2014 = "resources/input/h1b_input2014.csv"
  val input2015 = "resources/input/h1b_input2015.csv"
  val input2016 = "resources/input/h1b_input2016.csv"
  val input2017 = "resources/input/h1b_input2017.csv"

  val outputOccupations = "resources/output/top_10_occupations.txt"
  val outputWorkState = "resources/output/top_10_states.txt"

  val outputOccupationsBase = "resources/output/top_10_occupationsBase.txt"
  val outputStatesBase = "resources/output/top_10_statesBase.txt"

  val outputOccupations2008 = "resources/output/top_10_occupations2008.txt"
  val outputStates2008 = "resources/output/top_10_states2008.txt"

  val outputOccupations2014 = "resources/output/top_10_occupations2014.txt"
  val outputStates2014 = "resources/output/top_10_states2014.txt"

  val outputOccupations2015 = "resources/output/top_10_occupations2015.txt"
  val outputStates2015 = "resources/output/top_10_states2015.txt"

  val outputOccupations2016 = "resources/output/top_10_occupations2016.txt"
  val outputStates2016 = "resources/output/top_10_states2016.txt"

  val outputOccupations2017 = "resources/output/top_10_occupations2017.txt"
  val outputStates2017 = "resources/output/top_10_states2017.txt"

  val h1bstats = new h1b.H1BStats

  /**
    * Tests the importCSV function from the class H1BStats
    */
  def testImportCSV(): Unit = {

    var data: Seq[h1b.H1BAppCertified] = h1bstats.importCSV(inputDNE)
    assert(data.isEmpty, s"There should be 0 qualified H-1B visa applications for: $inputDNE")

    data = h1bstats.importCSV(inputBase)
    assert(data.length == 10, s"There should only be 10 qualified H-1B visa applications for: $inputBase")
    assert(data.head.occupationSOC == "SOFTWARE DEVELOPERS, APPLICATIONS",
      s"First application should contain the occupation: SOFTWARE DEVELOPERS, APPLICATIONS for: $inputBase")

    data = h1bstats.importCSV(input2008)

    assert(data.length == 30, s"There should be only 30 qualified H-1B visa applications for: $input2008")
    assert(data.head.occupationSOC == "OCCUPATIONS IN SYSTEMS ANALYSIS AND PROGRAMMING",
      s"First application should contain the occupation: OCCUPATIONS IN SYSTEMS ANALYSIS AND PROGRAMMING for: $input2008")

    data = h1bstats.importCSV(input2009)
    assert(data.length == 37, s"There should be only 37 qualified H-1B visa applications for: $input2009")
    assert(data.head.occupationSOC == "THERAPISTS",
      s"First application should contain the occupation: THERAPISTS for: $input2009")

    try {
      data = h1bstats.importCSV(input2010)
    }
    catch {
      case e: AssertionError => println(s"Assertion correctly thrown for file: $input2010 due to missing visa class column")
    }

    data = h1bstats.importCSV(input2011)

    assert(data.length == 255, s"There should be only 301 qualified H-1B visa applications for: $input2011")
    assert(data.head.occupationSOC == "Teachers and Instructors, All Other*",
      s"First application should contain the occupation: Teachers and Instructors, All Other* for: $input2011")
    assert(data.last.occupationSOC == "Computer Programmers",
      s"Last application should contain the occupation: Computer Programmers for: $input2011")

    data = h1bstats.importCSV(input2012)
    assert(data.length == 861, s"There should be only 861 qualified H-1B visa applications for: $input2012")
    assert(data.head.occupationSOC == "Computer Systems Analysts",
      s"First application should contain the occupation: Computer Systems Analysts for: $input2012")
    assert(data.last.occupationSOC == "Sales Engineers",
      s"Last application should contain the occupation: Sales Engineers for: $input2012")

    data = h1bstats.importCSV(input2013)
    assert(data.length == 699, s"There should be only 699 qualified H-1B visa applications for: $input2013")
    assert(data.head.occupationSOC == "Mechanical Engineers",
      s"First application should contain the occupation: Mechanical Engineers for: $input2013")
    assert(data.last.occupationSOC == "Software Developers, Applications",
      s"Last application should contain the occupation: Software Developers, Applications for: $input2013")

    data = h1bstats.importCSV(input2014)
    assert(data.length == 7, s"There should be only 7 qualified H-1B visa applications for: $input2014")
    assert(data.head.occupationSOC == "Optometrists",
      s"First application should contain the occupation: Optometrists for: $input2014")
    assert(data.last.occupationSOC == "Computer Occupations, All Other",
      s"Last application should contain the occupation: Computer Occupations, All Other for: $input2014")

    data = h1bstats.importCSV(input2015)
    assert(data.length == 186, s"There should be only 186 qualified H-1B visa applications for: $input2015")
    assert(data.head.occupationSOC == "ENGINEERING TEACHERS, POSTSECONDARY",
      s"First application should contain the occupation: ENGINEERING TEACHERS, POSTSECONDARY for: $input2015")
    assert(data.last.occupationSOC == "GEOLOGICAL AND PETROLEUM TECHNICIANS",
      s"Last application should contain the occupation: GEOLOGICAL AND PETROLEUM TECHNICIANS for: $input2015")

    data = h1bstats.importCSV(input2016)
    assert(data.length == 4, s"There should be only 4 qualified H-1B visa applications for: $input2016")
    assert(data.head.occupationSOC == "CHIEF EXECUTIVES",
      s"First application should contain the occupation: CA for: $input2016")
    assert(data.head.workSiteState == "CA",
      s"First application should contain the work site state: CA for: $input2016")

    data = h1bstats.importCSV(input2017)
    assert(data.length == 13, s"There should be only 13 qualified H-1B visa applications for: $input2017")
    assert(data.head.occupationSOC == "ENGINEERING TEACHERS, POSTSECONDARY",
      s"First application should contain the occupation: ENGINEERING TEACHERS, POSTSECONDARY for: $input2017")
    assert(data.head.workSiteState == "OK",
      s"First application should contain the work site state: OK for: $input2017")

  }

  /**
    * Tests the writeTopOccurrences function from the class H1BStats
    */
  def testWriteTopOccupations(): Unit = {

    var data = h1bstats.importCSV(inputBase)
    h1bstats.writeTopOccupations(data,outputOccupations)
    var theTest = Source.fromFile(outputOccupations).getLines().mkString("\n")
    var compareTo = Source.fromFile(outputOccupationsBase).getLines().mkString("\n")
    assert(theTest == compareTo, s"Occupations file does not match compared to what's expected for: $inputBase")

    data = h1bstats.importCSV(input2008)
    h1bstats.writeTopOccupations(data,outputOccupations)
    theTest = Source.fromFile(outputOccupations).getLines().mkString("\n")
    compareTo = Source.fromFile(outputOccupations2008).getLines().mkString("\n")
    assert(theTest == compareTo, s"Occupations file does not match compared to what's expected for: $input2008")

    data = h1bstats.importCSV(input2014)
    h1bstats.writeTopOccupations(data,outputOccupations)
    theTest = Source.fromFile(outputOccupations).getLines().mkString("\n")
    compareTo = Source.fromFile(outputOccupations2014).getLines().mkString("\n")
    assert(theTest == compareTo, s"Occupations file does not match compared to what's expected for: $input2014")

    data = h1bstats.importCSV(input2015)
    h1bstats.writeTopOccupations(data,outputOccupations)
    theTest = Source.fromFile(outputOccupations).getLines().mkString("\n")
    compareTo = Source.fromFile(outputOccupations2015).getLines().mkString("\n")
    assert(theTest == compareTo, s"Occupations file does not match compared to what's expected for: $input2015")

    data = h1bstats.importCSV(input2016)
    h1bstats.writeTopOccupations(data,outputOccupations)
    theTest = Source.fromFile(outputOccupations).getLines().mkString("\n")
    compareTo = Source.fromFile(outputOccupations2016).getLines().mkString("\n")
    assert(theTest == compareTo, s"Occupations file does not match compared to what's expected for: $input2016")

    data = h1bstats.importCSV(input2017)
    h1bstats.writeTopOccupations(data,outputOccupations)
    theTest = Source.fromFile(outputOccupations).getLines().mkString("\n")
    compareTo = Source.fromFile(outputOccupations2017).getLines().mkString("\n")
    assert(theTest == compareTo, s"Occupations file does not match compared to what's expected for: $input2017")

  }

  /**
    * Tests the writeTopStates fnction from the class H1BStats
    */
  def testWriteTopStates(): Unit = {

    var data = h1bstats.importCSV(inputBase)
    h1bstats.writeTopStates(data,outputWorkState)
    var theTest = Source.fromFile(outputWorkState).getLines().mkString("\n")
    var compareTo = Source.fromFile(outputStatesBase).getLines().mkString("\n")
    assert(theTest == compareTo, s"States file does not match compared to what's expected for: $inputBase")

    data = h1bstats.importCSV(input2008)
    h1bstats.writeTopStates(data,outputWorkState)
    theTest = Source.fromFile(outputWorkState).getLines().mkString("\n")
    compareTo = Source.fromFile(outputStates2008).getLines().mkString("\n")
    assert(theTest == compareTo, s"States file does not match compared to what's expected for: $input2008")

    data = h1bstats.importCSV(input2014)
    h1bstats.writeTopStates(data,outputWorkState)
    theTest = Source.fromFile(outputWorkState).getLines().mkString("\n")
    compareTo = Source.fromFile(outputStates2014).getLines().mkString("\n")
    assert(theTest == compareTo, s"States file does not match compared to what's expected for: $input2014")

    data = h1bstats.importCSV(input2015)
    h1bstats.writeTopStates(data,outputWorkState)
    theTest = Source.fromFile(outputWorkState).getLines().mkString("\n")
    compareTo = Source.fromFile(outputStates2015).getLines().mkString("\n")
    assert(theTest == compareTo, s"States file does not match compared to what's expected for: $input2015")

    data = h1bstats.importCSV(input2016)
    h1bstats.writeTopStates(data,outputWorkState)
    theTest = Source.fromFile(outputWorkState).getLines().mkString("\n")
    compareTo = Source.fromFile(outputStates2016).getLines().mkString("\n")
    assert(theTest == compareTo, s"States file does not match compared to what's expected for: $input2016")

    data = h1bstats.importCSV(input2017)
    h1bstats.writeTopStates(data,outputWorkState)
    theTest = Source.fromFile(outputWorkState).getLines().mkString("\n")
    compareTo = Source.fromFile(outputStates2017).getLines().mkString("\n")
    assert(theTest == compareTo, s"States file does not match compared to what's expected for: $input2017")

  }
}

