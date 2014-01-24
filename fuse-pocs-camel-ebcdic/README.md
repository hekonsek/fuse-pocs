JBoss Fuse proof of concept - Decoding IBM's EBCDIC-encoded messages
=========

The example demonstrates how to use Camel [String Data Format](http://camel.apache.org/string.html) to decode
EBCDIC-encoded message. Messages received from the IBM software (from the
[WebSphere](http://en.wikipedia.org/wiki/IBM_WebSphere_Application_Server) in particular) are likely to be
encoded using one of the charset from the [EBCDIC family](http://pl.wikipedia.org/wiki/EBCDIC).
