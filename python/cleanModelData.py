import csv
import os
import pandas as pd
def APCode2Num(code):
    cd =  [str(ord(c)) for c in code]
    return "".join(cd)#cd.join("")
year = "2008"
subfolder = "test_data"

df = pd.read_csv("../data/"+subfolder+"/DOT_2008_WH_test.csv")
df["Delay"]=df["ArrDelay"]#find total dealy
df = df[["Delay","DayOfWeek","Origin","Dest","OriginSnow","OriginPrcp","DestSnow","DestPrcp","Holiday"]]
df["Origin"] = df["Origin"].apply(lambda x:x+"_ORIGIN")
df["Dest"] = df["Dest"].apply(lambda x:x+"_DEST")
df = pd.concat([df, pd.get_dummies(df['Origin'])], axis=1)
df = pd.concat([df, pd.get_dummies(df['Dest'])], axis=1)
del df["Origin"]
del df["Dest"]
print(df.head())
 
df.to_csv("../data/"+subfolder+"/DOT_2008_WHD_test.csv",index=False)
