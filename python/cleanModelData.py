import csv
import os
import pandas as pd
def APCode2Num(code):
    cd =  [str(ord(c)) for c in code]
    return "".join(cd)#cd.join("")
year = "2008"
subfolder = "test_data"

df = pd.read_csv("../data/"+subfolder+"/DOT_2008_WH_test.csv")
df["TotalDelay"]=df["ArrDelay"] + df["DepDelay"]#find total dealy
df = df[["TotalDelay","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp","Holiday"]]
df["Origin"] = df["Origin"].apply(lambda x:APCode2Num(x))
df["Dest"] = df["Dest"].apply(lambda x:APCode2Num(x))

print(df.head())
 
df.to_csv("../data/"+subfolder+"/DOT_2008_WHD_test.csv")
