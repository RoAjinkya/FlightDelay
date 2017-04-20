import csv
import os
import pandas as pd
import numpy as np
def APCode2Num(code):
    cd =  [str(ord(c)) for c in code]
    return "".join(cd)#cd.join("")
year = "2008"
subfolder = "test_data"
month = 1
df = pd.read_csv("../data/DOT_2008_"+str(month)+"_Weather.csv")
#Clean data
df["Delay"]=df["ArrDelay"]
df = df[df["Cancelled"] == 0]
df = df[["Delay","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp"]]#removed Holiday 
df["Origin"] = df["Origin"].apply(lambda x:x+"_ORIGIN")
df["Dest"] = df["Dest"].apply(lambda x:x+"_DEST")
#ones hot encoding 
df = pd.concat([df, pd.get_dummies(df['Origin'])], axis=1)
df = pd.concat([df, pd.get_dummies(df['Dest'])], axis=1)
del df["Origin"]
del df["Dest"]
#Booleanize delay
df["Delay"] = df["Delay"].apply(lambda x: 1 if x > 5 else 0)#Delayed more than 5 mintues 
df = df[0:50000]
#Split data into test and train
msk = np.random.rand(len(df)) < 0.8
train = df[msk]
test = df[~msk]
print(test.size())
#Save training data
train["Delay"].to_csv("../data/modeldata_W/DOT_2008_"+str(month)+"_W_train_labels.csv",index=False,header=False)
del train["Delay"]
train.to_csv("../data/modeldata_W/DOT_2008_"+str(month)+"_W_train_data.csv",index=False,header=False)
#save testing data
test["Delay"].to_csv("../data/modeldata_W/DOT_2008_"+str(month)+"_W_test_labels.csv",index=False,header=False)
del test["Delay"]
test.to_csv("../data/modeldata_W/DOT_2008_"+str(month)+"_W_test_data.csv",index=False,header=False)
