# Sistema de Movilidad - Spring Boot

## Descripción

Sistema web desarrollado en Java con Spring Boot
para gestionar carros, motores, choferes,
pasajeros y viajes.

El proyecto permite administrar la información
desde una interfaz web, utilizando una base de
datos H2 para almacenar los registros.

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.5.7
- Spring MVC
- Spring Data JPA
- Thymeleaf
- HTML y CSS
- Base de datos H2
- Apache Maven
- Apache POI para Excel

## Módulos del sistema

### 1. Gestión de carros

- Registrar carros.
- Consultar vehículos.
- Buscar por placa.
- Editar información.
- Eliminar registros.
- Modificar disponibilidad.

### 2. Gestión de motores

- Registrar motores.
- Buscar por número de serie.
- Editar información.
- Eliminar registros.
- Consultar disponibilidad.

### 3. Gestión de choferes

- Registrar conductores.
- Buscar por documento.
- Actualizar información.
- Eliminar registros.
- Controlar disponibilidad.

### 4. Gestión de pasajeros

- Registrar pasajeros.
- Consultar por documento.
- Editar información personal.
- Eliminar registros.

### 5. Gestión de viajes

- Registrar recorridos.
- Seleccionar carro, motor, chofer y pasajero.
- Establecer origen y destino.
- Asignar fecha y hora.
- Editar y eliminar viajes.
- Filtrar viajes por estado.

Estados de los viajes:

- PROGRAMADO
- EN_CURSO
- FINALIZADO
- CANCELADO

## Panel principal

El sistema dispone de un panel de administración
con acceso a los cinco módulos.

Incluye estadísticas sobre:

- Total de carros.
- Total de motores.
- Total de choferes.
- Total de pasajeros.
- Total de viajes.
- Viajes programados.
- Viajes en curso.
- Viajes finalizados.

## Reportes en Excel

El sistema permite exportar un archivo Excel
con cinco hojas:

1. Carros
2. Motores
3. Choferes
4. Pasajeros
5. Viajes

Ruta de descarga:

http://localhost:8081/reportes/excel

## Base de datos

Se utiliza H2 en modo archivo para conservar
los registros después de reiniciar la aplicación.

La base de datos local no se incluye en GitHub.

## Cómo ejecutar el proyecto

Requisitos:

- Java 17.
- Apache Maven.
- Un navegador web.

Clonar el repositorio:

git clone https://github.com/eizi28/SistemaMovilidadSpringBoot.git

Ingresar a la carpeta:

cd SistemaMovilidadSpringBoot

Ejecutar la aplicación:

mvn spring-boot:run

Abrir en el navegador:

http://localhost:8081/

## Rutas principales

- /                 Panel principal
- /carros           Gestión de carros
- /motores          Gestión de motores
- /choferes         Gestión de choferes
- /pasajeros        Gestión de pasajeros
- /viajes           Gestión de viajes
- /reportes/excel   Exportar reporte Excel

## Arquitectura

El proyecto está organizado mediante una
arquitectura MVC.

- Model: entidades del sistema.
- Repository: acceso a datos.
- Service: lógica de negocio.
- Controller: gestión de solicitudes web.
- Templates: páginas HTML con Thymeleaf.

## Consideraciones

Este proyecto fue desarrollado con fines
académicos.

Para realizar pruebas se recomienda utilizar
datos ficticios. La aplicación está configurada
para ejecutarse localmente.

## Repositorio

https://github.com/eizi28/SistemaMovilidadSpringBoot
