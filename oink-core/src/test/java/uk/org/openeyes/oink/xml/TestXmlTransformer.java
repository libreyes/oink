package uk.org.openeyes.oink.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestXmlTransformer {
	
	
	@Ignore
	@Test
	public void testSimpleTransform() throws IOException, TransformerFactoryConfigurationError, TransformerException {

		String inputXml = getResourceAsString("/example-messages/hl7v2/A28-3.xml");
		InputStream xsl = TestXmlTransformer.class.getResourceAsStream("/uk/org/openeyes/oink/hl7v2/A28.xsl");
		
		XmlTransformer transformer = new XmlTransformer();
		String result = transformer.transform(inputXml,xsl);
		fail("Not yet implemented");
	}
	
	private static String getResourceAsString(String s) throws IOException {
		InputStream is = TestXmlTransformer.class.getResourceAsStream(s);
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		return writer.toString();
	}

}
