# Proyecto: Sistema Bancario Java

Microservicios para la gestión de usuarios y cuentas bancarias.

## Ejecución local con Docker Compose

Para levantar todo el entorno localmente con PostgreSQL, RabbitMQ y ambos microservicios:

1. Verifica que exista el archivo `.env` en la raíz del proyecto. Puedes partir de `.env.example` si necesitas recrearlo.
2. Ejecuta el stack completo:

```bash
docker compose up --build -d
```

3. Verifica el estado de los contenedores:

```bash
docker compose ps
```

4. Si necesitas revisar logs:

```bash
docker compose logs -f
```

5. Para detener el entorno:

```bash
docker compose down
```

## Microservicios

### Bank Service

Servicio encargado de la gestión de productos financieros (cuentas).

- **Puerto local expuesto**: `8081`
- **Puerto interno del contenedor**: `8080`
- **Context Path / Base path REST**: `/api/v1`
- **Base de Datos**: PostgreSQL (`bank_db` en el servicio `bank_postgres`, expuesto en `localhost:5433`)

#### Endpoints Principales (Cuentas)

| Método | Path                                  | Descripción                             |
| ------ | ------------------------------------- | --------------------------------------- |
| POST   | `/api/v1/cuentas`                     | Crear una nueva cuenta                  |
| GET    | `/api/v1/cuentas/{id}`                | Obtener detalle de cuenta               |
| GET    | `/api/v1/cuentas/cliente/{clienteId}` | Listar cuentas de un cliente (paginado) |
| PUT    | `/api/v1/cuentas/{id}`                | Actualización total de cuenta           |
| PATCH  | `/api/v1/cuentas/{id}`                | Actualización parcial de cuenta         |
| DELETE | `/api/v1/cuentas/{id}`                | Eliminación lógica (soft-delete)        |

#### Endpoints Principales (Movimientos)

| Método | Path                                    | Descripción                                 |
| ------ | --------------------------------------- | ------------------------------------------- |
| POST   | `/api/v1/movimientos`                   | Registrar un nuevo movimiento (dep/ret)     |
| GET    | `/api/v1/movimientos/{id}`              | Obtener detalle de movimiento               |
| GET    | `/api/v1/movimientos/cuenta/{cuentaId}` | Listar movimientos de una cuenta (paginado) |
| PUT    | `/api/v1/movimientos/{id}`              | Actualización total de movimiento           |
| PATCH  | `/api/v1/movimientos/{id}`              | Actualización parcial de movimiento         |
| DELETE | `/api/v1/movimientos/{id}`              | Eliminación lógica (soft-delete)            |

#### Endpoints de Reportería

| Método | Path               | Parámetros                          | Descripción                          |
| ------ | ------------------ | ----------------------------------- | ------------------------------------ |
| GET    | `/api/v1/reportes` | `fecha` (rango), `clienteId` (UUID) | Obtener estado de cuenta consolidado |

### Users Service

Servicio encargado de la gestión de perfiles de usuario y sincronización.

- **Puerto local expuesto**: `8080`
- **Puerto interno del contenedor**: `8080`
- **Context Path / Base path REST**: `/api/v1`
- **Base de Datos**: PostgreSQL (`users_db` por defecto o `${DB_NAME}` en el servicio `postgres`, expuesto en `localhost:5432`)

#### Endpoints Principales (Personas)

| Método | Path                    | Descripción                      |
| ------ | ----------------------- | -------------------------------- |
| POST   | `/api/v1/personas`      | Registrar una nueva persona      |
| GET    | `/api/v1/personas/{id}` | Obtener detalle de una persona   |
| PUT    | `/api/v1/personas/{id}` | Actualización total de persona   |
| PATCH  | `/api/v1/personas/{id}` | Actualización parcial de persona |
| DELETE | `/api/v1/personas/{id}` | Eliminación lógica de persona    |

#### Endpoints Principales (Clientes)

| Método | Path                           | Descripción                      |
| ------ | ------------------------------ | -------------------------------- |
| POST   | `/api/v1/clientes`             | Crear un nuevo cliente           |
| GET    | `/api/v1/clientes/{clienteId}` | Obtener detalle de cliente       |
| PUT    | `/api/v1/clientes/{clienteId}` | Actualización total de cliente   |
| PATCH  | `/api/v1/clientes/{clienteId}` | Actualización parcial de cliente |
| DELETE | `/api/v1/clientes/{clienteId}` | Eliminación lógica de cliente    |

#### Servicios disponibles al levantar Docker Compose

| Servicio            | URL local base                 | Observaciones                              |
| ------------------- | ------------------------------ | ------------------------------------------ |
| Users API           | `http://localhost:8080/api/v1` | Gestión de personas y clientes             |
| Bank API            | `http://localhost:8081/api/v1` | Gestión de cuentas, movimientos y reportes |
| RabbitMQ Management | `http://localhost:15672`       | Consola de administración RabbitMQ         |

---

## Tecnologías

- **Java 17**
- **Spring Boot 4.0.6**
- **RabbitMQ**
- **PostgreSQL**
- **Docker & Docker Compose**
