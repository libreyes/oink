package uk.org.openeyes.oink.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class XmlTransformer {

	public static String transform(String inputXml, InputStream xslIs) throws TransformerFactoryConfigurationError, TransformerException, UnsupportedEncodingException {

		Source xslSource = new StreamSource(xslIs);

		InputStream inputXmlIs = new ByteArrayInputStream(inputXml.getBytes());
		Source inputXmlSource = new StreamSource(inputXmlIs);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Result outputXmlResult = new StreamResult(os);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer(xslSource);
		transformer.transform(inputXmlSource, outputXmlResult);
		
		return os.toString("UTF-8");
	}

}
