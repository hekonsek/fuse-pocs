JBoss Fuse proof of concept - Asynchronous processing of REST requests with Camel, CXF and Jetty
=====

This example demonstrates how to leverage [Camel Asynchronous engine](http://camel.apache.org/async.html) and
[Jetty continuations](https://wiki.eclipse.org/Jetty/Feature/Continuations)
to asynchronously process HTTP requests coming to the REST CXF server.