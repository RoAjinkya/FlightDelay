package edu.neu.FlightDelay
import net.liftweb.json._

import scala.io.Source
import scalaj.http.Http
/**
  * Created by Daniel Eichman on 3/29/17.
  */
object Weather {
  case class SnowPrcp(Snow: Double, Prcp: Double)
  case class coord(Lat: Double, Long: Double)

  def main(args: Array[String]): Unit ={
    println(getWeather(2010,1,4,"IAD"))
    println(getWeather(2010,1,4,"BOS"))
    println(getWeather(2010,1,4,"IAD"))
    println(getWeather(2010,1,4,"BOS"))
    println(getWeather(2010,1,4,"BOS"))
    println(getWeather(2003,2,17,"BOS"))
  }
  def getWeather(year: Integer, month: Integer, day:Integer, aPCode: String): SnowPrcp= {
    val sp = checkLocally(year,month,day,aPCode)
    if(sp.Snow != -1 && sp.Prcp != -1){//found locally
      return sp
    }
    val date = year+"-"+"%02d".format(month)+"-"+"%02d".format(day)
    val zipCode = getAPZip(aPCode)
    val response = Http("https://www.ncdc.noaa.gov/cdo-web/api/v2/data")
                  .header("token","oPXGWqHtTMSmdZGZJQmaZwXGLeWzbuBx")
                  .param("datasetid", "GHCND")
                  .param("locationid","ZIP:"+zipCode)
                  .param("startdate",date)
                  .param("enddate",date).asString
    if(response.isError)
      return SnowPrcp(-1,-1)
    val json = parse(response.body)
    val results = (json \\ "results").children
    var prcp = -1.0;
    var snow = -1.0;
    for(result <- results){
      if((result \\ "datatype").values == "PRCP"){
        prcp = (result \\ "value").values.toString().toDouble
      }
      if((result \\ "datatype").values == "SNOW"){
        snow = (result \\ "value").values.toString().toDouble
      }
    }
    val SP = SnowPrcp(snow,prcp)
    saveLocally(year,month,day,aPCode,SP)
    return SP
  }
  def saveLocally(year: Integer, month: Integer, day:Integer, aPCode: String, sp: SnowPrcp)= {
    if(sp.Prcp != -1 && sp.Snow != -1) {
      val localWeather = scala.tools.nsc.io.File("../data/local_weather.csv")
      localWeather.appendAll(year + "," + month + "," + day + "," + aPCode + "," + sp.Snow + "," + sp.Prcp + "\n")
    }
  }
  def checkLocally(year: Integer, month: Integer, day:Integer, aPCode: String):SnowPrcp = {
    val localWeather = Source.fromFile("../data/local_weather.csv")
    for(line <- localWeather.getLines().drop(1)){
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
      if(cols(0).toInt == year && cols(1).toInt == month && cols(2).toInt == day && cols(3) == aPCode){
        return SnowPrcp(cols(4).toDouble,cols(5).toDouble)
      }
    }
    return SnowPrcp(-1,-1)
  }
  def getAPZip(aPCode:String):String = {
    val cd = getAPLatLong(aPCode)
    val zip = getAPZip(cd)
    return zip
  }
  def getAPZip(cd:coord):String={
    val zip2LL = Source.fromFile("../data/raw/ZipToLatLong.csv")
    var dist = 1000000.0//Distance in degress
    var zip = ""
    for(line <- zip2LL.getLines().drop(1)){
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
      val d = haversineDistance((cols(1).toDouble,cols(2).toDouble),(cd.Lat,cd.Long))
      if(d < dist){
        dist = d
        zip = cols(0);
      }
    }
    return zip
  }
  def getAPLatLong(aPCode:String):coord ={
    val airports = Source.fromFile("../data/raw/airports.csv")
    var lat = 0.0;
    var long = 0.0;
    for(line <- airports.getLines()){
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
      if(cols(1) == (""""K"""+aPCode+""""""")){
        lat = cols(4).toDouble
        long = cols(5).toDouble
        return coord(lat,long)
      }
    }
    return coord(lat,long)
  }
  def haversineDistance(pointA: (Double, Double), pointB: (Double, Double)): Double = {
    val deltaLat = math.toRadians(pointB._1 - pointA._1)
    val deltaLong = math.toRadians(pointB._2 - pointA._2)
    val a = math.pow(math.sin(deltaLat / 2), 2) + math.cos(math.toRadians(pointA._1)) * math.cos(math.toRadians(pointB._1)) * math.pow(math.sin(deltaLong / 2), 2)
    val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    3958.761 * greatCircleDistance
  }
}
