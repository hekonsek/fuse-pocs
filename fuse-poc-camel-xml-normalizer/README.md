JBoss Fuse proof of concept - Normalizing XML message before tokenizing 
=========

This example demonstrates how to transfer XML message before further processing (splitting for instance). In this particular example we remove newlines from within XML elements in order to provide workaround for [MR-785](https://issues.jboss.org/browse/MR-785) i.e. invalid handling of such newlines `split().tokenizeXML()` element of Camel DSL.
