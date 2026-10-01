# Лабораторная работа 1

## 1. Минимальное множество функциональных зависимостей

Исходная схема из `Data.sql` уже разбита на отдельные отношения. Для каждого отношения минимальное множество функциональных зависимостей совпадает с зависимостью от первичного ключа:

- `researcher`: `id -> name, surname, country, birth_year, description`
- `discovery`: `id -> title, d_year, temperament, sphere, description`
- `scientific_object`: `id -> title, description`
- `research`: `id -> id_researcher, id_discovery, id_sc_object`
- `expectation`: `id -> object, discovery_id, title, purpose_id, certainty`
- `purpose`: `id -> description`

Это означает, что внутри каждой таблицы не видно частичных или транзитивных зависимостей, нарушающих нормальные формы.

## 2. Приведение отношений к 3NF

### 2.1. Что считается 3NF

Отношение находится в третьей нормальной форме, если:

1. оно уже находится во 2NF;
2. каждый неключевой атрибут зависит только от ключа;
3. отсутствуют транзитивные зависимости вида `ключ -> неключевой атрибут -> другой неключевой атрибут`.

В нашей предметной области исходная структура почти сразу попадает в 3NF, но есть несколько важных уточнений, которые нужно оформить явно.

### 2.2. Анализ исходной схемы

#### `researcher`

Сущность хранит данные об исследователе. Все поля описывают именно исследователя и зависят от `id`.

Функциональная зависимость:

- `id -> name, surname, country, birth_year, description`

#### `discovery`

Сущность хранит сведения об открытии. Все атрибуты зависят от `id`.

Функциональная зависимость:

- `id -> title, d_year, temperament, sphere, description`

#### `scientific_object`

Сущность описывает научный объект.

Функциональная зависимость:

- `id -> title, description`

#### `purpose`

Справочник целей.

Функциональная зависимость:

- `id -> description`

#### `expectation`

Сущность описывает ожидание, связанное с открытием и целью.

Функциональная зависимость:

- `id -> object, discovery_id, title, purpose_id, certainty`

#### `research`

Сущность связывает исследователя, открытие и научный объект.

В исходном `Data.sql` есть суррогатный `id`, поэтому формально зависимость выглядит так:

- `id -> id_researcher, id_discovery, id_sc_object`

Но семантически это таблица связи, и для неё более корректно использовать составной ключ:

- `(id_researcher, id_discovery, id_sc_object)` как первичный ключ.

Это устраняет риск дублирования одной и той же связи.

### 2.3. Какие изменения нужны для 3NF

Чтобы сделать схему более строгой и удобной для отчёта, я предлагаю следующие изменения:

1. Для таблицы `research` заменить суррогатный `id` на составной первичный ключ:
   - `(id_researcher, id_discovery, id_sc_object)`

2. Для обязательных связей сделать внешние ключи `NOT NULL`:
   - `research.id_researcher`
   - `research.id_discovery`
   - `research.id_sc_object`
   - `expectation.discovery_id`

3. Для ссылочного поля `expectation.purpose_id` разрешить `NULL`, если цель может отсутствовать.

4. Для полей-идентификаторов добавить явные бизнес-ограничения там, где это оправдано:
   - например, уникальность комбинации `discovery(title, d_year)` можно добавить при необходимости.

### 2.4. Итоговая схема в 3NF

Ниже схема после приведения к 3NF.

#### `researcher`

- `id` — PK
- `name`
- `surname`
- `country`
- `birth_year`
- `description`

#### `discovery`

- `id` — PK
- `title`
- `d_year`
- `temperament`
- `sphere`
- `description`

#### `scientific_object`

- `id` — PK
- `title`
- `description`

#### `research`

- `id_researcher` — FK -> `researcher(id)`
- `id_discovery` — FK -> `discovery(id)`
- `id_sc_object` — FK -> `scientific_object(id)`
- PK: `(id_researcher, id_discovery, id_sc_object)`

#### `purpose`

- `id` — PK
- `description`

#### `expectation`

- `id` — PK
- `object`
- `discovery_id` — FK -> `discovery(id)`
- `title`
- `purpose_id` — FK -> `purpose(id)`
- `certainty` с ограничением `0..100`

### 2.5. Почему это 3NF

Проверим каждое отношение по определению.

#### `researcher`, `discovery`, `scientific_object`, `purpose`, `expectation`

У всех этих отношений все неключевые атрибуты зависят только от ключа `id`.

Нет атрибутов, которые бы определяли другие неключевые атрибуты внутри самой таблицы.

Следовательно, эти отношения находятся в 3NF.

#### `research`

После замены на составной ключ зависимость становится тривиальной для таблицы связи: вся строка определяется набором внешних ключей.

Это также соответствует 3NF, потому что:

- нет неключевых атрибутов, зависящих от части составного ключа;
- нет транзитивных зависимостей внутри отношения;
- таблица описывает только сам факт связи.

### 2.6. Итог

