import csv
import os
year = "2008"
f_all = open("../data/raw/DOT_"+year+".csv","r")
csv_all = csv.reader(f_all, delimiter=',')
header = csv_all.__next__()
month_col = 1
#This loop is much slower as it loop the orginal data set 12 times
#But it is pretty (and only run once)
for m in range(1,13):
    print (m)
    f_month = open("../data/raw/DOT_"+year+"_"+str(m)+".csv","w",newline='')
    csv_month = csv.writer(f_month, delimiter=',')
    csv_month.writerow(header)
    f_all.seek(1)#row 0 is header
    for row in csv_all:
        if(row[month_col]==str(m)):
            csv_month.writerow(row)

