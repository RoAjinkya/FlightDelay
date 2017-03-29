package edu.neu.FlightDelay

/**
  * Created by Daniel Eichman on 3/29/17.
  */
object Weather {
  case class SnowPrcp(Snow: Double, Prcp: Double)

  def main(args: Array[String]): Unit ={
    print(getWeather(2008,1,1,"IAD"))
  }
  def getWeather(year: Integer, Month: Integer, Day:Integer, APCode: String): SnowPrcp= {

    val url = "https://www.ncdc.noaa.gov/cdo-web/api/v2/data?datasetid=GHCND&locationid=ZIP:02128&startdate=2010-05-01&enddate=2010-05-01"
    return SnowPrcp(10,0)
  }
}
