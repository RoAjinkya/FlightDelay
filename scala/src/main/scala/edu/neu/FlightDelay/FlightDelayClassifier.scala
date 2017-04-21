import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.io._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.classification.{MultilayerPerceptronClassificationModel, MultilayerPerceptronClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
/**
  * Created by daniel on 3/23/17.
  */
object FlightDelayClassifier {
  //Setup spark context
  val spark = SparkSession
    .builder
    .appName("FlightDelayClassifier")
    .config("spark.executor.memory", "4g")
    .config("spark.driver.memory", "4g")
    .config("spark.master", "local")
    .getOrCreate()

  val modelName = "FlightDelayClassifier"
  def trainModel(dataPath:String): (MultilayerPerceptronClassificationModel, Dataset[Row], Dataset[Row]) ={
    // Load the data stored in LIBSVM format as a DataFrame.
    val data = spark.read.format("libsvm")
      .load(dataPath)

    // Split the data into train and test
    val splits = data.randomSplit(Array(0.7, 0.3), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    // specify layers for the neural network:
    // input layer of size 4 (features), two intermediate of size 5 and 4
    // and output of size 3 (classes)
    val layers = Array[Int](106-1, 25, 20, 6)

    // create the trainer and set its parameters
    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(128)
      .setSeed(1525L)
      .setMaxIter(500)
    // train the model
    val model = trainer.fit(train)
    return (model,train,test)
  }
  def saveModel(model:MultilayerPerceptronClassificationModel ): String ={
    val df = DateTimeFormatter.ofPattern("<MM-dd-yyy>hh-mm-ss")
    val now = ZonedDateTime.now()
    model.save("../model/"+modelName+df.format(now)+".model")
    return modelName+df.format(now)
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
  def useModel(model: MultilayerPerceptronClassificationModel, dataSet: Dataset[Row]): DataFrame ={
    val result = model.transform(dataSet)
    val predictionAndfeatures = result.select("prediction", "features")
    return predictionAndfeatures
  }
  def getData(path:String):Dataset[Row]={
    val data = spark.read.format("libsvm")
      .load(path)
    return data
  }
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val delayModel= "FlightDelayClassifier"
    val requestPath =   "../data/requests/requests.libsvm"

    /*
    Uncomment these lines to trian a new model
    val (model,train,test) = trainModel("../data/modeldata_W/DOT_2008_W.libsvm")
    val name = saveModel(model)
    val (evaluator,predictionAndLabels) = findAccuracy(model,test)
    println("Test set accuracy = " + evaluator.evaluate(predictionAndLabels))
    */

    val dModel = loadModel(delayModel)
    val request= getData(requestPath)
    val predictionResults = useModel(dModel,request.select("features"))
    val x= request.select("features").first()
    println(x)
    val prediction = predictionResults.select("prediction").first().toString()
    println(predictionResults.select("prediction").first())
    val (evaluator,predictionAndLabels) = findAccuracy(dModel,request)
    //Save to file
    val pw = new PrintWriter(new File("../data/requests/response.txt" ))
    if(prediction == "[0.0]")
      pw.write("Flight not delayed, with"+evaluator.evaluate(predictionAndLabels)*100+"% confidence.")
    if(prediction=="[1.0]")
      pw.write("Flight delayed, with "+evaluator.evaluate(predictionAndLabels)*100+"% confidence.")
    pw.close
    spark.stop()
  }

}
