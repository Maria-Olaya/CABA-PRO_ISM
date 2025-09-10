# CABA-PRO_ISM
---
## Cómo ejecutar el programa

### 1) Requisitos previos
- **Java 17** o superior  
- **Maven**  
- **MySQL** (si usarás BD local)

Si usarás MySQL, crea la base de datos:
```sql
CREATE DATABASE caba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
````

### 2) Configuración adicional

El repositorio incluye un archivo de ejemplo que contiene una plantilla básica de configuración:

```
src/main/resources/application-example.properties
```

Este archivo sirve como **guía de referencia** para configurar tu entorno.  
Antes de ejecutar la aplicación, **cópialo o renómbralo** a:

```
src/main/resources/application.properties
```

y edítalo con tus credenciales reales de **MySQL** y de **correo Gmail**.

#### Configuración MySQL

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/caba?useSSL=false&serverTimezone=UTC
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
```

#### Configuración Email (Gmail)

> Para notificaciones automáticas por correo.

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Importante:** usa una **Contraseña de Aplicación de Gmail** (no tu contraseña personal).

##### Variables de entorno recomendadas

**Windows (PowerShell)**

```powershell
$env:MAIL_USERNAME="tu_correo@gmail.com"
$env:MAIL_PASSWORD="tu_password_app"
mvn spring-boot:run
```

---

### 3) Ejecutar

#### Desde Maven (línea de comandos)

Dentro de la carpeta raíz del proyecto:

```bash
mvn spring-boot:run
```

---

## Ruta principal

La aplicación queda disponible en: **http://localhost:8080/**


## Dependencias principales

* Spring Boot (Web, Security, Data JPA, Thymeleaf, Mail, Validation)
* MySQL Connector
* OpenPDF (PDF)
* H2 Database (solo para pruebas locales)

---
