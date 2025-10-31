# Usamos una imagen de Maven con OpenJDK 17 para compilar el proyecto.
# Nombramos a esta etapa "build" para poder referenciarla más adelante.
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app
# Copiamos primero el archivo de definición del proyecto (pom.xml).
# Esto aprovecha el sistema de caché de Docker. Si el pom.xml no cambia,
# Docker reutilizará la capa de dependencias descargadas, acelerando la compilación.
COPY pom.xml .
# Descargamos todas las dependencias del proyecto.
RUN mvn -q -DskipTests dependency:go-offline
# Ahora copiamos el resto del código fuente.
COPY src ./src
# La opción -DskipTests omite la ejecución de tests, lo cual es común para builds de Docker.
RUN mvn -q -DskipTests clean package
# Usamos una imagen base mucho más ligera que solo contiene el Java Runtime Environment (JRE).
# No necesitamos el JDK completo para ejecutar la aplicación.
FROM openjdk:17-jdk-slim

# Establecemos el directorio de trabajo en la nueva etapa.
WORKDIR /app

# **Paso clave: Copiar el JAR desde la etapa de 'build'**
# Aquí es donde copiamos únicamente el artefacto compilado desde la primera etapa.
# Asegúrate de reemplazar 'mi-aplicacion-1.0.0.jar' con el nombre real de tu JAR.
# Lo renombramos a 'app.jar' para tener un nombre genérico y fácil de usar.
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el que se ejecuta tu aplicación (8080 es el estándar para Spring Boot).
EXPOSE 8080
# Variables de entorno opcionales
ENV JAVA_OPTS=""
# El comando que se ejecutará cuando el contenedor inicie.
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]