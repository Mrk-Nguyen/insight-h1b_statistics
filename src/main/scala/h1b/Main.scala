package h1b

/** Main class */
object Main extends H1BStats {

  /** Main function **/
  def main(args: Array[String]): Unit = {

    try {
      val inputCSV: String = args(0)
      val outputOccupations: String = args(1)
      val outputStates: String = args(2)

      val data: Seq[H1BAppCertified] = importCSV(inputCSV)

    } catch {
      case boundsException: ArrayIndexOutOfBoundsException => {
        println("Not enough arguments provided for inputCSV, outputOccupation, and outputStates")
        System.exit(1)
      }
      case _: Throwable => println("Some other exception occurred.")
    }
  }

}


