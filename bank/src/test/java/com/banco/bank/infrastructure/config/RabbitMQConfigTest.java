package com.banco.bank.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RabbitMQConfigTest {

    private RabbitMQConfig config;

    @BeforeEach
    void setUp() {
        config = new RabbitMQConfig();
        ReflectionTestUtils.setField(config, "exchangeName", "users.v1.exchange");
        ReflectionTestUtils.setField(config, "queueName", "bank-sync-clientes.queue");
        ReflectionTestUtils.setField(config, "dlqName", "bank-sync-clientes.dlq");
        ReflectionTestUtils.setField(config, "routingKey", "user.cliente.*");
    }

    @Test
    void debe_ConfigurarQueueConDLQ_Cuando_SeCreaClienteQueue() {
        var queue = config.clienteQueue();

        assertEquals("bank-sync-clientes.queue", queue.getName());
        assertNotNull(queue.getArguments());
        assertEquals("", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("bank-sync-clientes.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void debe_ConfigurarBindingConRoutingKeyEsperado() {
        Queue queue = config.clienteQueue();
        TopicExchange exchange = config.clienteExchange();
        var binding = config.clienteBinding(queue, exchange);

        assertEquals("bank-sync-clientes.queue", binding.getDestination());
        assertEquals("users.v1.exchange", binding.getExchange());
        assertEquals("user.cliente.*", binding.getRoutingKey());
    }
}
