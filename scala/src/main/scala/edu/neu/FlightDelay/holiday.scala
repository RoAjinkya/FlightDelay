import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.{DateTimeConstants, LocalDate}

import scala.io.Source
/**
  * Created by Ajinkya on 4/8/2017.
  */
object HolidayClassification {

  import java.util.Calendar
  val cal = Calendar.getInstance()

  def main(args: Array[String]): Unit = {

    //System.setProperty("hadoop.home.dir", "D:\\MS STUDY\\csye 7200 BDSEUScala\\Assignments\\Final Project\\spark-2.1.0-bin-hadoop2.7")
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val readFile = sc.textFile("../data/test_data/DOT_2008_Weather_test.csv")

    // getting the first 4 relevent columns of the csv file. Year, month, date, day
    val colData = readFile.map(line => {
      val lines = line.split(',')
      (lines(0), lines(1), lines(2), lines(3))
    })

    //remove the header row
    val newData = colData.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }

    val dot = Source.fromFile("../data/test_data/DOT_2008_Weather_test.csv")
    val dot_holiday = scala.tools.nsc.io.File("../data/test_data/DOT_2008_WH_test.csv")
    if(!dot_holiday.exists){
      dot_holiday.appendAll("Year,Month,DayofMonth,DayOfWeek,DepTime,CRSDepTime,ArrTime,CRSArrTime,UniqueCarrier,FlightNum,TailNum,ActualElapsedTime,CRSElapsedTime,AirTime,ArrDelay,DepDelay,Origin,Dest,Distance,TaxiIn,TaxiOut,Cancelled,CancellationCode,Diverted,CarrierDelay,WeatherDelay,NASDelay,SecurityDelay,LateAircraftDelay,OriginSnow,OriginPrcp,DestSnow,DestPrcp,Holiday\n")
    }

    var cont = 0;
    for(line <- dot.getLines().drop(1)){
      val withIndex = newData.zipWithIndex()
      val indexKey = withIndex.map { case (k, v) => (v, k) }
      val b = indexKey.lookup(cont)
      cont += 1

      val arrB = b.mkString("")
      var cnt = 0
      val str1 = arrB.dropRight(1)
      val str2 = str1.drop(1)
      val seqStr = str2.split(",")
      val sdf1 = new java.text.SimpleDateFormat("yyyy-MM-dd")
      val sdf2 = new java.text.SimpleDateFormat("EEEE")
      val date = sdf1.parse(seqStr.apply(0) + "-" + seqStr.apply(1) + "-" + seqStr.apply(2))
      val day = sdf2.format(date)
      val holiday  = isHoliday(date,day)
      dot_holiday.appendAll(line.trim+","+ holiday +"\n")
      cnt+=1
      if(cnt%1000==0){println(cnt)}
    }
  }

  //====================================================================================================================
  def isHoliday(date: java.util.Date, day : String): Int ={
    //major holidays
    if((isChristmas(date) || isNewYear(date) || is4thJuly(date) || isVeteransDay(date) || isStPatricksDay(date))
      || (isThanksgiving(date)) ){
      3
    }
    //long weekends
    else if(isMemorialDay(date) || isLaborDay(date) || isColumbusDay(date)){
      2
    }
    //weekends
    else if(isWeekend(date)) {
      1
    }
    //normal weekday
    else  0
  }

  //====================================================================================================================
  def isWeekend(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
      true
    }
    else false
  }

  def isChristmas(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    if(cal.get(Calendar.MONTH)== Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) == 25){
      true
    } else false
  }

  def isNewYear(date: java.util.Date) : Boolean ={
    cal.setTime(date)
    if(cal.get(Calendar.MONTH)== Calendar.JANUARY && cal.get(Calendar.DAY_OF_MONTH) == 1){
      true
    }
    else false
  }

  def is4thJuly(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    if(cal.get(Calendar.MONTH)== Calendar.JULY && cal.get(Calendar.DAY_OF_MONTH) == 4){
      true
    }
    else false
  }

  def isVeteransDay(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    if(cal.get(Calendar.MONTH)== Calendar.NOVEMBER && cal.get(Calendar.DAY_OF_MONTH) == 10){
      true
    }
    else false
  }

  def isStPatricksDay(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    if(cal.get(Calendar.MONTH)== Calendar.MARCH && cal.get(Calendar.DAY_OF_MONTH) == 17){
      true
    }
    else false
  }

  def isThanksgiving(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    val d = getNDayOfMonth(DateTimeConstants.THURSDAY, 4, 11, cal.get(Calendar.YEAR))
    if(date.equals(d)){
      true
    }
    else false
  }

  def isLaborDay(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    val d = getNDayOfMonth(DateTimeConstants.MONDAY, 1, 9, cal.get(Calendar.YEAR))
    if(date.equals(d)){
      true
    }
    else false
  }

  def isColumbusDay(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    val d = getNDayOfMonth(DateTimeConstants.MONDAY, 2, 10, cal.get(Calendar.YEAR))
    if(date.equals(d)){
      true
    }
    else false
  }

  def isMemorialDay(date: java.util.Date) : Boolean = {
    cal.setTime(date)
    val d = getLastWeekDayOfMonth(DateTimeConstants.MONDAY, 5, cal.get(Calendar.YEAR))
    if(date.equals(d)){
      true
    }else false
  }
  //====================================================================================================================
  //get nth particular day of the given month and the year (ex: 3rd Monday of April, 2017)
  def getNDayOfMonth(day: Int, week: Int, month: Int, year: Int) : java.util.Date ={
    var d = new LocalDate(year,month,1).withDayOfWeek(day)
    if(d.getMonthOfYear != month) {d = d.plusWeeks(1);d}
    d = d.plusWeeks(week-1)
    d.toDateTimeAtStartOfDay().toDate();
  }

  //get last of a particular day in the given month and the year (ex: last Monday of April, 2017)
  def getLastWeekDayOfMonth(day: Int, month: Int, year: Int) : java.util.Date={
    var d = new LocalDate(year,month,1).plusMonths(1).withDayOfWeek(day)
    if(d.getMonthOfYear != month){
      d = d.minusWeeks(1)
    }
    d.toDateTimeAtStartOfDay.toDate
  }
}