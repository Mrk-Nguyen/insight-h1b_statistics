package h1b

import scala.io.Source
import scala.util.matching.Regex

import java.io.BufferedReader

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
    * @param file path to the CSV file. Assumes CSV file is separated by semicolons
    * @return If there exists an IOException or FileNotFoundException, function will return an empty Seq[H1BAppCertified]
    */
  def importCSV(file: String): Seq[H1BAppCertified] = {

    // Change these assumptions below if the input files ever change their values encodings
    val sep = ";"
    val rep = ","
    //Find occurrences of quoted values that may contain the separator character
    val pattern = ";\"[^\"]*\";".r.unanchored

    try {
      val lines: BufferedReader = Source.fromFile(file).bufferedReader()

      //Analyze header and determine proper index for appropriate field references--------------------------------------
      val header = lines.readLine().split(sep).map(_.trim)
      assert(header.length > 1, s"The file is not delimited by: $sep. Please change the delimiter in your CSV files to: $sep.")
      val fieldMappings = getFieldMappings(header)

      //Build collection of only certified H1B apps---------------------------------------------------------------------
      var data: Seq[H1BAppCertified] = Seq.empty[H1BAppCertified]

      //Buffer each readline and only add it to data collection if the application is a certified H1-B application
      var nextLine = lines.readLine()
      while (nextLine != null) {
        //Replace all occurrences of the separator to the replacement character before splitting on the separator
        val raw: Array[String] = replaceSep(nextLine,pattern,sep,rep).split(sep).map(_.trim)
        val qualify: Boolean = qualifyApp(raw, fieldMappings)

        if (qualify) {

          val occupation = raw.applyOrElse(fieldMappings.getOrElse("occupation", -1),(num: Int) => "").replace("\"","")
          val wsstate = raw.applyOrElse(fieldMappings.getOrElse("wsstate", -1),(num: Int) => "")

          data = data :+ H1BAppCertified (occupation, wsstate)
        }

        nextLine = lines.readLine()
      }
      data
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
    val H1BValues = Array("H-1B","H-1B1 Chile","H-1B1 Singapore","R","C","S")

    val status = row.apply(fieldMappings.getOrElse("status", -1))
    val visaClass = row.apply(fieldMappings.getOrElse("visaclass", -1))
    val withdrawnIndex = fieldMappings.getOrElse("withdrawn",-1)

    if (status == certifiedValue) {
      if (withdrawnIndex > -1) { //Only include Certified applications that are not withdrawn
        H1BValues.contains(visaClass) && row.apply(withdrawnIndex) == "N"
      }
      else {
        H1BValues.contains(visaClass)
      }
    }
    else false
  }

  /**
    * Utility function to replace all occurrences of a character within match sections of a string according to a
    * Regex pattern. This function will be used to pre-process a string row from an incoming CSV file to guarantee that
    * the string split will produce consistent results.
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
      val first = build.slice(0,range._1 + 1)
      val middle = build.slice(range._1 + 1, range._2 - 1).replace(target,rep)
      val last = build.slice(range._2 - 1, s.length)

      build = first + middle + last
    }
    build
  }
}

/**
  * Class that contains all needed information to calculate Top 10 Occupation Metrics and Top 10 States for certified
  * visa applications. This class can be extended to calculate any other additional metrics if needed.
  *
  * @param occupationSOC contains the intended occupation according to Standard Occupational Classification of the
  *                      person applying for the visa
  * @param workSiteState contains the state where the work will take place
  */
case class H1BAppCertified (occupationSOC: String, workSiteState: String )