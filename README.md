# Petri Net Simulator in Clojure

This is a small simulator for petri nets, completly written in Clojure.

To load a net / database take a look at the Menubar --> File --> Load Database --> database.dsl


## Installation

Navigate into this repository and type

    $ lein deps
    
to download all dependencies for this project.

After this you can create your own jar file with

    $ lein uberjar

If you want to use the REPL your almost done after `$ lein deps`. Just start it as usual with `$ lein repl`.


## Usage

### Using the GUI

There are two options to use the GUI.

1. If you want to start the GUI out of the source and not with the jar file, type:

    `$ lein run`

2. To start the GUI with the jar file, locate the jar file in `petri-net/target/` and type:

    `$ java -jar petri-net-1.0-standalone.jar`


### For the hardcore's: petri nets in the REPL

Of course it is possible to use the REPL to manipulate all the nets. Navigate with your Terminal into this repository and type

    $ lein repl
    
All functions needed to manipulate the petri net, are stored in the given API. So you can use this library `petri-net.api` to work with this tool.

If you started the REPL from this repository where the `project.clj` stored, you can start hacking with the API. See the public function with

```clojure
(keys (ns-publics 'petri-net.api))
```

The API uses the prefix `api` so you can load the database with:

```clojure
(api/load-db "database.dsl")
```

But it is highly recommended to use the GUI instead.


## Examples

For some examples, start the GUI, click in the Menubar --> File --> Load Database and locate database.dsl in this respository.

There are currently 3 nets in the database. Select one net from the left and start simulating two traffic lights, one or two elevators or create your own ones.


### Future Work

* Add option to delete places, transitions, edges, ...
* Add graphical visualization.

## License

Copyright © 2014 Christian Meter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
