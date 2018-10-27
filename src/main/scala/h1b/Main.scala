package h1b

/** Main class */
object Main extends H1BStats {

  /** Main function **/
  def main(args: Array[String]): Unit = {

    try {
      val inputCSV: String = args(0)
      val outputOccupations: String = args(1)
      val outputStates: String = args(2)

      val t0 = System.nanoTime()

      println(s"\nLoading data from: $inputCSV")
      val data: Seq[H1BAppCertified] = importCSV(inputCSV)
      println("Loaded %d qualified H-1B Applications".format(data.length))

      if (!data.isEmpty) {

        println(s"\nWriting Top Occupations file to: $outputOccupations")
        writeTopOccupations(data,outputOccupations)

        println(s"Writing Top States file to : $outputStates")
        writeTopStates(data,outputStates)

        val t1 = System.nanoTime()

        println("\nJob finished in: %.2f milliseconds".format((t1 - t0) / 1e6))
      } else {
        println("\nThere are no qualified H-1B applications for the given input file!")
      }

    } catch {
      case boundsException: ArrayIndexOutOfBoundsException => {
        println("Not enough arguments provided for inputCSV, outputOccupation, and outputStates")
        System.exit(1)
      }
      case _: Throwable => println("Some other exception occurred.")
    }
  }

}


