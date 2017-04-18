
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession

import scala.io.Source
import java.io._

import com.typesafe.config
import org.apache.spark

import scalaj.http._
import org.apache.spark.{SparkConf, SparkContext}
import org.dmg.pmml.Coefficients


object ajinkya {

  def  main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\MS STUDY\\csye 7200 BDSEUScala\\Assignments\\Final Project\\spark-2.1.0-bin-hadoop2.7")
    val conf = new SparkConf().setAppName("Word Count").setMaster("local")
    val sc = new SparkContext(conf)




    val jssFile = Source.fromFile("Holiday.csv").getLines().toList //fromFile(“/home/walker/Downloads/data.csv”).getLines().toList

    import java.io._

    val pw = new PrintWriter(new File("data.svm" ))

    val  bufferedSource = Source.fromFile("Holiday.csv")
    for(line<- bufferedSource.getLines().drop(1)){
      val cols = line.split(",").map(_.trim)
        pw.write(""+cols);
    }
  pw.close()

val spark = SparkSession.builder().master("local").getOrCreate()

    val training = spark.read.format("libsvm").load("data.svm") //. format(“libsvm”).load(“/home/walker/Downloads/data.svm”)

    val lr = new LinearRegression().setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8)

    val lrModel = lr.fit(training)

    println(s"Coefficients:${lrModel.coefficients} + Intercept: ${lrModel.intercept}")

    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: ${trainingSummary.objectiveHistory.toList}")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept} " )

      }
      }


    // Load training data in LIBSVM format
    /*val data = MLUtils.loadLibSVMFile(sc, "Holiday.csv")

    // Split data into training (60%) and test (40%).
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 100
    val model = SVMWithSGD.train(training, numIterations)

    // Clear the default threshold.
    model.clearThreshold()

    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)

    // Save and load model

    model.save(sc, "scalaSVMWithSGDModel")
    val sameModel = SVMModel.load(sc, "tarscalaSVMWithSGDModel")*/




