import csv
import os
import pandas as pd
import numpy as np


subfolder = "modeldata_W"
month = 1
apf = open("topAirports.csv")
apl = []
for ap in apf:
    apl.append(ap.strip())
df = pd.DataFrame()
for month in range(1,4):
    df = df.append(pd.read_csv("../data/DOT_2008_"+str(month)+"_Weather.csv"),ignore_index=True)
#Clean data
df["Delay"]=df["ArrDelay"]
df = df[df["Cancelled"] == 0]
#removed Holiday 
df = df[["Delay","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp"]]
#Keep top airports only this helps keep the data fram the same size
df = df[df["Origin"].isin(apl)]
df = df[df["Dest"].isin(apl)]
df["Origin"] = df["Origin"].apply(lambda x:x+"_ORIGIN")
df["Dest"] = df["Dest"].apply(lambda x:x+"_DEST")
#ones hot encoding 
df = pd.concat([df, pd.get_dummies(df['Origin'])], axis=1)
df = pd.concat([df, pd.get_dummies(df['Dest'])], axis=1)
del df["Origin"]
del df["Dest"]
#Booleanize delay
df["Delay"] = df["Delay"].apply(lambda x: 1 if x > 5 else 0)#Delayed more than 5 mintues 
#df = df[0:50000]
print(df.shape)
#df.to_csv("../data/"+subfolder+"/DOT_2008_W.csv",index=False)
#Store data as libsvm
inpt = "../data/"+subfolder+"/DOT_2008_W.csv"
output = "../data/"+subfolder+"/DOT_2008_W.libsvm"
#os.system("python csv2libsvm.py "+inpt+" "+output+" 0 True")
