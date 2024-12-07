# Usar una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR de la aplicación al contenedor
COPY target/babooBackend-0.0.1-SNAPSHOT.jar /app/baboo-java-app.jar

# Exponer el puerto en el que la aplicación escucha
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/baboo-java-app.jar"]
