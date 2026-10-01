# Денормализация для схемы ЛР1

Ниже — набор конкретных, полезных вариантов денормализации для вашей схемы (`researcher`, `discovery`, `scientific_object`, `research`, `expectation`, `purpose`).

Цель денормализации: ускорить **частые чтения** (JOIN/агрегации/отчёты) ценой:

- дополнительного места;
- усложнения обновлений (триггеры/ETL);
- риска рассинхронизации (нужны проверки/repair).

Важно: денормализация не «улучшает» модель данных в смысле нормальных форм — она делает чтение быстрее под конкретные запросы.

---

## 0) Какие запросы в вашей предметной области обычно «тяжёлые»

Типовые сценарии, где начинаются JOIN и GROUP BY:

1. Показать ожидания вместе с названием открытия:
   - `expectation` + `discovery` (+ иногда `purpose`).
2. Показать статистику по открытиям:
   - сколько исследований (`research`) связано с каждым `discovery`.
3. Отчёт в одну таблицу: ожидание + открытие + цель + счётчики.

Далее денормализации нацелены именно на это.

---

## 1) Материализованное представление (MV) для быстрых отчётов

### Что денормализуем

Предварительно «склеиваем» данные из `expectation` и `discovery` (и при желании `purpose`).

### Когда применять

- отчёты/дашборды;
- допускается задержка обновления (периодический refresh).

### Плюсы / минусы

Плюсы:

- быстрый SELECT без JOIN;
- не нужно поддерживать консистентность триггерами на каждой записи.

Минусы:

- данные могут быть чуть устаревшими;
- `REFRESH MATERIALIZED VIEW` может быть тяжёлым на больших объёмах.

### SQL-шаблон

```sql
CREATE MATERIALIZED VIEW mv_expectation_discovery AS
SELECT e.id AS expectation_id,
       e.title AS expectation_title,
       e.object,
       e.certainty,
       e.purpose_id,
       d.id AS discovery_id,
       d.title AS discovery_title,
       d.d_year
FROM expectation e
JOIN discovery d ON e.discovery_id = d.id;

-- Для REFRESH CONCURRENTLY нужен уникальный индекс
CREATE UNIQUE INDEX mv_expectation_pk ON mv_expectation_discovery(expectation_id);
CREATE INDEX mv_by_discovery ON mv_expectation_discovery(discovery_id);

-- Обновление (например, по расписанию)
-- REFRESH MATERIALIZED VIEW CONCURRENTLY mv_expectation_discovery;
```

### Проверка / контроль

```sql
-- Кол-во ожиданий, которых нет в MV (после refresh должно быть 0)
SELECT COUNT(*)
FROM expectation e
LEFT JOIN mv_expectation_discovery mv ON mv.expectation_id = e.id
WHERE mv.expectation_id IS NULL;
```

---

## 2) Копирование часто читаемых полей в `expectation` (realtime, без JOIN)

### Что денормализуем

Копируем `discovery.title` в `expectation.discovery_title`.

### Когда применять

- UI/API часто делает запрос «список ожиданий с названием открытия»;
- данные нужны строго актуальные (realtime);
- JOIN стал заметным узким местом.

### Плюсы / минусы

Плюсы:

- чтение из одной таблицы (`expectation`);
- минимальные затраты на чтение.

Минусы:

- усложнение обновлений;
- нагрузка на запись;
- нужно отслеживать рассинхрон.

### SQL-шаблон (триггеры)

```sql
ALTER TABLE expectation ADD COLUMN discovery_title varchar;

CREATE OR REPLACE FUNCTION set_expectation_discovery_title() RETURNS trigger AS $$
BEGIN
  NEW.discovery_title := (SELECT title FROM discovery WHERE id = NEW.discovery_id);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER expectation_set_title
BEFORE INSERT OR UPDATE ON expectation
FOR EACH ROW EXECUTE FUNCTION set_expectation_discovery_title();

CREATE OR REPLACE FUNCTION propagate_discovery_title() RETURNS trigger AS $$
BEGIN
  UPDATE expectation
  SET discovery_title = NEW.title
  WHERE discovery_id = NEW.id;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER discovery_propagate_title
AFTER UPDATE OF title ON discovery
FOR EACH ROW EXECUTE FUNCTION propagate_discovery_title();
```

### Проверка рассинхрона

```sql
SELECT COUNT(*)
FROM expectation e
JOIN discovery d ON e.discovery_id = d.id
WHERE e.discovery_title IS DISTINCT FROM d.title;
```

### Repair-job (периодическая синхронизация)

```sql
UPDATE expectation e
SET discovery_title = d.title
FROM discovery d
WHERE e.discovery_id = d.id
  AND e.discovery_title IS DISTINCT FROM d.title;
```

---

## 3) Предагрегированная статистика (счётчики) по открытиям

### Что денормализуем

Храним число связанных исследований для каждого открытия:

- `discovery_stats(discovery_id, research_count)`

### Когда применять

- часто спрашивают «сколько исследований у открытия»;
- нужно быстро строить топ-списки.

