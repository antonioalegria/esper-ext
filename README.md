# Esper Extension Library

[Esper](http://esper.codehaus.org) is an OSS Complex Event Processing (CEP) library which enables anyone to build high-throughput, low-latency, real-time event processing applications.

The Esper extension library is a set of utility plugins you can use in your Esper implementation.

At version 0.1.0 it is stable although it still does not have many extensions. Currently it mostly has a plugin view that calculates the [TRIX index](http://en.wikipedia.org/wiki/Trix_%28technical_analysis%29) over a value in an event stream. This index allows you, in fact, to calculate the tendency of that value - is it trending up, down, stable?

There are plans to include further extensions for things like:
* Named window persistance and recovery after restarting
* Catalog of technical analysis plugin windows and aggregation functions (great for Algorithmic Trading but also for general time series trend analysis)
* EPL rule annotations
* (many more...)

Suggestions and contributions are welcome :-)

## Getting Started

You can build your own JAR or download a prebuilt one. You include the JAR in your classpath and configure the plugin windows and functions in your esper configuration files. For more information go [here](http://esper.codehaus.org/esper-4.4.0/doc/reference/en/html/extension.html#custom-views-config).

### Building JAR:

Build scripts are just plain-old Ant. To generate a JAR just do:
    ant build

### Maven:

This project still hasn't been Mavenified but I plan to do it as soon as I have some time. For the time being, if you are using Maven just include the following in your `pom.xml`:

    <dependency>
        <groupId>frogfish</groupId>
        <artifactId>esper-ext</artifactId>
        <version>0.1.0</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/esper-ext-0.1.0.jar</systemPath>
    </dependency>

## License

esper-ext is open source software released under the **MIT License** (see LICENSE file). I will be happy to integrate patches from anyone willing to contribute.
