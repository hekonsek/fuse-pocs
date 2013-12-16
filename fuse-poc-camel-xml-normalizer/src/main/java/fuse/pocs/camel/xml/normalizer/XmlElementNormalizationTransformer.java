package fuse.pocs.camel.xml.normalizer;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.camel.Body;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlElementNormalizationTransformer {

    public String process(@Body String xml) throws Exception {
        StringWriter transformedXml = new StringWriter();
        Source source = new StreamSource(new StringReader(xml));
        Transformer transformer = new TransformerFactoryImpl().newTransformer();
        transformer.transform(source, new StreamResult(transformedXml));
        transformedXml.flush();
        return transformedXml.toString();
    }

}