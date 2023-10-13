ALTER TABLE person
  ADD COLUMN trgm_search TEXT NULL;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS person_trgm_search_idx ON person USING GIN (trgm_search gin_trgm_ops);
