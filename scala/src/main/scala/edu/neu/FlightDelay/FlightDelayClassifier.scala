import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.classification.{MultilayerPerceptronClassificationModel, MultilayerPerceptronClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.{Dataset, Row, SparkSession}
/**
  * Created by daniel on 3/23/17.
  */
object FlightDelayClassifier {
  //Setup spark context
  val spark = SparkSession
    .builder
    .appName("FlightDelayClassifier")
    .config("spark.master", "local")
    .getOrCreate()

  def trainModel(dataPath:String): (MultilayerPerceptronClassificationModel, Dataset[Row], Dataset[Row]) ={
    // Load the data stored in LIBSVM format as a DataFrame.
    val data = spark.read.format("libsvm")
      .load(dataPath)

    // Split the data into train and test
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    // specify layers for the neural network:
    // input layer of size 4 (features), two intermediate of size 5 and 4
    // and output of size 3 (classes)
    val layers = Array[Int](4, 5, 4, 3)

    // create the trainer and set its parameters
    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(128)
      .setSeed(1234L)
      .setMaxIter(100)
    // train the model
    val model = trainer.fit(train)
    return (model,train,test)
  }
  def saveModel(model:MultilayerPerceptronClassificationModel ): Unit ={
    val df = DateTimeFormatter.ofPattern("<MM-dd-yyy>hh-mm-ss")
    val now = ZonedDateTime.now()
    model.save("../model/SampleClassifier"+df.format(now)+".model")
  }
  def findAccuracy(model:MultilayerPerceptronClassificationModel,test:Dataset[Row]):(MulticlassClassificationEvaluator,Dataset[Row])={
    // compute accuracy on the test set
    val result = model.transform(test)
    val predictionAndLabels = result.select("prediction", "label")
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
    return (evaluator,predictionAndLabels)
  }
  def loadModel(name:String):MultilayerPerceptronClassificationModel={
    return MultilayerPerceptronClassificationModel.load("../model/"+name+".model")
  }
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val (model,train,test) = trainModel("../data/test_data/sample_multiclass_classification_data.txt")
    saveModel(model)
    val model1 = loadModel("SampleClassifier<04-20-2017>11-36-53")
    val (evaluator,predictionAndLabels) = findAccuracy(model1,test)

    println(test.select("features").show(3))
    //println(result.show(3))
    val input =List(0,0.6,0.9,0)

    //println(model.transform(test.select("features")).show(3))
    println("Test set accuracy = " + evaluator.evaluate(predictionAndLabels))
    println("Precision:" + evaluator.evaluate(predictionAndLabels))
    spark.stop()
  }

}
