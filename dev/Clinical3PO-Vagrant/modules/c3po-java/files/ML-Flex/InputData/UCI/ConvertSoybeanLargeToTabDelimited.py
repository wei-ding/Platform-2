import os, sys, glob

data1FilePath = "soybean-large.data"
data2FilePath = "soybean-large.test"
attributesFilePath = "soybean-large.attributes"
dataOutFilePath = "soybean-large.all.data.tab"

data1Rows = [line.rstrip().split(",") for line in file(data1FilePath)]
data2Rows = [line.rstrip().split(",") for line in file(data2FilePath)]
dataRows = data1Rows + data2Rows

classes = [row[0] for row in dataRows]
dataRows = [row[1:] for row in dataRows] # remove class value from each row

for i in range(len(dataRows)):
    dataRows[i] = ["Instance%i" % (i+1)] + dataRows[i] + [classes[i]]

attributes = [line.rstrip() for line in file(attributesFilePath)]
attributes.append(attributes.pop(0))
dataRows.insert(0, [""] + attributes)

dataRows = zip(*dataRows) # matrix transposition

dataOutFile = open(dataOutFilePath, 'w')
for row in dataRows:
    dataOutFile.write("\t".join(list(row)) + "\n")
dataOutFile.close()
