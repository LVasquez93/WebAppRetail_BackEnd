# Cotizador Backend (Spring Boot + MySQL + PDFBox)

> 📖 **Guía Arquitectónica y Documentación para Agentes IA**:  
> Para una visión técnica profunda de la arquitectura, diagrama de entidades, reglas de negocio y cómo extender con nuevos módulos, consulta [AGENT_GUIDE.md](AGENT_GUIDE.md).

Backend API REST empresarial para el Sistema de Cotizaciones Retail (El Salvador). Desarrollado con Spring Boot 3.3.4, Java 17, Spring Security con autenticación Stateless JWT (RBAC), base de datos MySQL 8 y motor de generación de PDF con Thymeleaf, OpenHTMLtoPDF y Apache PDFBox.

---

## 🚀 Requisitos
- **Java 17** (Eclipse Temurin / OpenJDK 17).
- **Maven 3.9+** (o utilizar `./mvnw` / `mvnw.cmd`).
- **MySQL 8.x** (Local o en la nube como Aiven).

---

## 🛠️ Ejecución en Desarrollo Local (Windows)
Por defecto, la aplicación está preconfigurada para conectarse a `localhost:3306` con la base de datos `cotizador_db` y usuario `root` / `1234`.

```bash
# Opción 1: Ejecutar directamente con Maven
mvn spring-boot:run

# Opción 2: Usar el script batch
iniciar.bat
```
El servidor arrancará en: **http://localhost:8080**

### Ejecución de Pruebas Unitarias
```bash
mvn test
```

---

## 🌐 Variables de Entorno para Producción (Render / Aiven)
En producción, no necesitas modificar el código. Simplemente define estas variables en el panel de Render:

| Variable | Descripción | Valor por Defecto (Local) |
| :--- | :--- | :--- |
| `PORT` | Puerto HTTP donde escucha el servicio (inyectado por Render) | `8080` |
| `SPRING_DATASOURCE_URL` | URL JDBC de MySQL (con SSL obligatorio en Aiven) | `jdbc:mysql://localhost:3306/cotizador_db?...` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos en la nube | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `1234` |
| `CORS_ALLOWED_ORIGINS` | Dominios permitidos separados por coma (ej. URL de Cloudflare) | `http://localhost:5173,http://127.0.0.1:5173` |
| `JWT_SECRET` | Llave secreta en hexadecimal para firmar tokens JWT | `404E635266556...` |
| `JWT_EXPIRATION_MS` | Tiempo de vida del token en milisegundos (24 horas) | `86400000` |
| `HIBERNATE_DDL_AUTO` | Estrategia de esquemas de Hibernate (`update`, `validate`) | `update` |
| `HIKARI_MAX_POOL_SIZE` | Máximo de conexiones en el pool (ajustar según plan de BD) | `5` |

---

## 🐳 Despliegue con Docker (Render)
El proyecto incluye un `Dockerfile` multi-etapa que instala automáticamente `fontconfig` y `ttf-dejavu` para asegurar que el renderizado de PDFs no falle en entornos Linux contenerizados.
Render detectará automáticamente este `Dockerfile` al seleccionar el runtime Docker.
