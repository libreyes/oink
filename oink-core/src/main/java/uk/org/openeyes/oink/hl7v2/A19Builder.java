package uk.org.openeyes.oink.hl7v2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.openeyes.oink.common.RandomStringGenerator;
import uk.org.openeyes.oink.domain.OINKRequestMessage;
import uk.org.openeyes.oink.domain.OINKResponseMessage;
import uk.org.openeyes.oink.exception.OinkException;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.message.QRY_A19;
import ca.uhn.hl7v2.model.v24.segment.QRD;

public class A19Builder {

	private final static Logger log = LoggerFactory.getLogger(A19Builder.class);
	
	private RandomStringGenerator queryIdGenerator = new RandomStringGenerator(8);

	public Message buildQuery(OINKRequestMessage request) throws HL7Exception,
			IOException, OinkException {
		// Validate OINKRequestMessage

		QRY_A19 msg = new QRY_A19();

		// MSH segment
		msg.initQuickstart("QRY", "A19", "P");

		// QRD segment
		QRD qrd = msg.getQRD();
		// Set query time
		TS ts = qrd.getQueryDateTime();
		ts.getTs1_TimeOfAnEvent().setValue(new GregorianCalendar());
		// Set query format code
		qrd.getQueryFormatCode().setValue("R");
		// Set query priority
		qrd.getQueryPriority().setValue("I");
		// Set query id
		String queryId = queryIdGenerator.nextString();
		qrd.getQueryID().setValue(queryId);
		// Set quantity limited request
		qrd.getQuantityLimitedRequest().getQuantity().setValue("10");
		qrd.getQuantityLimitedRequest().getUnits().getIdentifier()
				.setValue("RD");
		// !! Set who subject filter
		if (isSearchByNHSNumber(request)) {
			log.info("Building an A19 with search by NHS number");
			String nhsNumber = extractNHSNumber(request);
			XCN who0 = qrd.getWhoSubjectFilter(0);
			who0.getIDNumber().setValue(nhsNumber);
			who0.getAssigningAuthority().getUniversalID().setValue("NHS");
			who0.getIdentifierTypeCode().setValue("MR");
		} else if (isSearchByFamilyName(request)) {
			log.info("Building an A19 with search by family name");
			String familyName = getQueryParameterValue(request, "family");
			XCN who0 = qrd.getWhoSubjectFilter(0);
			who0.getFamilyName().getSurname().setValue(familyName);
		} else {
			throw new OinkException("Only search by NHS number currently supported");
		}
		// Set what subject filter
		qrd.getWhatSubjectFilter(0).getIdentifier().setValue("DEM");
		// Set what department data code SKIP?

		// QRF
		// None

		return msg;
	}
	
	private String getQueryParameterValue(OINKRequestMessage request, String key) {
		List<NameValuePair> params = URLEncodedUtils.parse(
				request.getParameters(), Charset.forName("UTF-8"));
		for (NameValuePair param : params) {
			if (param.getName().equals(key)) {
				String value = param.getValue();
				return value;
			}
		}
		return null;
	}

	private boolean isSearchByNHSNumber(OINKRequestMessage request) {
		String idvalue = getQueryParameterValue(request, "identifier");
		if (idvalue != null) {
			return idvalue.startsWith("NHS");
		}
		return false;
	}
	
	private boolean isSearchByFamilyName(OINKRequestMessage request) {
		String idvalue = getQueryParameterValue(request, "family");
		return idvalue != null;	
	}
	
	private String extractNHSNumber(OINKRequestMessage request) {
		String value = getQueryParameterValue(request, "identifier");
		if (value == null) {
			return null;
		}
		String[] split = value.split("\\|");
		if (split.length == 2) {
			String nhsNumber = split[1];
			return nhsNumber;
		}
		return null;
		
	}

	public OINKResponseMessage processResponse(Message message) {
		return null;
	}

}
