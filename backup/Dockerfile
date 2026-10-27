# Usa una imagen base ligera como Debian o Alpine
FROM debian:latest

# Instala las herramientas necesarias (cron, bash, default-mysql-client, etc.)
RUN apt-get update && apt-get install -y \
    cron \
    bash \
    default-mysql-client \
    && apt-get clean

# Copia el script `entrypoint.sh` al contenedor
COPY entrypoint.sh /usr/local/bin/entrypoint.sh

# Da permisos de ejecución al script
RUN chmod +x /usr/local/bin/entrypoint.sh

# Configura el entrypoint
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