### Плюсы / минусы

Плюсы:

- быстрые отчёты без `COUNT(*) GROUP BY` на лету.

Минусы:

- триггеры или batch-пересчёт;
- при bulk-операциях лучше отключать триггеры и делать пересчёт.

### SQL-шаблон (инициализация)

```sql
CREATE TABLE discovery_stats (
  discovery_id int PRIMARY KEY REFERENCES discovery(id),
  research_count int NOT NULL DEFAULT 0
);

INSERT INTO discovery_stats(discovery_id, research_count)
SELECT d.id, COALESCE(count(r.*), 0)
FROM discovery d
LEFT JOIN research r ON r.id_discovery = d.id
GROUP BY d.id;
```

### Поддержка триггерами (упрощённый вариант)

```sql
CREATE OR REPLACE FUNCTION trg_research_stats() RETURNS trigger AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    INSERT INTO discovery_stats(discovery_id, research_count)
      VALUES (NEW.id_discovery, 1)
    ON CONFLICT (discovery_id)
      DO UPDATE SET research_count = discovery_stats.research_count + 1;
    RETURN NEW;
  ELSIF TG_OP = 'DELETE' THEN
    UPDATE discovery_stats
    SET research_count = GREATEST(research_count - 1, 0)
    WHERE discovery_id = OLD.id_discovery;
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER research_after_ins
AFTER INSERT ON research
FOR EACH ROW EXECUTE FUNCTION trg_research_stats();

CREATE TRIGGER research_after_del
AFTER DELETE ON research
FOR EACH ROW EXECUTE FUNCTION trg_research_stats();
```

### Проверка корректности счётчиков

```sql
SELECT ds.discovery_id
FROM discovery_stats ds
LEFT JOIN (
  SELECT id_discovery, COUNT(*) AS cnt
  FROM research
  GROUP BY id_discovery
) r ON r.id_discovery = ds.discovery_id
WHERE ds.research_count IS DISTINCT FROM COALESCE(r.cnt, 0);
```

---

## 4) «Широкая» отчётная таблица (ETL / snapshot)

### Что денормализуем

Создаём таблицу, где в одной строке уже лежат:

- ожидание (`expectation`)
- открытие (`discovery`)
- цель (`purpose`)
- агрегаты по исследованиям (`research`)

### Когда применять

- тяжёлые отчёты с большим числом JOIN;
- чтений много, обновления можно делать пакетно.

### Плюсы / минусы

Плюсы:

- максимальная скорость чтения;
- можно хранить KPI/метрики.

Минусы:

- нужно обновление по расписанию;
- задержка актуальности.

### SQL-шаблон (создание snapshot)

```sql
CREATE TABLE report_expectations AS
SELECT e.id AS expectation_id,
       e.title AS expectation_title,
       e.object,
       e.certainty,
       p.description AS purpose_description,
       d.id AS discovery_id,
       d.title AS discovery_title,
       d.d_year,
       COALESCE(r_counts.research_count, 0) AS research_count
FROM expectation e
LEFT JOIN purpose p ON e.purpose_id = p.id
JOIN discovery d ON e.discovery_id = d.id
LEFT JOIN (
  SELECT id_discovery, COUNT(*) AS research_count
  FROM research
  GROUP BY id_discovery
) r_counts ON r_counts.id_discovery = d.id;
```

### Обновление (пример)

```sql
TRUNCATE report_expectations;
INSERT INTO report_expectations
SELECT ... (тот же SELECT, что и при создании) ...;
```

---

## 5) JSONB snapshot (исторический снимок связанной сущности)

### Что денормализуем

Храним снимок записи `discovery` внутри `expectation` на момент вставки/обновления.

### Когда применять

- важно хранить «как выглядело открытие тогда»;
- нужен удобный audit/история.

### Плюсы / минусы

Плюсы:

- удобно для истории;
- можно хранить много полей без изменения схемы.

Минусы:

- сложнее индексировать;
- больше места.

### SQL-шаблон

```sql
ALTER TABLE expectation ADD COLUMN discovery_snapshot jsonb;

CREATE OR REPLACE FUNCTION set_expectation_snapshot() RETURNS trigger AS $$
BEGIN
  SELECT to_jsonb(d) INTO NEW.discovery_snapshot
  FROM discovery d
  WHERE d.id = NEW.discovery_id;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER expectation_snapshot
BEFORE INSERT OR UPDATE ON expectation
FOR EACH ROW EXECUTE FUNCTION set_expectation_snapshot();
```

---

## 6) Итоговая рекомендация для ЛР

Для лабораторной работы удобно описать 2–3 варианта:

1. MV `mv_expectation_discovery` — как основной отчётный вариант (простая и «чистая» денормализация).
2. `discovery_stats` — как пример полезной денормализации-агрегата.
3. (Опционально) `expectation.discovery_title` — как realtime денормализация с триггерами (показать, что вы понимаете цену консистентности).

Эти варианты хорошо покрывают разные сценарии и демонстрируют понимание компромиссов.
