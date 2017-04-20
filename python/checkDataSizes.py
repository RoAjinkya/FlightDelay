import csv
import os
import pandas as pd
import numpy as np

subfolder = "modeldata_W"
month = 1
for month in range(1,13):
    df = pd.read_csv("../data/DOT_2008_"+str(month)+"_Weather.csv")
    #Clean data
    df["Delay"]=df["ArrDelay"]
    #Removed cancalated flights
    df = df[df["Cancelled"] == 0]
    #Remove airport if less than 150 flights a month
    minflight = 150
    for airport, flights in df.groupby("Origin"):
        if(len(flights) < minflight):
            df = df[df["Origin"] != airport]
    for airport, flights in df.groupby("Dest"):
        if(len(flights) < minflight):
            df = df[df["Dest"] != airport]
    #removed Holiday 
    df = df[["Delay","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp"]]

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
