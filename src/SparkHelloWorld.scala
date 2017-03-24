import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by daniel on 3/23/17.
  */
object SparkHelloWorld {
  def main(args: Array[String]){
    val conf = new SparkConf().setAppName("Word Count").setMaster("local")
    val sc = new SparkContext(conf)
    val filesc = sc.textFile("README.md")
    val wc = filesc.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_+_)
    wc.saveAsTextFile("wc_out.txt")
  }

}