После минимальных структурных уточнений схема соответствует 3NF и лучше отражает предметную область:

- данные разделены по сущностям;
- связь many-to-many оформлена отдельной таблицей;
- связи между таблицами явно описаны внешними ключами;
- правила целостности заданы на уровне схемы.

## 3. Схема, используемая в отчёте

```sql
researcher(id, name, surname, country, birth_year, description)
discovery(id, title, d_year, temperament, sphere, description)
scientific_object(id, title, description)
research(id_researcher, id_discovery, id_sc_object)
purpose(id, description)
expectation(id, object, discovery_id, title, purpose_id, certainty)
```

## 4. Пояснение к изменениям относительно исходной схемы

- Таблица `research` используется как таблица связи и лучше смотрится с составным первичным ключом.
- `certainty` ограничивается диапазоном 0..100.
- Внешние ключи задают логические зависимости между сущностями.
- Структура не содержит транзитивных зависимостей, нарушающих 3NF.

## 5. Изменения в функциональных зависимостях после приведения в 3NF

Ниже подробно описаны изменения ФЗ при переходе от исходной схемы (`Data.sql`) к предложенной 3NF-схеме.

1) Что было (исходное минимальное множество ФЗ)

- `researcher`: `id -> name, surname, country, birth_year, description`
- `discovery`: `id -> title, d_year, temperament, sphere, description`
- `scientific_object`: `id -> title, description`
- `research`: `id -> id_researcher, id_discovery, id_sc_object`
- `expectation`: `id -> object, discovery_id, title, purpose_id, certainty`
- `purpose`: `id -> description`

2) Что изменилось

- `research`:
   - Исходно имелся суррогатный `id`, который функционально определял `id_researcher, id_discovery, id_sc_object`.
   - После преобразования мы убираем суррогатный `id` и используем составной ключ `(id_researcher,id_discovery,id_sc_object)` как PK.
   - Это меняет представление ФЗ: раньше была зависимость `id -> id_researcher,id_discovery,id_sc_object`; теперь ключевое определение — составной ключ, т.е.
      `(id_researcher,id_discovery,id_sc_object) -> (сам набор атрибутов)` (формально это тривиальная зависимость, потому что все атрибуты отношения являются атрибутами ключа или зависят от него).
   - Практически это устраняет возможность дублирования одной и той же связи (одинаковых троек) и делает модель семантически корректнее.

- Остальные отношения (`researcher`, `discovery`, `scientific_object`, `purpose`, `expectation`) сохраняют прежние ФЗ вида `id -> остальные атрибуты` — они не изменились при декомпозиции.

3) Межтабличные (транзитивные) зависимости

- В исходной нормализованной схеме остаются естественные транзитивные связи через FK: например, `expectation.discovery_id -> discovery.title` (через связь `discovery_id` в `expectation` и PK таблицы `discovery`).
- Такие зависимости не являются нарушением 3NF, поскольку `discovery.title` не является атрибутом отношения `expectation`. Они лишь указывают, какие значения можно получить по JOIN'ам. Если вы денормализуете и скопируете `discovery.title` в `expectation`, появится FD внутри `expectation` — `discovery_id -> title` — и это потребует дополнительной проверки нормализации.

4) Минимальное множество ФЗ после преобразования в 3NF

- `researcher.id -> name, surname, country, birth_year, description`
- `discovery.id -> title, d_year, temperament, sphere, description`
- `scientific_object.id -> title, description`
- `(id_researcher, id_discovery, id_sc_object)` — составной PK для `research` (определяет наличие записи связи)
- `purpose.id -> description`
- `expectation.id -> object, discovery_id, title, purpose_id, certainty`

5) Сохранение зависимостей и свойства декомпозиции

- Разложение исходной схемы (в частности замена суррогатного `id` в `research` на составной PK) является информационно и семантически корректным — никаких данных не теряется: для каждой исходной строки существовала уникальная тройка `(id_researcher,id_discovery,id_sc_object)`, и мы сохраняем её как ключ.
- Декомпозиция является безпотерянной (lossless), потому что строки сопоставимы по ключам (одна запись в новой таблице соответствует одной записи в исходной связке) — формально пересечение схемы нового отношения с исходной функцией определяет ключ.
- Декомпозиция сохраняет функциональные зависимости: все исходные FD, заданные в пределах отдельного отношения, остаются в соответствующих новых отношениях; межтабличные FD (через FK) по-прежнему получаются при JOIN'ах.

6) Что добавилось/может добавиться при уточнении бизнес-правил

- Если добавить `UNIQUE(title,d_year)` в `discovery`, появится FD `(title,d_year) -> id` и это нужно будет учесть при проверке нормальных форм (возможны дополнительные натуральные ключи).
- Если денормализовать (скопировать) поля из `discovery` в `expectation`, появятся новые внутренних FD и потребуется новая нормализация/анализ.

## 6. Схема «на основе NF" (представление на основе функциональных зависимостей)

