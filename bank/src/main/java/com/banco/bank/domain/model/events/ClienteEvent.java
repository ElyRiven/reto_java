package com.banco.bank.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEvent {

    private Metadata metadata;
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private String eventId;
        private Instant occurredAt;
        private String version;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private UUID clienteId;
        private String nombre;
        private Boolean estado;
        private String action;
    }
}
