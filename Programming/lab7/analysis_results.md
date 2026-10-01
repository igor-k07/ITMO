# Анализ изменений lab6 → lab7

## Источники требований

В проекте есть **два файла** с требованиями:
- [ReadMe.md](file:///Users/igor/Projects/ITMO/Programming/lab7/ReadMe.md) — основное задание (MD5, ForkJoinPool для чтения/обработки, Thread для отправки, `Collections.synchronizedXXX`)
- [7.txt](file:///Users/igor/Projects/ITMO/Programming/lab7/7.txt) — **альтернативный вариант** (SHA-256, Thread для чтения, Cached thread pool для отправки, `ReadWriteLock`)

> [!WARNING]
> `ReadMe.md` и `7.txt` описывают **разные варианты** задания!  
> Текущая реализация соответствует **ReadMe.md**, а не `7.txt`. Убедись, какой вариант у тебя.

---

## Пункт 1: Хранение коллекции в PostgreSQL, убрать файл

| Требование | Статус | Детали |
|---|---|---|
| Хранение в PostgreSQL | ✅ Сделано | Добавлен [`DatabaseManager.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseManager.java) с CRUD-операциями через JDBC |
| Убрать хранение в файле | ✅ Сделано | В lab6 `ServerMain` требовал `DATA_FILE`, теперь этого нет. В lab7 [`ServerMain`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/ServerMain.java) конструктор `RemoteRuntime()` без аргумента `filePath` |
| Команда `save` убрана | ✅ Сделано | В lab7 [`RemoteRuntime.registerCommands()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/runtime/RemoteRuntime.java#L42-L59) нет `save`. Метод `processConsoleCommands()` в Server.java тоже убрал `save` — оставлен только `exit` |
| SQL-схема | ✅ Сделано | Добавлен [`create_db.sql`](file:///Users/igor/Projects/ITMO/Programming/lab7/create_db.sql) |

---

## Пункт 2: Генерация `id` через `sequence` БД

| Требование | Статус | Детали |
|---|---|---|
| `id` генерируется БД | ✅ Сделано | В `create_db.sql`: `CREATE SEQUENCE music_band_id_seq`, колонка `id INTEGER PRIMARY KEY DEFAULT nextval('music_band_id_seq')`. INSERT использует `RETURNING id` |

В lab6 `CollectionManager.addToCollection()` сам назначал `maxId+1`. В lab7 это удалено — id приходит из БД.

---

## Пункт 3: Обновление памяти только при успешном добавлении в БД

| Требование | Статус | Детали |
|---|---|---|
| Сначала БД, потом память | ✅ Сделано | В [`Add.execute()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/commands/Add.java#L29-L46): сначала `databaseManager.insertMusicBand()`, затем `collectionManager.addToCollection()` |

Этот же паттерн использован в `Update`, `RemoveById`, `Clear`, `RemoveGreater`, `RemoveLower`, `AddIfMax`.

---

## Пункт 4: Команды получения данных работают с коллекцией в памяти

| Требование | Статус | Детали |
|---|---|---|
| Чтение из памяти | ✅ Сделано | `Show`, `Info`, `MaxByNumberOfParticipants`, `MinByBestAlbum`, `FilterByNumberOfParticipants` работают только с `collectionManager`, без `DatabaseManager` |

---

## Пункт 5: Регистрация и авторизация

| Требование | Статус | Детали |
|---|---|---|
| Регистрация | ✅ Сделано | Добавлен [`UserManager.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/UserManager.java) с методом `register()` |
| Авторизация | ✅ Сделано | Метод `authenticate()` в `UserManager` |
| Команда `register` | ✅ Сделано | Новый класс [`Register.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/commands/Register.java) |
| Ввод логина/пароля на клиенте | ✅ Сделано | [`LocalRuntime.readCredentials()`](file:///Users/igor/Projects/ITMO/Programming/lab7/client/src/main/java/com/itmo/runtime/LocalRuntime.java#L68-L73) при старте |

---

## Пункт 6: Хэширование паролей

| Требование (ReadMe.md) | Статус | Детали |
|---|---|---|
| MD5 | ✅ Сделано | [`UserManager.md5()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/UserManager.java#L60-L72) использует `MessageDigest.getInstance("MD5")`. Колонка в БД: `password_md5 CHAR(32)` |

> [!IMPORTANT]
> В `7.txt` указан **SHA-256**, а в `ReadMe.md` — **MD5**. Текущая реализация использует **MD5**. Если твой вариант требует SHA-256, нужно поменять алгоритм.

---

## Пункт 7: Запрет команд для неавторизованных

| Требование | Статус | Детали |
|---|---|---|
| Запрет без авторизации | ✅ Сделано | [`RemoteRuntime.proccessRequest()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/runtime/RemoteRuntime.java#L61-L74): все команды кроме `register` проходят через `authorizeAndExecute()` |
| Логин/пароль в каждом запросе | ✅ Сделано | Все Request-классы расширены полями `login` и `password`. [`RequestBuilder`](file:///Users/igor/Projects/ITMO/Programming/lab7/client/src/main/java/com/itmo/util/request/RequestBuilder.java) передаёт их при создании запросов |

---

## Пункт 8: Информация о владельце объекта

| Требование | Статус | Детали |
|---|---|---|
| `owner_id` сохраняется | ✅ Сделано | Поле `ownerId` добавлено в [`MusicBand`](file:///Users/igor/Projects/ITMO/Programming/lab7/common/src/main/java/com/itmo/models/MusicBand.java#L18). В БД — колонка `owner_id BIGINT NOT NULL REFERENCES users(id)` |

---

## Пункт 9: Просмотр всех, модификация только своих

| Требование | Статус | Детали |
|---|---|---|
| Просмотр всех | ✅ Сделано | `Show` выводит всю коллекцию |
| Модификация только своих | ✅ Сделано | SQL-запросы в `DatabaseManager` содержат `WHERE ... AND owner_id = ?` для `UPDATE`, `DELETE`, `CLEAR` |

---

## Пункт 10: Многопоточная обработка запросов

### Текущая реализация (соответствует ReadMe.md):

| Этап | Требование ReadMe.md | Статус | Реализация |
|---|---|---|---|
| Чтение запросов | `ForkJoinPool` | ✅ | [`Server.java` L24](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/network/Server.java#L24): `readPool = new ForkJoinPool()`, используется в `handleRead()` |
| Обработка запросов | `ForkJoinPool` | ✅ | [`Server.java` L25](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/network/Server.java#L25): `processPool = new ForkJoinPool()`, используется в `readRequest()` |
| Отправка ответов | `new Thread` | ✅ | [`Server.java` L130](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/network/Server.java#L130): `new Thread(() -> ...)` |

### Различия с 7.txt:

| Этап | 7.txt требует | ReadMe.md требует | Что реализовано |
|---|---|---|---|
| Чтение | `new Thread` | `ForkJoinPool` | `ForkJoinPool` ✅ для ReadMe |
| Обработка | `ForkJoinPool` | `ForkJoinPool` | `ForkJoinPool` ✅ |
| Отправка | `Cached thread pool` | `new Thread` | `new Thread` ✅ для ReadMe |

---

## Пункт 11: Синхронизация доступа к коллекции

| Требование ReadMe.md | Статус | Детали |
|---|---|---|
| `Collections.synchronizedXXX` | ✅ Сделано | [`CollectionManager` L24](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/CollectionManager.java#L24): `Collections.synchronizedSet(...)`. Плюс `synchronized(collection)` блоки для итерации |

> [!IMPORTANT]
> `7.txt` требует `ReadWriteLock` вместо `synchronizedXXX`. Если у тебя вариант из `7.txt`, нужно переделать синхронизацию.

Также `sessions` в `Server.java` заменён на `ConcurrentHashMap` (в lab6 был `HashMap`).

---

## Пункт 12: Подключение к БД через переменные окружения

| Требование | Статус | Детали |
|---|---|---|
| Переменные окружения | ✅ Сделано | [`DatabaseConnection.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseConnection.java) читает `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` |

**Изменение относительно lab6**: lab6 использовал одну переменную `DB_URL`, lab7 разделил на отдельные компоненты (`DB_HOST`, `DB_PORT`, `DB_NAME`).

---

## Сводка изменений по файлам

### Новые файлы в lab7
| Файл | Назначение |
|---|---|
| [`DatabaseManager.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseManager.java) | CRUD для `music_bands` через JDBC |
| [`UserManager.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/UserManager.java) | Регистрация / авторизация пользователей |
| [`Register.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/commands/Register.java) | Серверная команда `register` |
| [`create_db.sql`](file:///Users/igor/Projects/ITMO/Programming/lab7/create_db.sql) | SQL-схема таблиц `users` и `music_bands` |
| [`RUNNING.md`](file:///Users/igor/Projects/ITMO/Programming/lab7/RUNNING.md) | Инструкция по запуску |

### Удалённые файлы / функциональность
| Было в lab6 | Удалено |
|---|---|
| Команда `Save.java` | Да, убрана из регистрации |
| `DumpManager` использование | Больше не используется (файл остался, но не подключён) |
| `SetEnviroment.getCollectionPath()` | Убран из `ServerMain` |
| `DatabaseConnectionCheck.java` | Отсутствует в lab7 |

### Изменённые файлы

| Файл | Что изменилось |
|---|---|
| **`ServerMain`** | Убран `DATA_FILE`, конструктор `RemoteRuntime()` без аргументов |
| **`RemoteRuntime`** | Добавлены `DatabaseManager`, `UserManager`, загрузка из БД, авторизация через `authorizeAndExecute()`, убраны `DumpManager` и `saveCollection()` |
| **`Server.java`** | Добавлены `ForkJoinPool readPool/processPool`, `ConcurrentHashMap` для sessions, многопоточные `readRequest()` → `processRequest()` → `sendResponse()`, убрана `save` из консоли |
| **`CollectionManager`** | `Collections.synchronizedSet()`, `synchronized` блоки, убраны `maxId`/`updateMaxId()`, `saveCollection()`, добавлен `snapshot()` |
| **`MusicBand`** | Добавлено поле `ownerId` с геттером/сеттером |
| **Все Request-классы** | Добавлены `login`, `password` поля и конструкторы |
| **`RequestBuilder`** | Принимает и передаёт `login`/`password` |
| **`LocalRuntime` (клиент)** | Добавлены `login`/`password` поля, `readCredentials()`, передача credentials при создании запросов |
| **`Add`, `Update`, `RemoveById`, `Clear`, `RemoveGreater`, `RemoveLower`, `AddIfMax`** | Принимают `DatabaseManager`, сначала пишут в БД, потом в память, проверяют `userId` |

---

## Потенциальные проблемы

> [!WARNING]
> **`DumpManager.java` остался в lab7** — файл существует (4215 байт), но нигде не используется. Можно удалить для чистоты.

> [!WARNING]
> **`HashSetCollectionManager.java` остался в lab7** — файл существует (3850 байт), но не используется. Тоже мусор из lab6.

> [!IMPORTANT]
> **Какой вариант у тебя?** `ReadMe.md` и `7.txt` описывают разные варианты задания:
> - **ReadMe.md**: MD5, ForkJoinPool/ForkJoinPool/Thread, `Collections.synchronizedXXX`
> - **7.txt**: SHA-256, Thread/ForkJoinPool/CachedThreadPool, `ReadWriteLock`
> 
> Текущий код реализует вариант из **ReadMe.md**.
