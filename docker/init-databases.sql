-- Create separate databases for each service using standardized naming
CREATE DATABASE "profile-service_db";
CREATE DATABASE "consent-service_db";
CREATE DATABASE "segment-service_db";
CREATE DATABASE "preference-service_db";

-- The 'pcm' user is already created via environment variables in docker-compose.yml
-- We just ensure it has full access to all databases

GRANT ALL PRIVILEGES ON DATABASE "profile-service_db" TO pcm;
GRANT ALL PRIVILEGES ON DATABASE "consent-service_db" TO pcm;
GRANT ALL PRIVILEGES ON DATABASE "segment-service_db" TO pcm;
GRANT ALL PRIVILEGES ON DATABASE "preference-service_db" TO pcm;

-- Schema privileges
\c "profile-service_db";
GRANT ALL ON SCHEMA public TO pcm;

\c "consent-service_db";
GRANT ALL ON SCHEMA public TO pcm;

\c "segment-service_db";
GRANT ALL ON SCHEMA public TO pcm;

\c "preference-service_db";
GRANT ALL ON SCHEMA public TO pcm;
