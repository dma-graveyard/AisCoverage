AisCoverage
======

### Introduction ###

Java application for making AIS coverage analysis

TODO

### Building ###

To build you will need

* JDK 1.6+ (http://java.sun.com/j2se/)
* Apache Ant 1.7+ (http://ant.apache.org) or Eclipse IDE

To build everything
 
	ant 
	
### Running ###

The application takes the following arguments

<pre>
Usage: AisCoverage <-t|-f> <filename/host1:port1,...,hostN,portN>
        -t TCP round robin connection to host1:port1 ... hostN:portN
        -f Read from file filename
</pre>

#### Running with Ant ####

To run with Ant. E.g.

    ant -Dargs='-f somefile' run

#### Running with SH/BAT file ####

Use BAT or SH script. E.g.

    AisCoverage.bat -t localhost:4001
    
#### Running with Eclipse ####

Use run configuration in project. Add command line arguments to use.

### License ###

This library is provided under the LGPL, version 3.