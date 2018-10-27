package h1b

import scala.io.Source
import scala.util.matching.Regex

import java.io._

/**
  * Contains the main functions to process the input CSV file and output the Occupation and States metrics
  * @author Mark Nguyen
  */
class H1BStats {

  /**
    * Imports a CSV file and returns a Seq of H1BAppCertified objects. Function makes the following assumptions about
    * the CSV file:
    * files are retrieved from: https://www.foreignlaborcert.doleta.gov/performancedata.cfm and follow the encoding found in the File Structure documentation on the site
    * data is delimited by ";"
    *
    * This import process will replace ALL instances of semicolons with commas within quoted values in order to achieve
    * consistent splits on the delimiter. Therefore, if a value comes in as:
    *
    *   "SOFTWARE DEVELOPERS; APPLICATIONS"
    *
    * then the value will be transformed into:
    *
    *    SOFTWARE DEVELOPERS, APPLICATIONS
    *
    * Notice that any literal double quotes will be removed in the process as well.
    *
    * @param file path to the CSV file. Assumes CSV file is separated by semicolons
    * @return If there exists an IOException or FileNotFoundException, function will return an empty Seq[H1BAppCertified]
    */
  def importCSV(file: String): Seq[H1BAppCertified] = {

    // Change these assumptions below if the input files ever change their values encodings
    val sep = ";"
    val rep = ","
    //Find occurrences of quoted values that may contain the separator character
    val pattern = "\"[^\"]*\"".r.unanchored

    try {
      val lines = Source.fromFile(file).getLines()

      //Analyze header and determine proper index for appropriate field references--------------------------------------
      val header = lines.next().split(sep).map(_.trim)
      assert(header.length > 1, s"The file is not delimited by: $sep. Please change the delimiter in your CSV files to: $sep.")
      val fieldMappings = getFieldMappings(header)

      //Build collection of only certified H1B apps---------------------------------------------------------------------

      //Make wrapper functions for easier comprehension in the map-reduce algorithm
      def preprocess(line: String): Array[String] = {
        replaceSep(line,pattern,sep,rep).split(sep).map(_.trim)
      }

      def filterH1BCertified(row: Array[String]): Boolean = {
        qualifyApp(row,fieldMappings)
      }

      def createH1BCertifiedObject(row: Array[String]): H1BAppCertified = {
        val occupation = row.applyOrElse(fieldMappings.getOrElse("occupation", -1),(num: Int) => "").replace("\"","")
        val wsstate = row.applyOrElse(fieldMappings.getOrElse("wsstate", -1),(num: Int) => "")

        H1BAppCertified (occupation, wsstate)
      }

      lines.
        map(preprocess).
        filter(filterH1BCertified).
        map(createH1BCertifiedObject).
        toSeq

    }
    catch {
      case e: java.io.FileNotFoundException => {
        println(s"File couldn't be found: $file. Did you make sure to not include a period in the front?")
        Seq.empty[H1BAppCertified]
      }
      case e: java.io.IOException => {
        println(s"Got an IOException while accessing: $file")
        Seq.empty[H1BAppCertified]
      }
    }

  }

