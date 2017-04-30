import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{FlatSpec, Matchers}
package scala.HolidayClassification

/**
  * Created by sakha on 4/21/2017.
  */
object HolidayClassificationSpec extends FlatSpec with Matchers{


   behavior of "HolidayClassification"
  it should "read the day, month, year from the csv" in {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val readFile = sc.textFile("..\\Project\\TestData.csv")

    val colData = readFile.map(line => {
      val lines = line.split(',')
      (lines(0), lines(1), lines(2), lines(3))
    })
  }

   behavior of "isHoliday"

   it should "accept a java.util.Date and return an integer" in {
    val obj = new HolidayClassification()
    val date = java.util.Calendar.getInstance().getTime

    val res = obj.isHoliday(date)

    if(res == (Int)){
      true
    }else false

  }


}
