# mimic2 to omop v4 etl #

## mimic2 demo dataset download webpage ##
https://physionet.org/mimic2/demo/
download: mimic2_flatfiles.tar.gz
https://physionet.org/mimic2/demo/mimic2_flatfiles.tar.gz

``` sh
$ for i in *.tar.gz; do echo working on $i; tar xvzf $i ; done

merge all the small files

## Build & run ##

```sh
$ cd mimic2
$ chmod u+x sbt
$ ./sbt
> +run
```

## Package ##

```sh
$ ./sbt package

## Contact ##

- Clinical3PO
