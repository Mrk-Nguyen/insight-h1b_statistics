package h1b

/**
  * Unit tests for various functions for the H1BStats class
  * @author Mark Nguyen
  */
object H1BTestSuite extends App {

    val h1btestsuite  = new H1BTestSuite

    h1btestsuite.testImportCSV()
    println("All tests successfully completed.")

}

class H1BTestSuite {

  val inputDNE = "input/doesnotexist.csv"
  val inputBase = "input/h1b_input.csv"
  val input2008 = "input/h1b_input2008.csv"
  val input2009 = "input/h1b_input2009.csv"
  val input2010 = "input/h1b_input2010.csv"
  val input2011 = "input/h1b_input2011.csv"
  val input2012 = "input/h1b_input2012.csv"
  val input2013 = "input/h1b_input2013.csv"
  val input2014 = "input/h1b_input2014.csv"
  val input2015 = "input/h1b_input2015.csv"
  val input2016 = "input/h1b_input2016.csv"
  val input2017 = "input/h1b_input2017.csv"

  val h1bstats = new h1b.H1BStats

  /**
    * Tests the importCSV function from the class H1BStats
    */
  def testImportCSV(): Unit = {

    var data: Seq[h1b.H1BAppCertified] = h1bstats.importCSV(inputDNE)
    assert(data.isEmpty, s"There should be 0 qualified H-1B visa applications for: $inputDNE")

    data = h1bstats.importCSV(inputBase)
    assert(data.length == 9, s"There should only be 9 qualified H-1B visa applications for: $inputBase")
    assert(data.head.occupationSOC == "SOFTWARE DEVELOPERS, APPLICATIONS",
      s"First application should contain the occupation: SOFTWARE DEVELOPERS, APPLICATIONS for: $inputBase")

    data = h1bstats.importCSV(input2008)
    assert(data.length == 26, s"There should be only 26 qualified H-1B visa applications for: $input2008")
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
    assert(data.length == 255, s"There should be only 255 qualified H-1B visa applications for: $input2011")
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
}