Ниже приведена схема в форме, удобной для доказательств нормальных форм: для каждого отношение указаны ключи и соответствующие ФЗ.

- `researcher(id)`; FD: `id -> name, surname, country, birth_year, description`
- `discovery(id)`; FD: `id -> title, d_year, temperament, sphere, description`
- `scientific_object(id)`; FD: `id -> title, description`
- `research(id_researcher, id_discovery, id_sc_object)`; PK: `(id_researcher,id_discovery,id_sc_object)`
- `purpose(id)`; FD: `id -> description`
- `expectation(id)`; FD: `id -> object, discovery_id, title, purpose_id, certainty`

Эта форма удобна для дальнейшего формального доказательства 3NF/BCNF: каждый детерминант в ФЗ либо является ключом отношения, либо поле вынесено в отдельную таблицу, устраняя возможные нарушения.


Если нужно, я сейчас формализую доказательство lossless и dependency-preserving для каждой декомпозиции формально (с шагами проверки пересечений атрибутов и вычислением замыканий). Хочешь, чтобы я сделал это для `research` и `expectation`? 

## 7. Преобразование в BCNF и доказательство

Задача: проверить, требуется ли дополнительная декомпозиция для приведения схемы к BCNF, или текущая схема уже удовлетворяет BCNF.

Подход: для каждого отношения выпишем нетривиальные функциональные зависимости (ФЗ) и проверим, является ли левосторонний детерминант суперключом отношения. Если для всех ФЗ это так, отношение в BCNF.

Проверка по отношениям:

- `researcher(id, name, surname, country, birth_year, description)`
   - ФЗ: `id -> name,surname,country,birth_year,description`
   - `id` — PK (суперключ) ⇒ выполняется условие BCNF.

- `discovery(id, title, d_year, temperament, sphere, description)`
   - ФЗ: `id -> title,d_year,temperament,sphere,description`
   - `id` — PK ⇒ BCNF (замечание: если добавить новую ФЗ, напр. `title,d_year -> id`, это не нарушает BCNF — детерминант станет ключом; если же появится ФЗ с детерминантом, не являющимся ключом, потребуются декомпозиции).

- `scientific_object(id, title, description)`
   - ФЗ: `id -> title,description`
   - `id` — PK ⇒ BCNF.

- `purpose(id, description)`
   - ФЗ: `id -> description`
   - `id` — PK ⇒ BCNF.

- `expectation(id, object, discovery_id, title, purpose_id, certainty)`
   - ФЗ (явные): `id -> object,discovery_id,title,purpose_id,certainty`
   - `id` — PK ⇒ BCNF.
   - Примечание: существует межтабличная зависимость `discovery_id -> discovery.title`, но пока `discovery.title` не хранится в `expectation`, это не внутренняя ФЗ для `expectation`.

- `research(id_researcher, id_discovery, id_sc_object)`
   - PK: `(id_researcher,id_discovery,id_sc_object)` — составной ключ; внутри отношения нет нетривиальных ФЗ с детерминантом, не являющимся суперключом.
   - Следовательно, `research` в BCNF.

Вывод: при текущих, явно заданных в DDL, функциональных зависимостях все отношения удовлетворяют условию BCNF — никаких дополнительных декомпозиций не требуется.

Ограничение доказательства: заключение опирается на явные ФЗ, вытекающие из PK и текущих ограничений. Если в предметной области существуют дополнительные ФЗ (натуральные зависимости), не отражённые в DDL (например, `sphere -> temperament` или иные зависимости между неключевыми атрибутами), их нужно явно указать — некоторые из них могут нарушить BCNF и потребовать разложения.

Дальше могу:
- формально провести вычисление замыканий и chase-процедуру для выбранной декомпозиции (если хотите убедиться в lossless/dependency-preserving), либо
- показать, как выполнить BCNF-декомпозицию при наличии конкретной дополнительной ФЗ.

---

## 8. Денормализация (пункт 5)

Денормализация для данной схемы (варианты: материализованное представление, копирование полей с триггерами, предагрегированные таблицы, широкая отчётная таблица/snapshot, JSONB snapshot), а также плюсы/минусы, контроль рассинхронизации и repair-запросы вынесены в отдельный файл:

- [Denormalization.md](Denormalization.md)

---

## 9. Функция и триггер PL/pgSQL (пункт 6)

Выбран предметно-ориентированный вариант: автоматическое ведение статистики по открытиям (`discovery_stats`) — сколько исследований (`research`) и ожиданий (`expectation`) связано с каждым `discovery`. Это полезно для отчётов и демонстрирует денормализацию с поддержкой консистентности на триггерах.

Реализация находится в файле:

- [trigger.sql](trigger.sql)

Результат:

- Таблица `discovery_stats(discovery_id, research_count, expectation_count, last_updated)` поддерживается автоматически.
- При INSERT/UPDATE/DELETE в `research` или `expectation` пересчитываются счётчики для соответствующего `discovery`.


