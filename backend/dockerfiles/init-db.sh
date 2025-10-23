#!/bin/sh

# This script is executed during the Docker image build process.
# It waits for the database to be ready and then initializes it.

# Exit immediately if a command exits with a non-zero status.
set -e

# Set the PGPASSWORD so psql doesn't prompt for it.
export PGPASSWORD=$POSTGRES_PASSWORD

# Since this script now runs in its own container, it must wait for the 'postgres' service to be ready.
echo "INFO: Waiting for database on host 'postgres'..."
until psql -h "postgres" -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c '\q'; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command"


# Determine which schema file to use based on the 'SCHEMA_MODE' environment variable.
if [ "$SCHEMA_MODE" = "validate" ]; then
    echo "INFO: SCHEMA_MODE is 'validate'. Using non-destructive schema."
    SCHEMA_FILE="/schemas/prod-schema.sql"
elif [ "$SCHEMA_MODE" = "create-drop" ]; then
    echo "INFO: SCHEMA_MODE is 'create-drop'. Using create-drop schema."
    SCHEMA_FILE="/schemas/dev-schema.sql"
else
    echo "INFO: SCHEMA_MODE is not set or invalid. Defaulting to create-drop schema."
    SCHEMA_FILE="/schemas/dev-schema.sql"
fi

echo "INFO: Initializing database with schema: $SCHEMA_FILE"

# Execute the schema file.
psql -h "postgres" -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -a -f "$SCHEMA_FILE"

echo "INFO: Database initialization complete."
