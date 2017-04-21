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
for month in range(1,13):
    df = df.append(pd.read_csv("../data/DOT_2008_"+str(month)+"_Weather.csv"),ignore_index=True)
print(df.head())
#Clean data

#removed Holiday 
df = df[["Cancelled","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp"]]
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
#Srink data set
msk = np.random.rand(len(df)) < 0.2
df = df[msk]
print(df.shape)
df.to_csv("../data/"+subfolder+"/DOT_2008_W_C.csv",index=False)
#Store data as libsvm
inpt = "../data/"+subfolder+"/DOT_2008_W_C.csv"
output = "../data/"+subfolder+"/DOT_2008_W_C.libsvm"
os.system("python csv2libsvm.py "+inpt+" "+output+" 0 True")
