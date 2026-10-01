BEGIN;

CREATE TABLE IF NOT EXISTS discovery_stats (
  discovery_id       int PRIMARY KEY REFERENCES discovery(id) ON DELETE CASCADE,
  research_count     int NOT NULL DEFAULT 0,
  expectation_count  int NOT NULL DEFAULT 0,
  last_updated       timestamptz NOT NULL DEFAULT now()
);


CREATE OR REPLACE FUNCTION refresh_discovery_stats(p_discovery_id int)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  r_cnt int;
  e_cnt int;
BEGIN
  IF p_discovery_id IS NULL THEN
    RETURN;
  END IF;

  SELECT COUNT(*)::int
  INTO r_cnt
  FROM research r
  WHERE r.id_discovery = p_discovery_id;

  SELECT COUNT(*)::int
  INTO e_cnt
  FROM expectation e
  WHERE e.discovery_id = p_discovery_id;

  INSERT INTO discovery_stats(discovery_id, research_count, expectation_count, last_updated)
  VALUES (p_discovery_id, r_cnt, e_cnt, now())
  ON CONFLICT (discovery_id) DO UPDATE
  SET research_count = EXCLUDED.research_count,
      expectation_count = EXCLUDED.expectation_count,
      last_updated = now();
END;
$$;

CREATE OR REPLACE FUNCTION trg_discovery_stats_init()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM refresh_discovery_stats(NEW.id);
  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS discovery_stats_init ON discovery;
CREATE TRIGGER discovery_stats_init
AFTER INSERT ON discovery
FOR EACH ROW EXECUTE FUNCTION trg_discovery_stats_init();


CREATE OR REPLACE FUNCTION trg_discovery_stats_on_research()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    PERFORM refresh_discovery_stats(NEW.id_discovery);
    RETURN NEW;
  ELSIF TG_OP = 'DELETE' THEN
    PERFORM refresh_discovery_stats(OLD.id_discovery);
    RETURN OLD;
  ELSIF TG_OP = 'UPDATE' THEN
    IF NEW.id_discovery IS DISTINCT FROM OLD.id_discovery THEN
      PERFORM refresh_discovery_stats(OLD.id_discovery);
      PERFORM refresh_discovery_stats(NEW.id_discovery);
    ELSE
      PERFORM refresh_discovery_stats(NEW.id_discovery);
    END IF;
    RETURN NEW;
  END IF;
  RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS research_stats_change ON research;
CREATE TRIGGER research_stats_change
AFTER INSERT OR UPDATE OR DELETE ON research
FOR EACH ROW EXECUTE FUNCTION trg_discovery_stats_on_research();


CREATE OR REPLACE FUNCTION trg_discovery_stats_on_expectation()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    PERFORM refresh_discovery_stats(NEW.discovery_id);
    RETURN NEW;
  ELSIF TG_OP = 'DELETE' THEN
    PERFORM refresh_discovery_stats(OLD.discovery_id);
    RETURN OLD;
  ELSIF TG_OP = 'UPDATE' THEN
    IF NEW.discovery_id IS DISTINCT FROM OLD.discovery_id THEN
      PERFORM refresh_discovery_stats(OLD.discovery_id);
      PERFORM refresh_discovery_stats(NEW.discovery_id);
    ELSE
      PERFORM refresh_discovery_stats(NEW.discovery_id);
    END IF;
    RETURN NEW;
  END IF;
  RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS expectation_stats_change ON expectation;
CREATE TRIGGER expectation_stats_change
AFTER INSERT OR UPDATE OR DELETE ON expectation
FOR EACH ROW EXECUTE FUNCTION trg_discovery_stats_on_expectation();


INSERT INTO discovery_stats(discovery_id, research_count, expectation_count, last_updated)
SELECT d.id,
       COALESCE(r_cnt.cnt, 0) AS research_count,
       COALESCE(e_cnt.cnt, 0) AS expectation_count,
       now()
FROM discovery d
LEFT JOIN (
  SELECT id_discovery, COUNT(*)::int AS cnt
  FROM research
  WHERE id_discovery IS NOT NULL
  GROUP BY id_discovery
) r_cnt ON r_cnt.id_discovery = d.id
LEFT JOIN (
  SELECT discovery_id, COUNT(*)::int AS cnt
  FROM expectation
  GROUP BY discovery_id
) e_cnt ON e_cnt.discovery_id = d.id
ON CONFLICT (discovery_id) DO UPDATE
SET research_count = EXCLUDED.research_count,
    expectation_count = EXCLUDED.expectation_count,
    last_updated = now();

COMMIT;
