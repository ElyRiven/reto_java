package com.banco.bank.infrastructure.messaging.in;

import com.banco.bank.domain.model.events.ClienteEvent;
import com.banco.bank.domain.port.in.SincronizarClienteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ClienteEventConsumerAdapterTest {

    @Mock
    private SincronizarClienteUseCase sincronizarClienteUseCase;

    @InjectMocks
    private ClienteEventConsumerAdapter adapter;

    @Test
    void debe_DelegarCasoDeUso_Cuando_EventoEsValido() {
        var event = buildEvent("evt-100");

        adapter.consume(event);

        verify(sincronizarClienteUseCase).execute(event);
    }

    @Test
    void debe_FallarRapido_Cuando_EventoEsMalformadoSinMetadata() {
        var malformed = new ClienteEvent(null,
                new ClienteEvent.Payload(UUID.randomUUID(), "Ana", true, "CREATE_OR_UPDATE"));

        assertThrows(NullPointerException.class, () -> adapter.consume(malformed));
        verifyNoInteractions(sincronizarClienteUseCase);
    }

    private ClienteEvent buildEvent(String eventId) {
        var metadata = new ClienteEvent.Metadata(eventId, Instant.now(), "1.0");
        var payload = new ClienteEvent.Payload(UUID.randomUUID(), "Ana", true, "CREATE_OR_UPDATE");
        return new ClienteEvent(metadata, payload);
    }
}