  /**
    * Returns a mutable Map that contains the index values we need to reference the key columns we need.
    *
    * Map will also:
    *   contain a flag to indicate whether the application is withdrawn (if the header contains a column name: "WITHDRAWN")
    *
    * @param header Contains the column names from an input CSV file
    * @return Map containing the four keys: class, status, occupation, wsstate, appencoding
    */
  private def getFieldMappings(header: Array[String]): collection.mutable.Map[String, Int] = {

    assert(!header.isEmpty, "Input header must contain data.")

    //Define possible field names for key fields that needs to be referenced.
    //Assumes that the header will ONLY contain one of each possible field names. For example, The header will not contain VISA_CLASS and PROGRAM together
    val visaClassNames = Array("VISA_CLASS", "PROGRAM", "PROGRAM_DESIGNATION")
    val statusNames = Array("CASE_STATUS", "STATUS", "APPROVAL_STATUS")
    val occupationNames = Array("SOC_NAME", "LCA_CASE_SOC_NAME", "OCCUPATIONAL_TITLE")
    val workSiteStateNames = Array("WORKSITE_STATE", "LCA_CASE_WORKLOC1_STATE", "STATE_1","WORK_LOCATION_STATE1")

    val fieldMappings = collection.mutable.Map("visaclass" -> -2, "status" -> -2,
                                               "occupation" -> -2, "wsstate" -> -2, "withdrawn" -> -1)

    for (i <- header.indices) {
      val col = header(i)

      if (visaClassNames.contains(col)) fieldMappings.update("visaclass", i)
      else if (statusNames.contains(col)) fieldMappings.update("status", i)
      else if (occupationNames.contains(col)) fieldMappings.update("occupation", i)
      else if (workSiteStateNames.contains(col)) fieldMappings.update("wsstate", i)
      else if (col == "WITHDRAWN") fieldMappings.update("withdrawn", i)
    }

    //Check if all field names can be found
    fieldMappings.foreach {
      case (key, value) =>
        assert(value > -2, s"Couldn't find a matching field for the $key. " +
          s"Please check the source file and update the Arrays defined at the beginning of getFieldMappings if needed.")
    }

    fieldMappings
  }

  /**
    * Qualifies the application of whether the application is a certified H1-B visa
    * @param row Contains all the columns of data for one visa application
    * @param fieldMappings object returned from the function getFieldMappings to aid in data retrievel from the row array
    * @return true if the application is H1-B certified, false otherwise
    */
  private def qualifyApp(row: Array[String], fieldMappings: collection.mutable.Map[String, Int]): Boolean = {

    assert(!row.isEmpty, "Input row must contain data.")

    // Change these assumptions below if the input files ever change their values encodings
    val certifiedValue = "CERTIFIED"

    val status = row.apply(fieldMappings.getOrElse("status", -1))
    val withdrawnIndex = fieldMappings.getOrElse("withdrawn",-1)

    if (status == certifiedValue) {
      if (withdrawnIndex > -1) { //Only include Certified applications that are not withdrawn
        row.apply(withdrawnIndex) == "N"
      }
      else {
        true
      }
    }
    else false
  }

  /**
    * Utility function to replace all occurrences of a character within match sections of a string according to a
    * Regex pattern. This function will be used to pre-process a string row from an incoming CSV file to guarantee that
    * the String.split function will produce consistent results for incoming row of data.
    *
    * @param s the target string for replacements
    * @param pattern Regex pattern for matching
    * @param target the target character
    * @param rep the replacing character
    * @return the string with all character replacements
    */
  private def replaceSep(s: String, pattern: Regex, target: String, rep: String): String = {

    val matches: Iterator[Regex.Match] = pattern.findAllMatchIn(s)
    var build = s

    for (m <- matches) {

      val range = (m.start, m.end)
      val first = build.slice(0, range._1)
      val middle = build.slice(range._1, range._2).replace(";", ",")
      val last = build.slice(range._2, build.length)

      build = first + middle + last
    }
    build
  }

  /**
    * Writes the top 10 occurrences by count to a CSV file. CSV file will contain the following three columns of data:
    *   - TOP_OCCUPATIONS
    *   - NUMBER_CERTIFIED_APPLICATIONS
    *   - PERCENTAGE
    *
    * Percentage will be calculated based off the total certified applications regardless of occupation
    * The data will be semicolon separated.
    *
    * @param applications data structure returned from the importCSV function
    * @param fileOut the filename used to write the CSV file.
    */
  def writeTopOccupations(applications: Seq[H1BAppCertified], fileOut: String) = {

    //Change these assumptions if requirements change
    val header = Array("TOP_OCCUPATIONS","NUMBER_CERTIFIED_APPLICATIONS","PERCENTAGE")
    val topN = 10
    val sep = ";"

    val aggregated = aggregateCountBy("occupationSOC", applications)
    writeOutGroupedCounts( aggregated.take(topN), fileOut, header, sep)
  }

