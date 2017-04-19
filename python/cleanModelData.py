import csv
import os
import pandas as pd

year = "2008"
subfolder = "test_data"

df = pd.read_csv("../data/"+subfolder+"/DOT_2008_WH_test.csv")
df["TotalDelay"]=df["ArrDelay"] + df["DepDelay"]#find total dealy
df = df[["TotalDelay","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp","Holiday"]]

df.to_csv("../data/"+subfolder+"/DOT_2008_WHD_test.csv")
print(df.head())
 
