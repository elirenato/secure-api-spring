FROM postgres:15.6-alpine
COPY create-db.sh /create-db.sh
RUN chmod +x /create-db.sh
COPY init-user-db.sh /docker-entrypoint-initdb.d/init-user-db.sh
