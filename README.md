# FlightDelay
This is spark project that predicts if a US flight will be delayed. It implements a Spark neural net to create these predictions. The neural net was trained on all flight data from 2008 that can be found [here](http://stat-computing.org/dataexpo/2009/the-data.html). To increase the accuracy of the data we looked up the weather at the origin and destination airports. We also included holiday information that would aid in predicting the flight delays. 
## Web Front End (Java)
This project uses a simple dynamic web project (from eclipse) and an apache server to produce a web page where the user can choose the origin and destination and weather of the flight. 
## Neural net backend (Scala on Spark) 
The backend of this project implements a Spark neural net. Two models (cancellation and delay) were both trained with around 4 million records. Both models added weather information and holiday information to try to make the model more precise. The cancellation model is about 98% correct while the delay model was about 67% correct. 
## Gathering extra data (Scala)
To gather the weather from NOAA.gov a Scala script was written. Similarly, adding holiday information was done with a Scala script.
## Cleaning data (Python)
Python scripts were used to convert csv formatted data to svm formatted data. The script also removes unnecessary columns and changes origin and destination to one’s hot encoding. 
