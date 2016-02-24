#! /usr/bin/env Rscript
args<-commandArgs(TRUE)
#username <-  Sys.getenv("USER")
#var<-paste("/home",username,"observationSearchrmr/",sep="/")

#setwd(var)
#df <- read.csv("obproperties.csv", header=TRUE,sep="|")

#pid<-toString(df$patientId)
pid<-args[1]
patients<-unlist(strsplit(pid,","))

observationId<-args[2]

#cncpt<-as.vector(df$conceptFile)
cncpt<-args[3]
#deathf<-as.vector(df$deathFile)
deathf<-args[4]
#Inpath<-as.vector(df$inputfilepath)
Inpath<-args[5]
#Infname<-as.vector(df$inputfilename)
#Infname<-args[6]
#Outpath<-as.vector(df$outputfilepath)
Outpath<-args[6]
#Outfname<-as.vector(df$outputfilename)
Outfname<-args[7]
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

dx<-hdfs.file(deathf,"r")
dy<-hdfs.read(dx)
dl<-rawToChar(dy)
dn<-strsplit(dl,"\n")
dm<-strsplit(dn[[1]][],"\t")

d1<-unlist(lapply(dm,function(x){x[2]}))
#s<-as.numeric(observationId)+1
map<-function(k,lines1){
  lines<-strsplit(lines1,";")
  
  x3<-sapply(lines,function(x1){
   if(!is.na(match(x1[3],patients[1:length(patients)]))){
      s<-as.numeric(x1[4])+1
      if(m[[s]][2]==observationId){
        if(m[[s]][5]==""){
          m[[s]][5]="No Units"
        }
        k<-paste(x1[5],x1[6],x1[7],m[[s]][5],m[[s]][3],sep=";")
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
  d<-"Alive"
  if(!is.na(match(string[1],d1))){
    d<-"Death"
  } 
  valo<-paste(string[2],d,"",sep=";")
  keyval(string[1],valo)
}

prg<-function(input,output){
  mapreduce(input=input,output=output,input.format="text",map=map,reduce=reduce)
}

#hdfs.root<-Inpath
hdfs.data<-file.path(Inpath)
hdfs.out<-file.path(Outpath)

out<-prg(hdfs.data,hdfs.out)
result<-from.dfs(out)
write.table(result,file=Outfname,sep="\t",row.names=FALSE,quote=FALSE,col.names=FALSE)

