package camelpoc;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class TmpXxx {

    public static void main(String[] args) throws Exception {
        String xml = "<Parent>\n" +
                "\t<Child A=\"1\" \n" +
                "\tB=\"2\"/>\n" +
                "<text>foo\nbar</text>" +
                "</Parent>";
//        CamelContext camelContext = new DefaultCamelContext();
//        camelContext.addRoutes(new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                ZipFileDataFormat zipFile = new ZipFileDataFormat();
//                zipFile.setUsingIterator(true);
//                from("file:/home/hkonsek/Desktop?fileName=1386610227.zip").unmarshal(zipFile).split(body(Iterator.class)).streaming().process(new Processor() {
//                    @Override
//                    public void process(Exchange exchange) throws Exception {
//                        InputStream inputStream = (InputStream) exchange.getIn().getBody();
//                        inputStream.read();
//                        System.out.println();
//                    }
//                });
//            }
//        });
//        camelContext.start();
//        Thread.sleep(100000);
        System.out.println(xml);
//        System.out.println(xml.replaceAll("\\s+<","<").replaceAll("(\\s*\\n)(?<!(<.*?>))", " "));

        xml = xml.replaceAll("\\s+<", "<");
        StringWriter sw = new StringWriter();
        Source source = new StreamSource(new StringReader(xml));
        TransformerFactory tf = new TransformerFactoryImpl();
        Transformer trans = tf.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, false ? "yes" : "no");

        trans.transform(source, new StreamResult(sw));
        sw.flush();
        String res = sw.toString();
        System.out.println(res);
    }

}
