package edu.neu.FlightDelay
import net.liftweb.json._
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success}
/**
  * Created by Daniel Eichman on 3/29/17.
  */
object Weather {
  case class SnowPrcp(Snow: Double, Prcp: Double)
  case class coord(Lat: Double, Long: Double)

  def main(args: Array[String]): Unit ={
    for( a <- 2 to 12){
      getAllWeather(2008,a)
      println("Completed "+a)
    }
    //print(getWeather(2008,1,2,"BLI"))
  }
  def getAllWeather(y: Integer, m: Integer): String ={
    val year = 0
    val month = 1
    val day = 2
    val origin = 16
    val dest = 17
    var cnt = 0;
    val dot = Source.fromFile("../data/raw/DOT_"+y+"_"+m+".csv")
    val dot_weather = scala.tools.nsc.io.File("../data/DOT_"+y+"_"+m+"_Weather.csv")
    if(!dot_weather.exists){
      dot_weather.appendAll("Year,Month,DayofMonth,DayOfWeek,DepTime,CRSDepTime,ArrTime,CRSArrTime,UniqueCarrier,FlightNum,TailNum,ActualElapsedTime,CRSElapsedTime,AirTime,ArrDelay,DepDelay,Origin,Dest,Distance,TaxiIn,TaxiOut,Cancelled,CancellationCode,Diverted,CarrierDelay,WeatherDelay,NASDelay,SecurityDelay,LateAircraftDelay,OriginSnow,OriginPrcp,DestSnow,DestPrcp\n")
    }
    for(line <- dot.getLines().drop(1)){
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
      //println(s"${cols(year)}|${cols(month)}|${cols(day)}|${cols(origin)}|${cols(dest)}|$cnt")
      val or  = getWeather(cols(year).toInt,cols(month).toInt,cols(day).toInt,cols(origin))
      val dst = getWeather(cols(year).toInt,cols(month).toInt,cols(day).toInt,cols(dest))
      dot_weather.appendAll(line.trim+","+or.Snow+","+or.Prcp+","+dst.Snow+","+dst.Prcp+"\n")
      cnt+=1
      if(cnt%1000==0){println(cnt)}
      //if(cnt%22000==0){return "" }
    }
    return ""
  }
  def getWeather(year: Integer, month: Integer, day:Integer, aPCode: String): SnowPrcp= {
    val sp = checkLocally(year,month,day,aPCode)
    if(sp.Snow != -2 && sp.Prcp != -2){//found locally
      return sp
    }
    val date = year+"-"+"%02d".format(month)+"-"+"%02d".format(day)
    val zipCode = getAPZip(aPCode)
    val wsClient = NingWSClient()
    val response = wsClient.url("https://www.ncdc.noaa.gov/cdo-web/api/v2/data")
            .withQueryString("datasetid" -> "GHCND",
                             "locationid" -> ("ZIP:"+zipCode),
                              "startdate" -> date,
                              "enddate" -> date)
            //.withHeaders("token" -> "oPXGWqHtTMSmdZGZJQmaZwXGLeWzbuBx").get()//D.Eichman@outlook.com
            .withHeaders("token" -> "pHUqSdEVFTlkBeAgKjSjRzDlxrPoRZPE").get()//eichman.d@huksy.neu.edu
    response.map { wsResponse =>
        if (! (200 to 299).contains(wsResponse.status)) {
          sys.error(s"Received unexpected status ${wsResponse.status} : ${wsResponse.body}")
          return SnowPrcp(-1,-1)
        }
      }
    var json = parse("")
    response.onComplete{
      case Success(r) => {
        json = parse(r.body)
      }
      case Failure(t) => System.err.println("An error has happend: "+ t.getMessage())
    }
    try{
      Await.ready(response,100000 millis)
    } catch {
      case _ : Throwable=>  {
        println("ERROR_TY: "+year+","+month+","+day+","+aPCode+","+zipCode)
        Thread.sleep(100000)
      }
    }
    wsClient.close()
    if(json.values == None){
      println("ERROR_NONE: "+year+","+month+","+day+","+aPCode+","+zipCode)
      println(json.values)
    }
    if((json \ "status").values == "429"){
      println("OVer daily limit")
      System.exit(1)
    }
    var prcp = -1.0;//If error
    var snow = -1.0;
    val results = (json \\ "results").children
    if(results.size>0){//Some locations don't track snow
      prcp = 0.0;
      snow = 0.0;
    }
    for(result <- results){
      if((result \ "datatype").values == "PRCP"){
        prcp = (result \ "value").values.toString().toDouble
      }
      if((result \ "datatype").values == "SNOW"){
        snow = (result \ "value").values.toString().toDouble
      }
    }
    val SP = SnowPrcp(snow,prcp)

    if(SP.Snow == -1 && SP.Prcp == -1){
      println("ERROR_-1-1: "+year+","+month+","+day+","+aPCode+","+zipCode)
      println(json.values)
      //saveLocally(year,month,day,aPCode,SnowPrcp(0,0))//Data is probably missing from NOAA save as 0
      return SnowPrcp(0,0);//Data is probably missin gform NOAA save as 0
    }
    saveLocally(year,month,day,aPCode,SP)

    return SP
  }
  def saveLocally(year: Integer, month: Integer, day:Integer, aPCode: String, sp: SnowPrcp)= {
      val localWeather = scala.tools.nsc.io.File("../data/local_weather_"+year+"_"+month+".csv")
      if(!localWeather.exists) {
        localWeather.appendAll("Year,Month,Day,APCode,Snow,Prcp\n")
      }
      localWeather.appendAll(year + "," + month + "," + day + "," + aPCode + "," + sp.Snow + "," + sp.Prcp + "\n")
  }
  def checkLocally(year: Integer, month: Integer, day:Integer, aPCode: String):SnowPrcp = {
    val localWeatherf = scala.tools.nsc.io.File("../data/local_weather_"+year+"_"+month+".csv")
    if(!localWeatherf.exists) {return SnowPrcp(-2,-2)}
    val localWeather = Source.fromFile("../data/local_weather_"+year+"_"+month+".csv")
    for(line <- localWeather.getLines().drop(1)){
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
      if(cols(0).toInt == year && cols(1).toInt == month && cols(2).toInt == day && cols(3) == aPCode){
        return SnowPrcp(cols(4).toDouble,cols(5).toDouble)
      }
    }
    return SnowPrcp(-2,-2)
  }
  def getAPZip(aPCode:String):String = {
    val zipCode = checkAPLocally(aPCode)
    if(zipCode!= "Not Found"){
      return zipCode
    }
    val cd = getAPLatLong(aPCode)
    val zip = getAPZip(cd)
    saveAPLocally(aPCode,zip)
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
  def checkAPLocally(aPCode: String):String = {
    val apcodes = Source.fromFile("../data/APCode_ZipCode.csv")
    for(line <- apcodes.getLines().drop(1)){
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").map(_.trim)
      if(cols(0) == aPCode){
        return cols(1)
      }
    }
    return "Not Found"
  }
  def saveAPLocally(aPCode: String, zip:String)= {
    val apcodes = scala.tools.nsc.io.File("../data/APCode_ZipCode.csv")
    apcodes.appendAll(aPCode + "," + zip + "\n")
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
