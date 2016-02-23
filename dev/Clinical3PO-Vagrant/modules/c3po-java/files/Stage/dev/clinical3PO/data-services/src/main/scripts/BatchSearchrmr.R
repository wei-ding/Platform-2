#! /usr/bin/env Rscript
args<-commandArgs(TRUE)

filename<-args[1]
cncpt<-args[2]
deathf<-args[3]
Inpath<-args[4]
#Infname<-args[5]
Outpath<-args[5]
Outfname<-args[6]

sp1<-unlist(strsplit(filename,"#"))

patientsnew<-unlist(lapply(sp1,function(x){
y<-strsplit(x,"~")
y1<-unlist(strsplit(y[[1]][2],","))
z<-y[[1]][1]
y2<-paste(y1,z,sep="~")
return(y2)
}))

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
patients1<-unique(patientsnew[1:length(patientsnew)])
map<-function(k,lines1){
  lines<-strsplit(lines1,";")
  x3<-sapply(lines,function(x1){
    s<-as.numeric(x1[4])+1  
   f<-paste(x1[3],m[[s]][2],sep="~")
   if(f %in% patients1){
        s<-as.numeric(x1[4])+1
        j<-paste(m[[s]][2],x1[3],sep="~")
        if(m[[s]][5]==""){
          m[[s]][5]="No Units"
        }
        k<-paste(x1[5],x1[6],x1[7],m[[s]][5],m[[s]][3],sep=";")
        l<-paste(j,k,sep="#")
        return(l)
   }

})
  key1<-unlist(x3)
  return(keyval(key1,key1))
}

d1<-unlist(lapply(dm,function(x){x[2]}))

reduce<-function(key,value){
  string<-unlist(strsplit(key,"#"))

  stsp<-unlist(strsplit(string[1],"~"))
  d<-"Alive"
  if(!is.na(match(stsp[2],d1))){
    d<-"Death"
  }
  valo<-paste(string[2],d,"",sep=";")
  keyval(string[1],valo)
}

prg<-function(input,output){
  mapreduce(input=input,output=output,input.format="text",map=map,reduce=reduce)
}


hdfs.data<-file.path(Inpath)
hdfs.out<-file.path(Outpath)

out<-prg(hdfs.data,hdfs.out)
result<-from.dfs(out)
write.table(result,file=Outfname,sep="\t",row.names=FALSE,quote=FALSE,col.names=FALSE)
