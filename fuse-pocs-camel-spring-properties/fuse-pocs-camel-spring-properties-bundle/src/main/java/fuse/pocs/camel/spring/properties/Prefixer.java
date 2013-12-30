package fuse.pocs.camel.spring.properties;

import org.apache.camel.Body;

public class Prefixer {

    private final String prefix;

    public Prefixer(String prefix) {
        this.prefix = prefix;
    }

    public String process(@Body String message) {
        return prefix + message;
    }

}