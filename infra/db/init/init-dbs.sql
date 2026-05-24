SELECT 'CREATE DATABASE keycloak'
  WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak') \gexec

SELECT 'CREATE DATABASE hsg'
  WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hsg') \gexec

\connect hsg
CREATE SCHEMA IF NOT EXISTS hsg;
