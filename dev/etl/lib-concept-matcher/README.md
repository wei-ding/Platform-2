# lib-concept-match #

## Build & run ##

1. Get the source codes: 

    ```sh
    $ git clone git@github.com:Clinical3PO/Stage.git
    $ cd Stage/dev/etl/lib-concept-matcher
    ```
2. Build and Run
    
    ```sh
    chmod u+x sbt
    ./sbt clean && ./sbt compile && ./sbt run
    ```
    or
    ```
    ./sbt
    > +run
    ```

## Package ##

```sh
$ ./sbt package
```

## Supported Domains ##

1. `Lab`
2. `Drug`


## Contact ##

- [Clinical3PO](http://www.clinical3po.org)

## License

The content of this project itself is licensed under the [Apache License 2.0 license](http://www.apache.org/licenses/LICENSE-2.0), and the underlying source code used to format and display that content is licensed under the [Apache License 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

