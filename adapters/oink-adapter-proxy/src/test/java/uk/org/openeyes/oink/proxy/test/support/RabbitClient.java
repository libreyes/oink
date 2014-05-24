package uk.org.openeyes.oink.proxy.test.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.openeyes.oink.domain.OINKRequestMessage;
import uk.org.openeyes.oink.domain.OINKResponseMessage;
import uk.org.openeyes.oink.domain.json.OinkRequestMessageJsonConverter;
import uk.org.openeyes.oink.messaging.OinkMessageConverter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

public class RabbitClient {

	ConnectionFactory factory;
	
	private static final Logger log = LoggerFactory.getLogger(RabbitClient.class);

	public RabbitClient(String host, int port, String virtualHost,
			String username, String password) {
		factory = new ConnectionFactory();
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setHost(host);
		factory.setPort(port);
		factory.setVirtualHost(virtualHost);
	}
	
	public OINKResponseMessage sendAndRecieve(OINKRequestMessage message, String routingKey, String exchange) throws Exception {
		OinkMessageConverter conv = new OinkMessageConverter();
		byte[] msgBytes = conv.toByteArray(message);
		byte[] msgResp = sendAndRecieve(msgBytes, routingKey, exchange);
		if (msgResp == null) {
			throw new Exception("No response received");
		}
		return conv.responseMessageFromByteArray(msgResp);
	}

	public byte[] sendAndRecieve(byte[] message, String routingKey,
			String exchange) throws Exception {
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(exchange, "direct", true, false, null);
		String replyQueueName = channel.queueDeclare().getQueue();
		channel.queueBind(replyQueueName, exchange, replyQueueName);
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
		String corrId = java.util.UUID.randomUUID().toString();
		BasicProperties props = new BasicProperties.Builder()
				.correlationId(corrId).replyTo(replyQueueName).build();

		channel.basicPublish(exchange, routingKey, props, message);
		log.debug("Waiting for delivery");
		QueueingConsumer.Delivery delivery = consumer.nextDelivery(10000);
		connection.close();
		if (delivery == null
				|| !delivery.getProperties().getCorrelationId()
						.equals(corrId)) {
			return null;
		} else {
			byte[] response = delivery.getBody();
			return response;

		}
	}

}