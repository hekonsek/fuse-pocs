import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

public class Xxx {

    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").unmarshal().json(JsonLibrary.Gson, Map.class).setBody().simple("${body[bar][baz]}").log("XXX${body}XXX");
            }
        });
        camelContext.start();
        camelContext.createProducerTemplate().sendBody("direct:start", "{bar: {baz: 1}}");
        Thread.sleep(5000);
    }

}
