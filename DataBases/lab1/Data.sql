CREATE TABLE "researcher" (
  "id" SERIAL PRIMARY KEY,
  "name" varchar,
  "surname" varchar,
  "country" varchar,
  "birth_year" integer,
  "description" varchar
);

CREATE TABLE "discovery" (
  "id" SERIAL PRIMARY KEY,
  "title" varchar,
  "d_year" integer,
  "temperament" varchar,
  "sphere" varchar,
  "description" text
);

CREATE TABLE "scientific_object" (
  "id" SERIAL PRIMARY KEY,
  "title" varchar,
  "description" text
);

CREATE TABLE "research" (
  "id" SERIAL PRIMARY KEY,
  "id_researcher" integer NOT NULL,
  "id_discovery" integer,
  "id_sc_object" integer NOT NULL
);

CREATE TABLE "expectation" (
  "id" SERIAL PRIMARY KEY,
  "object" varchar,
  "discovery_id" integer NOT NULL,
  "title" varchar,
  "purpose_id" integer,
  "certainty" integer NOT NULL DEFAULT 0 CHECK ("certainty" >= 0 AND "certainty" <= 100)
);

CREATE TABLE "purpose" (
  "id" SERIAL PRIMARY KEY,
  "description" text
);

COMMENT ON COLUMN "expectation"."certainty" IS 'in percents';

ALTER TABLE "research" ADD FOREIGN KEY ("id_researcher") REFERENCES "researcher" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "research" ADD FOREIGN KEY ("id_discovery") REFERENCES "discovery" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "research" ADD FOREIGN KEY ("id_sc_object") REFERENCES "scientific_object" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "expectation" ADD FOREIGN KEY ("discovery_id") REFERENCES "discovery" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "expectation" ADD FOREIGN KEY ("purpose_id") REFERENCES "purpose" ("id") DEFERRABLE INITIALLY IMMEDIATE;

-- Defer constraint checking for INSERT
BEGIN;
SET CONSTRAINTS ALL DEFERRED;

INSERT INTO "researcher" ("id", "name", "surname", "country", "birth_year", "description")
VALUES
  (1, 'Джеймс', 'Уотсон', 'Англия', NULL, 'молодой'),
  (2, 'Френсис', 'Крик', 'Англия', NULL, 'молодой');
INSERT INTO "discovery" ("id", "title", "d_year", "temperament", "sphere", "description")
VALUES
  (1, 'Расшифровка структуры ДНК', 1953, 'триумф', NULL, 'увенчало попытки постичь вселенную');
INSERT INTO "scientific_object" ("id", "title", "description")
VALUES
  (1, 'ДНК', NULL);
INSERT INTO "research" ("id", "id_researcher", "id_discovery", "id_sc_object")
VALUES
  (1, 1, 1, 1),
  (2, 2, 1, 1);
INSERT INTO "purpose" ("id", "description")
VALUES
  (1, 'служить во благо');    
INSERT INTO "expectation" ("id", "object", "discovery_id", "title", "purpose_id", "certainty")
VALUES
  (1, 'все', 1, 'отдадут людям', 1, 90);

SET CONSTRAINTS ALL IMMEDIATE;
COMMIT;