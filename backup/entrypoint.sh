#!/bin/bash

# Configuración de cron
echo "43 19 * * * /usr/bin/mysqldump -h baboo-mysql -u babooProject -ptubocaloca baboo > /backup/db_backup_$(date +\%Y-\%m-\%d).sql" > /etc/cron.d/backup-cron

# Da permisos al archivo de cron y lo inicia
chmod 0644 /etc/cron.d/backup-cron
crontab /etc/cron.d/backup-cron

# Inicia el servicio de cron en el contenedor
cron -f
