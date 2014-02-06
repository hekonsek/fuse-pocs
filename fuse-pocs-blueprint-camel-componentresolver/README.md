JBoss Fuse proof of concept - Retrieving and using Camel ComponentResolver in the OSGi environment
=========

The example demonstrates how to retrieve and programmatically use Camel
[Component Resolver](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/spi/ComponentResolver.html)
service in OSGi environment.

Keep in mind that retrieving `ComponentResolver` service associated with the given component is a trick commonly used to
block loading of dependent (retrieving) bundle unless component bundle is fully loaded.