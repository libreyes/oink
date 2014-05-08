package uk.org.openeyes.oink.hl7v2.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ACK;
import uk.org.openeyes.oink.domain.OINKRequestMessage;
import uk.org.openeyes.oink.hl7v2.A28Processor;
import uk.org.openeyes.oink.hl7v2.A31Processor;
import uk.org.openeyes.oink.hl7v2.A40Processor;
import uk.org.openeyes.oink.hl7v2.Hl7v2Processor;
import uk.org.openeyes.oink.messaging.OinkMessageConverter;

/**
 * 
 * Tests the main HLv2 routing whilst mocking the {@link Hl7v2Processor}
 * processors which are responsible for converting the received (unvalidated)
 * {@link Message} into the appropriate {@link OINKRequestMessage}.
 * 
 * This test has no dependencies on the OpenMapsSW part.
 * 
 * The test methods here are to ensure that the Route is correctly configured.
 * 
 * @author Oliver Wilkie
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class TestHl7v2RouteWithoutProcessors extends Hl7TestSupport {

	@Autowired
	A28Processor a28Processor;

	@Autowired
	A31Processor a31Processor;

	@Autowired
	A40Processor a40Processor;

	ConnectionFactory rabbitFactory;

	@Before
	public void setUp() throws IOException {
		setProperties("/hl7v2-test.properties");
		rabbitFactory = initRabbit(getProperty("rabbit.host"),
				Integer.parseInt(getProperty("rabbit.port")),
				getProperty("rabbit.username"), getProperty("rabbit.password"),
				getProperty("rabbit.vhost"));
	}

	@Test
	@DirtiesContext
	public void testA28MessageRoutesOntoRabbit() throws Exception {

		// Choose a message to send
		Message m = loadMessage("/samples/A28-1.txt");

		// Prepare mocks
		OINKRequestMessage mockRequest = new OINKRequestMessage();
		when(a28Processor.process(any(Message.class))).thenReturn(mockRequest);

		OINKRequestMessage req = testGivenMessageRoutesOntoRabbit(m, mockRequest);

		// Check mocks
		verify(a28Processor).process(any(Message.class));
		verify(a31Processor, never()).process(any(Message.class));
		verify(a40Processor, never()).process(any(Message.class));
	}
	
	@Test
	@DirtiesContext
	public void testA31MessageRoutesOntoRabbit() throws Exception {
		// Choose a message to send
		Message m = loadMessage("/samples/A31-1.txt");

		// Prepare mocks
		OINKRequestMessage mockRequest = new OINKRequestMessage();
		when(a31Processor.process(any(Message.class))).thenReturn(mockRequest);

		OINKRequestMessage req = testGivenMessageRoutesOntoRabbit(m, mockRequest);

		// Check mocks
		verify(a31Processor).process(any(Message.class));
		verify(a28Processor, never()).process(any(Message.class));
		verify(a40Processor, never()).process(any(Message.class));
	}
	
	@Test
	@DirtiesContext
	public void testA40MessageRoutesOntoRabbit() throws Exception {
		// Choose a message to send
		Message m = loadMessage("/samples/A40-1.txt");

		// Prepare mocks
		OINKRequestMessage mockRequest = new OINKRequestMessage();
		when(a40Processor.process(any(Message.class))).thenReturn(mockRequest);

		OINKRequestMessage req = testGivenMessageRoutesOntoRabbit(m, mockRequest);

		// Check mocks
		verify(a40Processor).process(any(Message.class));
		verify(a28Processor, never()).process(any(Message.class));
		verify(a31Processor, never()).process(any(Message.class));
	}
	
	private OINKRequestMessage testGivenMessageRoutesOntoRabbit(Message m, OINKRequestMessage r) throws Exception {
		// Init Rabbit listener
		Channel c = getChannel(rabbitFactory);
		String queueName = setupRabbitQueue(c,
				getProperty("rabbit.defaultExchange"),
				getProperty("rabbit.routingKey"));
		
		// Send message
		String host = getProperty("hl7v2.host");
		int port = Integer.parseInt(getProperty("hl7v2.port"));
		Message responseMessage = sendHl7Message(m, host, port);
		ACK acknowledgement = (ACK) responseMessage;
		assertEquals("AA", acknowledgement.getMSA().getAcknowledgementCode().getValue());

		// Consume message from rabbit
		byte[] body = receiveRabbitMessage(c, queueName, 1000);
		
		// Close rabbit connection
		c.close();

		// Check message
		OinkMessageConverter conv = new OinkMessageConverter();
		return conv.fromByteArray(body);
	}
	
	@Test
	public void testA04MessageDoesNotRouteOntoRabbit() throws Exception {
		// Init Rabbit listener
		Channel c = getChannel(rabbitFactory);
		String queueName = setupRabbitQueue(c,
				getProperty("rabbit.defaultExchange"),
				getProperty("rabbit.routingKey"));

		// Choose a message to send
		Message m = loadMessage("/samples/A04-1.txt");
		
		// Send message
		String host = getProperty("hl7v2.host");
		int port = Integer.parseInt(getProperty("hl7v2.port"));
		Message responseMessage = sendHl7Message(m, host, port);
		ACK acknowledgement = (ACK) responseMessage;
		assertEquals("AR", acknowledgement.getMSA().getAcknowledgementCode().getValue());

		// Consume message from rabbit
		byte[] body = receiveRabbitMessage(c, queueName, 1000);
		assertNull(body);
		
		// Close rabbit connection
		c.close();

		// Check mocks
		verify(a28Processor, never()).process(any(Message.class));
		verify(a31Processor, never()).process(any(Message.class));
		verify(a40Processor, never()).process(any(Message.class));
	}

}