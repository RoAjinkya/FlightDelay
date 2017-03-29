package edu.neu.FlightDelay
import scalaj.http.Http
import net.liftweb.json._
/**
  * Created by Daniel Eichman on 3/29/17.
  */
object Weather {
  case class SnowPrcp(Snow: Double, Prcp: Double)

  def main(args: Array[String]): Unit ={
    println(getWeather(2010,1,4,"IAD"))
  }
  def getWeather(year: Integer, Month: Integer, Day:Integer, APCode: String): SnowPrcp= {
    val date = year+"-"+"%02d".format(Month)+"-"+"%02d".format(Day)
    val zipCode = "20166"
    val response = Http("https://www.ncdc.noaa.gov/cdo-web/api/v2/data")
                  .header("token","oPXGWqHtTMSmdZGZJQmaZwXGLeWzbuBx")
                  .param("datasetid", "GHCND")
                  .param("locationid","ZIP:"+zipCode)
                  .param("startdate",date)
                  .param("enddate",date).asString
    if(response.isError)
      return SnowPrcp(-1,-1)
    val json = parse(response.body)
    val resutls = (json \\ "results").children
    var prcp = 0.0;
    var snow = 0.0;
    for(result <- resutls){
      if((result \\ "datatype").values == "PRCP"){
        prcp = (result \\ "value").values.toString().toDouble
      }
      if((result \\ "datatype").values == "SNOW"){
        snow = (result \\ "value").values.toString().toDouble
      }
    }
    return SnowPrcp(snow,prcp)
  }
}