  def writeTopStates(applications: Seq[H1BAppCertified], fileOut: String) = {

    //Change these assumptions if requirements change
    val header = Array("TOP_STATES","NUMBER_CERTIFIED_APPLICATIONS","PERCENTAGE")
    val topN = 10
    val sep = ";"

    val aggregated = aggregateCountBy("workSiteState", applications)
    writeOutGroupedCounts( aggregated.take(topN), fileOut, header, sep)
  }

  /**
    * Computes a count aggregate given the groupByCol.
    * Percentage will be calculated based off the total number of certified applications
    *
    * @param groupByCol the groupByCol must belong to the possible parameters in the case class H1BAppCertified
    * @param applications data structure returned from the importCSV function
    * @return sorted sequence of the aggregated
    */
  private def aggregateCountBy(groupByCol: String, applications: Seq[H1BAppCertified] ): Seq[GroupedCount] = {

    val total = applications.size.toDouble

    val aggregated =
      applications.par.
        groupBy( _.getField(groupByCol).getOrElse("") ).
        mapValues(_.size).
        map( tup => GroupedCount(tup._1, tup._2, tup._2/total * 100 ))

    aggregated.toList.
      sortWith(_.groupByCol < _.groupByCol).
      sortWith( _.count > _.count)
  }

  /**
    * Writes out data to a CSV file format with the given header and delimiter.
    *
    * @param aggregated the data returned from the function aggregateCountBy
    * @param fileOut the path to the file to be written to
    * @param header the column names to be written on the first line
    * @param sep the delimiter
    */
  private def writeOutGroupedCounts(aggregated: Seq[GroupedCount], fileOut: String, header: Array[String], sep: String) = {

    var file: File = null
    var bw: BufferedWriter = null

    try {

      file = new File(fileOut)
      bw = new BufferedWriter(new FileWriter(file))

      bw.write(header.mkString(sep))
      bw.newLine()

      for(groupedCount <- aggregated.slice(0,aggregated.length - 1)) {
        bw.write(groupedCount.toCSVRow(sep))
        bw.newLine()
      }

      //write last line
      bw.write( aggregated.last.toCSVRow(sep) )

    }
    catch {
      case e: IOException => println(s"IOException error, error writing file: $fileOut")
    }
    finally {
      bw.close()
    }

  }

}

/**
  * Contains all needed information to calculate Top 10 Occupation Metrics and Top 10 States for certified
  * visa applications. This class can be extended to calculate any other additional metrics if needed.
  *
  * @param occupationSOC contains the intended occupation according to Standard Occupational Classification of the
  *                      person applying for the visa
  * @param workSiteState contains the state where the work will take place
  */
case class H1BAppCertified (occupationSOC: String, workSiteState: String ) {

  /**
    * Utility function to retrieves parameters by a given String.
    * This method is needed to abstract away the map-reduce algorithms into the aggregateCountBy function
    *
    * @param fieldName
    * @return
    */
  def getField(fieldName: String): Option[String] = {
    fieldName match {
      case "occupationSOC" => Some(occupationSOC)
      case "workSiteState" => Some(workSiteState)
      case _ => None
    }
  }
}

/**
  * Contains the calculated values from a groupBy count aggregation. Contains a method to convert the values
  * into a string delimited by a given separator.
  *
  * @param groupByCol contains the key value from a groupBy count aggregation
  * @param count contains the count of items that belong to groupByCol from a groupBy count aggregation
  * @param percent contains the count divided by a given total number
  */
case class GroupedCount(groupByCol: String, count: Int, percent: Double ) {

  /**
    * Convert values in GroupedCount to a delimited String. Formats the percent by rounding to the tenth decimal place and adds
    * a % character at the end.
    *
    * @param sep Given delimiter
    * @return String combination of all values delimited by a given separator
    */
  def toCSVRow(sep: String): String = {
    val percentFormatted = "%.1f%%".format(percent)
    s"$groupByCol$sep$count$sep$percentFormatted"
  }
}