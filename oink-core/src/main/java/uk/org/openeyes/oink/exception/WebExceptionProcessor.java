package uk.org.openeyes.oink.exception;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom exception handler that sets the HTTP response code to be returned to
 * the caller using annotation inspection of the exception.
 * 
 * @author Oliver Wilkie
 */
public class WebExceptionProcessor implements Processor {

	private static final int DEFAULT_ERROR_CODE = 500;

	private static final Logger logger = LoggerFactory
			.getLogger(WebExceptionProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
				Exception.class);
		
		if (e == null) {
			return;
		}

		int errorCode = DEFAULT_ERROR_CODE;

		if (e.getClass().isAnnotationPresent(HttpStatusCode.class)) {
			HttpStatusCode statusCode = e.getClass().getAnnotation(
					HttpStatusCode.class);
			errorCode = statusCode.value();
			logger.info("Setting HTTP Response Code to " + errorCode
					+ " due to exception: " + e.getClass().getName());
		} else {
			logger.info("Setting HTTP Response Code to default code "
					+ DEFAULT_ERROR_CODE + " because the exception "
					+ e.getClass().getName()
					+ " is not associated with a response code");
		}

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, errorCode);
		exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/plain");
		exchange.getOut().setBody(e.getMessage());
	}

}
