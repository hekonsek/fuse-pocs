package fuse.pocs.camel.spring.properties;

import org.apache.camel.Body;

/**
 * Simple bean transformer prefixing String message passed to it.
 */
public class Prefixer {

    private final String prefix;

    public Prefixer(String prefix) {
        this.prefix = prefix;
    }

    public String process(@Body String message) {
        return prefix + message;
    }

}