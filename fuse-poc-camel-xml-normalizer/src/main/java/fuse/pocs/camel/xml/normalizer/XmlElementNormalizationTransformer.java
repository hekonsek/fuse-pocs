package fuse.pocs.camel.xml.normalizer;

import org.apache.camel.Body;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;

public class XmlElementNormalizationTransformer {

    public String process(@Body InputStream xml) throws Exception {
        StringWriter transformedXml = new StringWriter();
        Source source = new StreamSource(xml);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, new StreamResult(transformedXml));
        transformedXml.flush();
        return transformedXml.toString();
    }

}