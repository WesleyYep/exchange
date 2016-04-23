#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER exchange_app WITH PASSWORD 'exchange_app';
    CREATE DATABASE exchange;
    GRANT ALL PRIVILEGES ON DATABASE exchange TO exchange_app;
EOSQL