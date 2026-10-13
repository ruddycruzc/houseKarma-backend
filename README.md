# HouseKarma | Backend

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.1.1">
  <img src="https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white" alt="Spring Security">
  <img src="https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white" alt="MySQL 8">
  <img src="https://img.shields.io/badge/Flyway-Migrations-CC0200?logo=flyway&logoColor=white" alt="Flyway">
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white" alt="Docker Compose">
  <img src="https://img.shields.io/badge/Status-In%20development-orange" alt="In development">
</p>

> **HouseKarma** es una plataforma de reputación y confianza para el mercado del alquiler. Su objetivo es facilitar relaciones más transparentes entre inquilinos y caseros mediante perfiles, experiencias de alquiler y un sistema de reputación mutua.

Este repositorio contiene el backend de HouseKarma, desarrollado con Java y Spring Boot. Proporciona la API REST, la lógica de negocio, la autenticación y el acceso a la base de datos.

---

## Índice

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Instalación y configuración](#instalación-y-configuración)
- [Base de datos y migraciones](#base-de-datos-y-migraciones)
- [Autenticación y seguridad](#autenticación-y-seguridad)
- [API REST](#api-rest)
- [Pruebas](#pruebas)
- [Integración con el frontend](#integración-con-el-frontend)
- [Roadmap](#roadmap)
- [Buenas prácticas](#buenas-prácticas)
- [Documentación](#documentación)

---

## Características

### Implementación actual

El backend está organizado en cuatro módulos funcionales principales:

- **Autenticación:** login, servicios JWT y filtro de autenticación.
- **Usuarios:** registro, perfiles, roles y operaciones relacionadas con los usuarios.
- **Viviendas:** gestión y consulta de viviendas.
- **Experiencias de alquiler:** estructuras y operaciones para gestionar experiencias vinculadas al alquiler.

También dispone de:

- Configuración de seguridad con Spring Security.
- Gestión de contraseñas mediante BCrypt.
- DTOs para las peticiones y respuestas.
- Repositorios para el acceso a datos.
- Tratamiento global de excepciones.
- Migraciones de base de datos versionadas con Flyway.
- Pruebas automatizadas organizadas por módulos.

> [!NOTE]
> La existencia de una clase o módulo no implica que todas sus funcionalidades estén terminadas o conectadas al frontend. El estado de cada operación se contrastará con el código y las pruebas correspondientes.

---

## Tecnologías

| Tecnología | Finalidad |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot 4.1.1 | Framework del backend |
| Spring Security | Seguridad y autorización |
| JWT | Autenticación mediante tokens |
| BCrypt | Codificación segura de contraseñas |
| Spring Data JPA | Persistencia y acceso a datos |
| MySQL 8 | Base de datos relacional |
| Flyway | Versionado de migraciones |
| Maven | Dependencias, compilación y pruebas |
| Docker Compose | Configuración de servicios en contenedores |
| Postman | Pruebas manuales de la API |
| DBeaver | Inspección y consultas de base de datos |
| JUnit / herramientas de testing de Spring | Pruebas automatizadas |

---

## Arquitectura

El backend utiliza una arquitectura modular organizada por funcionalidades, con separación de responsabilidades.

```text
Cliente Angular
      |
      v
   API REST
      |
      v
  Controllers
      |
      v
   Services
      |
      v
  Repositories
      |
      v
     MySQL

Seguridad transversal:
Spring Security + JWT

Persistencia:
JPA + Flyway
```

### Responsabilidad de las capas

| Capa | Responsabilidad |
|---|---|
| `controller/` | Recibir y responder a las peticiones HTTP. |
| `service/` | Coordinar la lógica de negocio. |
| `repository/` | Gestionar el acceso a los datos. |
| `entity/` | Representar las entidades del dominio. |
| `dto/` | Definir los datos de entrada y salida de la API. |
| `filter/` | Intervenir en el procesamiento de las peticiones, incluida la autenticación JWT. |
| `exception/` | Agrupar excepciones específicas. |
| `config/` | Centralizar configuraciones, seguridad y tratamiento global de errores. |

Esta separación facilita el mantenimiento, reduce el acoplamiento entre responsabilidades y permite probar cada módulo de forma independiente.

---

## Estructura del proyecto

La siguiente estructura resume los principales directorios y archivos del backend.

```text
houseKarma-backend/
├── .mvn/
│   └── wrapper/
├── compose.yaml
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/housekarma/housekarma/
    │   │   ├── auth/
    │   │   │   ├── controller/
    │   │   │   ├── dto/
    │   │   │   ├── exception/
    │   │   │   ├── filter/
    │   │   │   └── service/
    │   │   ├── config/
    │   │   ├── experiencia/
    │   │   │   ├── controller/
    │   │   │   ├── dto/
    │   │   │   ├── entity/
    │   │   │   ├── repository/
    │   │   │   └── service/
    │   │   ├── usuario/
    │   │   │   ├── controller/
    │   │   │   ├── dto/
    │   │   │   ├── entity/
    │   │   │   ├── repository/
    │   │   │   └── service/
    │   │   ├── vivienda/
    │   │   │   ├── controller/
    │   │   │   ├── dto/
    │   │   │   ├── entity/
    │   │   │   ├── repository/
    │   │   │   └── service/
    │   │   └── HousekarmaApplication.java
    │   └── resources/
    │       ├── application.properties
    │       ├── application-dev.properties
    │       ├── application-prod.properties
    │       └── db/migration/
    │           ├── V1__create_usuario.sql
    │           ├── V2__create_vivienda_and_experiencia_alquiler.sql
    │           └── V3__add_casero_to_vivienda.sql
    └── test/
        └── java/com/housekarma/housekarma/
            ├── auth/
            ├── config/
            ├── experiencia/
            ├── usuario/
            └── vivienda/
```

### Archivos principales

- `HousekarmaApplication.java`: punto de entrada de Spring Boot.
- `pom.xml`: dependencias y configuración de Maven.
- `compose.yaml`: definición de los servicios del entorno Docker Compose.
- `application.properties`: configuración general.
- `application-dev.properties`: configuración del entorno de desarrollo.
- `application-prod.properties`: configuración del entorno de producción.
- `db/migration/`: migraciones SQL gestionadas por Flyway.

---

## Requisitos previos

Antes de iniciar el proyecto, es necesario disponer de:

- [Java 21](https://adoptium.net/).
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) o Docker Engine con Docker Compose.
- Git.
- [OpenSSL](https://www.openssl.org/), disponible en Git Bash en muchas instalaciones de Git para Windows.
- [Postman](https://www.postman.com/) para probar la API.
- [DBeaver](https://dbeaver.io/) para inspeccionar la base de datos.

---

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd houseKarma-backend
```

Sustituye `<URL_DEL_REPOSITORIO>` por la URL real del repositorio.

### 2. Configurar las variables de entorno

Configura localmente las variables que necesiten Spring Boot y Docker Compose, siguiendo los nombres definidos en los archivos de configuración del proyecto.

Si utilizas un archivo `.env`, mantenlo fuera del control de versiones.

### 3. Generar una clave JWT propia

HouseKarma utiliza una clave secreta para firmar y validar los tokens JWT. Cada desarrollador debe generar su propia clave y guardarla localmente.

> [!WARNING]
> No reutilices claves publicadas ni compartas tu clave JWT. No la escribas directamente en el código, el README ni los commits de Git.

Desde **Git Bash**, sitúate en la raíz del backend, donde se encuentra el archivo `.env`, y ejecuta:

```bash
key="$(openssl rand -base64 64)"

if grep -q '^JWT_KEY=' .env; then
  sed -i "s|^JWT_KEY=.*|JWT_KEY=$key|" .env
else
  printf '\nJWT_KEY=%s\n' "$key" >> .env
fi
```

Este comando:

1. Genera 64 bytes aleatorios con OpenSSL y los codifica en Base64.
2. Comprueba si `JWT_KEY` ya existe en `.env`.
3. Si existe, sustituye su valor.
4. Si no existe, añade la variable al final del archivo.

El resultado es una clave generada localmente, sin necesidad de copiarla desde el README.

#### Comprobar que la variable existe

Puedes verificar que la clave está configurada sin mostrar su contenido:

```bash
grep -q '^JWT_KEY=.' .env \
  && echo "JWT_KEY configurada" \
  || echo "JWT_KEY no configurada"
```

> [!CAUTION]
> Ejecutar de nuevo el generador cambia la clave. Los tokens firmados con la clave anterior dejarán de validarse si la aplicación pasa a utilizar la nueva.

### 4. Conectar la variable con Spring Boot

Comprueba que `application.properties` utiliza el nombre de variable correcto.

Por ejemplo, si la propiedad que consume la aplicación es `jwt.secret`:

```properties
jwt.secret=${JWT_KEY}
```

Esta propiedad es ilustrativa. El nombre definitivo debe coincidir con la configuración utilizada por `JwtService.java`.

**Importante:** crear un archivo `.env` no garantiza que Spring Boot lo cargue automáticamente. Si ejecutas el backend directamente con Maven, debes comprobar cómo se proporcionan las variables al proceso. Si utilizas Docker Compose, comprueba que la variable se transmite al servicio del backend.

### 5. Proteger los archivos locales

Añade estas reglas a `.gitignore`:

```gitignore
# Variables de entorno y secretos
.env
.env.*
!.env.example
```

Si necesitas compartir los nombres de las variables, crea un `.env.example` con valores ficticios:

```dotenv
JWT_KEY=REPLACE_WITH_YOUR_OWN_SECRET
```

Comprueba que Git ignora el archivo real:

```bash
git check-ignore -v .env
git status
```

Si `.env` ya estaba versionado, retíralo del índice sin borrar tu copia local:

```bash
git rm --cached .env
```

> [!WARNING]
> `.gitignore` no elimina secretos de los commits anteriores. Si una clave real se ha publicado, genera una nueva y revisa la exposición de la anterior.

### 6. Iniciar los servicios

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Comprueba el estado de los contenedores:

```bash
docker compose ps
```

Los nombres de los servicios, los puertos y las variables necesarias dependen de la configuración actual de `compose.yaml`.

### 7. Ejecutar el backend

En Windows, utilizando PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

Consulta los archivos de configuración para identificar el puerto, el perfil activo y los datos de conexión.

### 8. Ejecutar las pruebas

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

Maven mostrará el resultado de la ejecución de la suite de pruebas.

---

## Base de datos y migraciones

HouseKarma utiliza MySQL como base de datos relacional y Flyway para versionar los cambios de su estructura.

### Migraciones existentes

| Migración | Cambio registrado |
|---|---|
| `V1__create_usuario.sql` | Creación inicial de la estructura de usuarios. |
| `V2__create_vivienda_and_experiencia_alquiler.sql` | Creación de las estructuras de viviendas y experiencias de alquiler. |
| `V3__add_casero_to_vivienda.sql` | Incorporación del casero a la estructura de viviendas. |

Flyway permite mantener un historial de los cambios del esquema y aplicar las migraciones en el orden establecido.

### Comprobaciones con DBeaver

DBeaver se utiliza para inspeccionar MySQL y verificar la persistencia de los datos.

Las comprobaciones que se documentarán incluyen:

- Conexión a la base de datos correcta.
- Existencia de las tablas esperadas.
- Relaciones y restricciones del esquema.
- Estado de las migraciones registradas por Flyway.
- Datos generados mediante peticiones de prueba.
- Correspondencia entre las operaciones de la API y los datos almacenados.

> [!TIP]
> Para documentar una prueba de persistencia, registra la operación realizada en Postman, consulta después el registro correspondiente en DBeaver y anota el resultado. Así se puede seguir el recorrido de una petición desde la API hasta la base de datos.

---

## Autenticación y seguridad

La aplicación utiliza Spring Security y JWT para gestionar la autenticación.

Entre las clases relacionadas con esta funcionalidad se encuentran:

| Clase | Responsabilidad |
|---|---|
| `SecurityConfig` | Configuración de seguridad de la aplicación. |
| `AuthController` | Endpoints relacionados con la autenticación. |
| `AuthService` | Lógica del proceso de autenticación. |
| `JwtService` | Operaciones relacionadas con los tokens JWT. |
| `JwtAuthenticationFilter` | Filtro de autenticación de peticiones. |
| `GlobalExceptionHandler` | Tratamiento global de excepciones. |

Las contraseñas se gestionan mediante BCrypt y no deben almacenarse en texto plano.

### Uso del token

En las peticiones que requieran autenticación, el cliente deberá enviar el token JWT mediante la cabecera HTTP correspondiente:

```http
Authorization: Bearer <TOKEN_JWT>
```

La aplicación debe validar el token y aplicar las reglas de acceso configuradas para cada endpoint.

> [!IMPORTANT]
> La lista de endpoints públicos y protegidos, los permisos por rol y las respuestas ante tokens inválidos o caducados deben documentarse a partir de `SecurityConfig` y las pruebas de seguridad.

---

## API REST

El backend organiza sus endpoints en cuatro controladores principales:

| Módulo | Controlador |
|---|---|
| Autenticación | `AuthController` |
| Usuarios | `UsuarioController` |
| Viviendas | `ViviendaController` |
| Experiencias de alquiler | `ExperienciaAlquilerController` |

### Operaciones identificadas

Se han registrado pruebas de las siguientes operaciones:

| Operación | Método | Resultado registrado |
|---|---|---|
| Inicio de sesión | `POST` | `200 OK` |
| Consulta de viviendas | `GET` | `200 OK` |
| Consulta de una vivienda por ID | `GET` | `404 Not Found` para el identificador probado |

> [!NOTE]
> Las rutas completas, los cuerpos JSON, los parámetros y los requisitos de autenticación se incorporarán tras revisar los controladores y contrastar las peticiones reales de Postman. El `404 Not Found` registrado no permite concluir por sí solo que el endpoint esté mal implementado: es necesario comprobar si el recurso solicitado existía.

El catálogo técnico completo de la API se mantendrá en la Wiki de GitHub.

Cada endpoint contará con:

- Método HTTP y ruta.
- Descripción de la operación.
- Parámetros y cuerpo de la petición.
- Ejemplo de respuesta.
- Códigos HTTP posibles.
- Requisitos de autenticación y autorización.
- Ejemplo de petición en Postman.
- Ejemplo de consumo desde Angular.

---

## Pruebas

El backend dispone de pruebas automatizadas organizadas por módulos dentro de `src/test/java/com/housekarma/housekarma/`.

Se incluyen pruebas relacionadas con:

- Autenticación y servicios JWT.
- Filtros y configuración de seguridad.
- Controladores, servicios y repositorios de usuarios.
- Validación de DTOs.
- Controladores, servicios y repositorios de viviendas.
- Controladores, servicios y repositorios de experiencias de alquiler.
- Gestión global de excepciones.

### Herramientas de comprobación manual

| Herramienta | Objetivo |
|---|---|
| Postman | Enviar peticiones HTTP y comprobar las respuestas de la API. |
| DBeaver | Inspeccionar la base de datos y verificar los datos persistidos. |
| Maven | Ejecutar las pruebas automatizadas del backend. |

Cada prueba manual deberá indicar qué operación se ha ejecutado, qué respuesta se ha recibido y qué cambios se han observado en la base de datos, cuando corresponda.

El número de pruebas superadas se actualizará según la última ejecución real de Maven.

---

## Integración con el frontend

El frontend de HouseKarma utiliza Angular. La comunicación con el backend se realiza mediante peticiones HTTP a la API REST.

La integración debe contemplar:

1. Configurar la URL base del backend para el entorno correspondiente.
2. Utilizar `HttpClient` para realizar las peticiones.
3. Centralizar las llamadas en servicios Angular.
4. Definir interfaces TypeScript que representen los DTOs reales.
5. Enviar el JWT en las peticiones protegidas cuando corresponda.
6. Gestionar errores HTTP, estados de carga y respuestas de autenticación.
7. Consumir los servicios desde los componentes que necesiten los datos.
8. Verificar los permisos en el backend, sin utilizar la interfaz como única barrera de protección.

### Ejemplo conceptual

Una vez confirmados el endpoint y su respuesta, un servicio Angular puede centralizar la consulta:

```typescript
@Injectable({
  providedIn: 'root'
})
export class ViviendaService {
  private readonly http = inject(HttpClient);

  // La URL y el tipo de respuesta deben ajustarse
  // al contrato real de la API.
}
```

Este fragmento es ilustrativo y no representa una implementación completa.

> [!IMPORTANT]
> La URL base, las interfaces TypeScript y las llamadas concretas se documentarán a partir del contrato real de la API. No se deben inventar campos o respuestas para hacer coincidir el backend con el frontend.

---

## Roadmap

Las siguientes funcionalidades forman parte de la evolución prevista de HouseKarma. Su inclusión en este apartado no implica que estén implementadas.

### Confianza y experiencia de alquiler

- [ ] Completar el flujo de registro de experiencias de alquiler.
- [ ] Definir el proceso de confirmación de la experiencia por las partes implicadas.
- [ ] Implementar el sistema de valoraciones y reputación mutua.
- [ ] Definir reglas para valorar únicamente experiencias válidas.

### Consentimiento y políticas de seguridad

- [ ] Diseñar un flujo explícito de consentimiento entre inquilino y casero antes de confirmar una experiencia compartida.
- [ ] Definir qué información puede consultar cada parte y en qué condiciones.
- [ ] Implementar controles de autorización en el backend para proteger los datos personales.
- [ ] Establecer reglas para prevenir valoraciones abusivas o manipuladas.
- [ ] Definir mecanismos de revisión, moderación y gestión de reclamaciones.
- [ ] Documentar las políticas de privacidad, tratamiento de datos y conservación de información antes de la puesta en producción.

### Comunicación entre usuarios

- [ ] Diseñar e implementar un chat interactivo entre las partes cuando corresponda.
- [ ] Definir las reglas de acceso a las conversaciones y sus participantes.
- [ ] Establecer medidas para proteger la privacidad y gestionar posibles abusos.
- [ ] Evaluar las necesidades de comunicación en tiempo real y las tecnologías apropiadas.

### Administración y mantenimiento

- [ ] Completar las herramientas de administración y moderación previstas.
- [ ] Mejorar la trazabilidad de las operaciones relevantes.
- [ ] Ampliar las pruebas automatizadas de integración y seguridad.
- [ ] Documentar y verificar todos los endpoints consumidos por el frontend.
- [ ] Revisar la configuración de producción, los permisos y la gestión de secretos antes del despliegue definitivo.

> [!WARNING]
> Las políticas de consentimiento, el chat y los mecanismos de moderación requieren un diseño funcional y de seguridad antes de considerarse listos para producción. Deben existir reglas de autorización, validaciones y pruebas que demuestren su comportamiento.

---

## Buenas prácticas

Durante el desarrollo se toman como referencia los siguientes principios:

- **SOLID:** separar responsabilidades y favorecer un diseño mantenible.
- **DRY:** evitar duplicaciones innecesarias.
- **TDD:** utilizar pruebas para definir y verificar el comportamiento cuando corresponda.
- **Seguridad por diseño:** proteger los recursos desde el backend, validar las entradas y aplicar los permisos adecuados.
- **Gestión de secretos:** mantener las credenciales fuera del repositorio.
- **Migraciones versionadas:** controlar los cambios de la base de datos mediante Flyway.
- **Commits descriptivos:** facilitar el seguimiento de los cambios.
- **Documentación sincronizada:** mantener la información alineada con el código y las pruebas reales.

---

## Documentación

El README proporciona una visión general del backend y una guía inicial de configuración.

La Wiki de GitHub recogerá la documentación técnica detallada:

- Arquitectura y estructura de carpetas.
- Instalación y configuración de los entornos.
- Modelo de datos y migraciones.
- Autenticación, autorización y seguridad.
- Catálogo de endpoints con ejemplos.
- Pruebas realizadas con Postman, DBeaver y Maven.
- Integración de los servicios con Angular.
- Problemas encontrados y soluciones aplicadas.
- Funcionalidades pendientes y decisiones de diseño.

La documentación se actualizará conforme avance la implementación, distinguiendo siempre entre las funcionalidades disponibles, las que están en desarrollo y las previstas para futuras versiones.