-- ==============================================================
-- Script de Creación de Bases de Datos
-- Proyecto: Sistema Bancario (Microservicios)
--
-- Microservicio USERS  →  users_db
-- Microservicio BANK   →  bank_db
--
-- Motor: PostgreSQL 15
-- ==============================================================


-- ==============================================================
-- BASE DE DATOS: users_db  (Microservicio USERS)
-- ==============================================================

CREATE DATABASE users_db;

\connect users_db;

-- Tabla: personas
CREATE TABLE personas (
    id              UUID            NOT NULL,
    nombre          VARCHAR(255)    NOT NULL,
    genero          VARCHAR(255)    NOT NULL,
    edad            INTEGER         NOT NULL,
    identificacion  VARCHAR(255)    NOT NULL,
    direccion       VARCHAR(255)    NOT NULL,
    telefono        VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT pk_personas               PRIMARY KEY (id),
    CONSTRAINT uq_personas_identificacion UNIQUE      (identificacion)
);

-- Tabla: clientes
CREATE TABLE clientes (
    cliente_id  UUID            NOT NULL,
    persona_id  UUID            NOT NULL,
    contrasena  VARCHAR(255)    NOT NULL,
    estado      BOOLEAN         NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL,
    updated_at  TIMESTAMPTZ,
    deleted_at  TIMESTAMPTZ,
    CONSTRAINT pk_clientes           PRIMARY KEY (cliente_id),
    CONSTRAINT uq_clientes_persona   UNIQUE      (persona_id),
    CONSTRAINT fk_clientes_persona   FOREIGN KEY (persona_id) REFERENCES personas (id)
);

CREATE INDEX idx_clientes_deleted_at ON clientes (deleted_at);
CREATE INDEX idx_clientes_estado     ON clientes (estado);


-- ==============================================================
-- BASE DE DATOS: bank_db  (Microservicio BANK)
-- ==============================================================

CREATE DATABASE bank_db;

\connect bank_db;

-- Tabla: clientes  (réplica local sincronizada vía RabbitMQ)
CREATE TABLE clientes (
    cliente_id  UUID            NOT NULL,
    nombre      VARCHAR(255)    NOT NULL,
    estado      BOOLEAN         NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL,
    updated_at  TIMESTAMPTZ,
    deleted_at  TIMESTAMPTZ,
    CONSTRAINT pk_clientes PRIMARY KEY (cliente_id)
);

-- Tabla: cuentas
CREATE TABLE cuentas (
    cuenta_id       UUID            NOT NULL,
    cliente_id      UUID            NOT NULL,
    numero_cuenta   VARCHAR(255)    NOT NULL,
    tipo_cuenta     VARCHAR(255)    NOT NULL,
    saldo_inicial   NUMERIC(18, 2)  NOT NULL,
    estado          BOOLEAN         NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT pk_cuentas              PRIMARY KEY  (cuenta_id),
    CONSTRAINT uq_cuentas_numero       UNIQUE       (numero_cuenta),
    CONSTRAINT fk_cuentas_cliente      FOREIGN KEY  (cliente_id)  REFERENCES clientes (cliente_id),
    CONSTRAINT ck_cuentas_tipo_cuenta  CHECK        (tipo_cuenta IN ('AHORROS', 'CORRIENTE'))
);

-- Tabla: movimientos
CREATE TABLE movimientos (
    movimiento_id   UUID            NOT NULL,
    cuenta_id       UUID            NOT NULL,
    fecha           TIMESTAMPTZ     NOT NULL,
    tipo_movimiento VARCHAR(20)     NOT NULL,
    valor           NUMERIC(18, 2)  NOT NULL,
    saldo           NUMERIC(18, 2)  NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT pk_movimientos                   PRIMARY KEY (movimiento_id),
    CONSTRAINT fk_movimientos_cuenta            FOREIGN KEY (cuenta_id) REFERENCES cuentas (cuenta_id),
    CONSTRAINT ck_movimientos_tipo_movimiento   CHECK       (tipo_movimiento IN ('DEPOSITO', 'RETIRO'))
);

-- Tabla: processed_events  (idempotencia de mensajes RabbitMQ)
CREATE TABLE processed_events (
    event_id     VARCHAR(255)    NOT NULL,
    processed_at TIMESTAMPTZ     NOT NULL,
    CONSTRAINT pk_processed_events PRIMARY KEY (event_id)
);
