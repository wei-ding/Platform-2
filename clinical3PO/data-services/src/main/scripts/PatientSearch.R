#! /usr/bin/env Rscript
args<-commandArgs(TRUE)

patientid<-args[1]
#cncpt<-as.vector(df$conceptFile)
cncpt<-args[2]
#Inpath<-as.vector(df$inputfilepath)
Inpath<-args[3]
#Infname<-as.vector(df$inputfilename)
#Infname<-args[4]
#Outpath<-as.vector(df$outputfilepath)
Outpath<-args[4]
#Outfname<-as.vector(df$outputfilename)
Outfname<-args[5]

hadoopHome <- Sys.getenv("HADOOP_HOME")
hadoopCmd <- paste(hadoopHome, "bin/hadoop", sep="/")

Sys.setenv(HADOOP_HOME=hadoopHome)
Sys.setenv(HADOOP_CMD=hadoopCmd)
library("rmr2")
library("rhdfs")
hdfs.init()
x<-hdfs.file(cncpt,"r")
y<-hdfs.read(x)
l<-rawToChar(y)
n<-strsplit(l,"\n")
m<-strsplit(n[[1]][],";")

d1<-unlist(lapply(m,function(x){x[1]}))

map<-function(k,lines1){
  lines<-strsplit(lines1,";")
  x3<-sapply(lines,function(x1){
    if(x1[3]==patientid){
      if(!is.na(match(x1[4],d1))){
        s<-as.numeric(x1[4])+1
        if(m[[s]][5]==""){
          m[[s]][5]="No Units"
        }
        k<-paste(m[[s]][2],x1[5],x1[6],x1[7],m[[s]][5],"",sep=";")
        l<-paste(x1[3],k,sep="#")
        return(l)
      }
    }
  })
  key1<-unlist(x3)
  return(keyval(key1,key1))
}

reduce<-function(key,value){
  string<-unlist(strsplit(key,"#"))
  keyval(NULL,string[2])
}

prg<-function(input,output){
  mapreduce(input=input,output=output,input.format="text",map=map,reduce=reduce)
}


#hdfs.root<-Inpath
hdfs.data<-file.path(Inpath)
hdfs.out<-file.path(Outpath)

out<-prg(hdfs.data,hdfs.out)
result<-from.dfs(out)
write.table(result$val,file=Outfname,sep="\t",row.names=FALSE,quote=FALSE,col.names=FALSE)

