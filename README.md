# petri-net

FIXME: description

## Installation

Set up JavaFX with Maven:

```> mvn install:install-file -Dfile=/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jfxrt.jar -DgroupId=com.oracle -DartifactId=javafx-runtime -Dpackaging=jar -Dversion=2.2.0```

Old version, don't know if this works...
```> mvn deploy:deploy-file -DgroupId=local.oracle -DartifactId=javafxrt -Dversion=2.2.0 -Dpackaging=jar -Dfile=/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jfxrt.jar -Durl=file:/Users/USERNAME/.m2/repository```

Then type:

```> lein deps```




## Usage

FIXME: explanation

    $ java -jar petri-net-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
