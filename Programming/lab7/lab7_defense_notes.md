# Конспект для защиты Lab7 — что и как менялось относительно Lab6

---

## 1. Хранение коллекции в PostgreSQL, убрать хранение в файле

### Что было в Lab6
Коллекция хранилась в JSON-файле. Путь к файлу передавался через переменную окружения `DATA_FILE`. За чтение/запись отвечал класс `DumpManager`. При запуске сервер загружал коллекцию из файла, при выполнении команды `save` — записывал обратно.

### Что изменилось в Lab7

**Добавлен** [`DatabaseManager.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseManager.java) — новый класс, который работает с PostgreSQL через JDBC (`PreparedStatement`). Содержит SQL-запросы для всех операций:
- `INSERT INTO music_bands ... RETURNING id` — вставка
- `UPDATE music_bands SET ... WHERE id = ? AND owner_id = ?` — обновление
- `DELETE FROM music_bands WHERE id = ? AND owner_id = ?` — удаление
- `SELECT ... FROM music_bands` — загрузка всей коллекции

**Добавлен** [`DatabaseConnection.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseConnection.java) — утилитный класс для подключения к БД. Читает переменные окружения `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` и формирует JDBC URL.

**Добавлен** [`create_db.sql`](file:///Users/igor/Projects/ITMO/Programming/lab7/create_db.sql) — SQL-скрипт для создания таблиц `users` и `music_bands`.

**Изменён** [`ServerMain.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/ServerMain.java) — убрана зависимость от `DATA_FILE`. Конструктор `RemoteRuntime()` теперь без аргумента `filePath`.

**Изменён** [`RemoteRuntime.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/runtime/RemoteRuntime.java) — вместо `DumpManager.readCollectionFromFile()` используется `DatabaseManager.loadCollection()`. Команда `save` удалена из регистрации. Метод `saveCollection()` убран.

**Убрана** команда `Save.java` — больше не нужна, т.к. данные пишутся в БД при каждой модифицирующей операции.

---

## 2. Генерация поля `id` средствами БД (sequence)

### Что было в Lab6
В `CollectionManager.addToCollection()` id генерировался на стороне Java: `element.setId(maxId + 1)`. Хранилось поле `maxId`, которое пересчитывалось при каждом изменении коллекции.

### Что изменилось в Lab7
В [`create_db.sql`](file:///Users/igor/Projects/ITMO/Programming/lab7/create_db.sql):
```sql
CREATE SEQUENCE music_band_id_seq AS INTEGER;
CREATE TABLE music_bands (
    id INTEGER PRIMARY KEY DEFAULT nextval('music_band_id_seq'),
    ...
);
```
Sequence в PostgreSQL автоматически генерирует уникальные id. INSERT-запрос использует `RETURNING id` чтобы получить назначенный id обратно в Java.

В [`DatabaseManager.insertMusicBand()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseManager.java#L70-L94) — после вставки читает `resultSet.getInt("id")`.

В [`CollectionManager`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/CollectionManager.java) — убраны поля `maxId` и метод `updateMaxId()`. `addToCollection()` больше не назначает id — он уже приходит из БД.

---

## 3. Обновлять коллекцию в памяти только при успешном добавлении в БД

### Что было в Lab6
Команда `Add` сразу добавляла элемент в коллекцию в памяти: `collectionManager.addToCollection(request.getElement())`.

### Что изменилось в Lab7
Во всех модифицирующих командах используется паттерн **«сначала БД, потом память»**. Пример — [`Add.execute()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/commands/Add.java#L29-L46):
```java
// 1. Сначала пишем в БД
int id = databaseManager.insertMusicBand(band, request.getUserId().intValue());
// 2. Только при успехе — обновляем память
band.setId(id);
band.setOwnerId(request.getUserId());
collectionManager.addToCollection(band);
```
Если `insertMusicBand()` бросит исключение (например, БД недоступна), коллекция в памяти не изменится.

Этот же паттерн реализован в: `Update`, `RemoveById`, `Clear`, `RemoveGreater`, `RemoveLower`, `AddIfMax`.

---

## 4. Команды получения данных работают с коллекцией в памяти

### Как реализовано
Команды, которые только **читают** данные, работают исключительно с `CollectionManager` (коллекция в оперативной памяти) и не обращаются к `DatabaseManager`:

| Команда | Что делает |
|---|---|
| `Show` | `collectionManager.getCollection()` |
| `Info` | `collectionManager.getCollectionSize()`, `getLastInitTime()` и т.д. |
| `MaxByNumberOfParticipants` | `collectionManager.getCollection().stream()...` |
| `MinByBestAlbum` | `collectionManager.getCollection().stream()...` |
| `FilterByNumberOfParticipants` | `collectionManager.getCollection().stream()...` |

Эти команды принимают в конструктор только `CollectionManager`, без `DatabaseManager`.

---

## 5. Регистрация и авторизация пользователей

### Что было в Lab6
Понятия пользователей не было. Клиент подключался к серверу и сразу мог выполнять любые команды.

### Что изменилось в Lab7

**Добавлен** [`UserManager.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/UserManager.java) — менеджер пользователей с двумя методами:
- `register(login, password)` — вставка в таблицу `users`, возвращает id
- `authenticate(login, password)` — поиск по логину + хэшу пароля, возвращает `Optional<Long>`

**Добавлена** команда [`Register.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/commands/Register.java) — серверная команда, вызывает `userManager.register()`.

**Таблица в БД:**
```sql
CREATE TABLE users (
    id       BIGSERIAL PRIMARY KEY,
    login    VARCHAR(50) UNIQUE NOT NULL,
    password_md5 CHAR(32) NOT NULL
);
```

**На клиенте** — [`LocalRuntime.readCredentials()`](file:///Users/igor/Projects/ITMO/Programming/lab7/client/src/main/java/com/itmo/runtime/LocalRuntime.java#L69-L93) показывает меню:
```
=== Добро пожаловать ===
1 - Войти
2 - Зарегистрироваться
```
При выборе «Войти» — проверяет credentials на сервере. При ошибке — предлагает зарегистрироваться. При выборе «Зарегистрироваться» — отправляет запрос на регистрацию. Только после успешной аутентификации/регистрации пользователь попадает в интерактивный режим.

---

## 6. Хэширование паролей алгоритмом MD5

### Как реализовано
В [`UserManager.md5()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/UserManager.java#L60-L72):
```java
public static String md5(String value) {
    MessageDigest digest = MessageDigest.getInstance("MD5");
    byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
    StringBuilder result = new StringBuilder(hash.length * 2);
    for (byte item : hash) {
        result.append(String.format("%02x", item & 0xff));
    }
    return result.toString();
}
```
Пароль хэшируется при регистрации и при каждой аутентификации. В БД хранится только хэш (`password_md5 CHAR(32)`), сам пароль никогда не сохраняется.

---

## 7. Запрет выполнения команд неавторизованным пользователям

### Как реализовано
В [`RemoteRuntime.proccessRequest()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/runtime/RemoteRuntime.java#L61-L74) все команды (кроме `register`) проходят через метод `authorizeAndExecute()`:
```java
if ("register".equals(standartRequest.getName())) {
    return executeCommand(standartRequest);   // без проверки
}
return authorizeAndExecute(standartRequest);  // с проверкой
```

В [`authorizeAndExecute()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/runtime/RemoteRuntime.java#L76-L89):
```java
var userId = userManager.authenticate(request.getLogin(), request.getPassword());
if (userId.isEmpty()) {
    return new Response<>(List.of("Неверный логин или пароль"), Status.ERROR);
}
request.setUserId(userId.get());
return executeCommand(request);
```
Если аутентификация не проходит — команда не выполняется, возвращается ошибка.

---

## 8. При хранении объектов сохранять информацию о пользователе-владельце

### Что было в Lab6
В модели `MusicBand` не было информации о владельце.

### Что изменилось в Lab7
В [`MusicBand`](file:///Users/igor/Projects/ITMO/Programming/lab7/common/src/main/java/com/itmo/models/MusicBand.java#L18) добавлено поле:
```java
private Long ownerId;
```
С геттером и сеттером.

В БД — колонка с внешним ключом:
```sql
owner_id BIGINT NOT NULL REFERENCES users(id)
```

При вставке в [`DatabaseManager.insertMusicBand()`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseManager.java#L85) `ownerId` передаётся в SQL-запрос. При загрузке из БД — читается и устанавливается в объект.

---

## 9. Просмотр всех объектов, модификация только своих

### Как реализовано

**Просмотр** — команда `Show` выводит всю коллекцию целиком, без фильтрации по владельцу. Все пользователи видят все записи.

**Модификация** — SQL-запросы в [`DatabaseManager`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/DatabaseManager.java) содержат условие `AND owner_id = ?`:

```sql
UPDATE music_bands SET ... WHERE id = ? AND owner_id = ?
DELETE FROM music_bands WHERE id = ? AND owner_id = ?
DELETE FROM music_bands WHERE owner_id = ?  -- для clear
```

Если пользователь пытается изменить/удалить чужой объект, SQL-запрос просто не затронет ни одну строку (`executeUpdate() == 0`), и команда вернёт ошибку `"Элемент не найден или не принадлежит пользователю"`.

---

## 10. Отправлять логин и пароль с каждым запросом

### Что было в Lab6
Класс `StandartRequest` содержал только поле `name`. Авторизации не было.

### Что изменилось в Lab7
В [`StandartRequest`](file:///Users/igor/Projects/ITMO/Programming/lab7/common/src/main/java/com/itmo/util/request/StandartRequest.java) добавлены поля `login`, `password`, `userId`:
```java
private final String login;
private final String password;
private Long userId;
```
Конструктор расширен: `StandartRequest(String name, String login, String password)`.

Аналогично изменены все остальные Request-классы: `ElementRequest`, `IdRequest`, `CombinedRequest`, `StringRequest`, `InitRequest` — все теперь передают `login` и `password`.

На клиенте [`RequestBuilder`](file:///Users/igor/Projects/ITMO/Programming/lab7/client/src/main/java/com/itmo/util/request/RequestBuilder.java) получает `login`/`password` через конструктор и передаёт их при создании каждого запроса:
```java
return new StandartRequest(name, login, password);
return new ElementRequest(name, result, login, password);
// и т.д.
```

---

## 11. Многопоточная обработка запросов

### Что было в Lab6
Сервер работал **однопоточно** — все операции (чтение, обработка, отправка) выполнялись в одном потоке главного event-loop через `Selector` с NIO.

### Что изменилось в Lab7
В [`Server.java`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/network/Server.java) обработка разбита на **три этапа**, каждый с отдельной моделью потоков:

### Этап 1: Чтение запроса → `ForkJoinPool`
```java
private final ForkJoinPool readPool = new ForkJoinPool();

private void handleRead(SelectionKey key) {
    key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);  // снимаем OP_READ
    readPool.submit(() -> readRequest(key, client, session));     // читаем в пуле
}
```
При появлении данных на канале, задача десериализации передаётся в `readPool`. Пока идёт чтение, `OP_READ` снимается, чтобы Selector не дёргал тот же канал повторно.

### Этап 2: Обработка запроса → `ForkJoinPool`
```java
private final ForkJoinPool processPool = new ForkJoinPool();

// Внутри readRequest(), после десериализации:
processPool.submit(() -> processRequest(key, client, session, request));
```
После чтения и десериализации, выполнение бизнес-логики (поиск команды, авторизация, обращение к БД) передаётся во второй пул.

### Этап 3: Отправка ответа → `new Thread`
```java
private void sendResponse(...) {
    Thread responseThread = new Thread(() -> {
        session.prepareResponse(SerializationUtils.serialize(response));
        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }, "response-sender");
    responseThread.start();
}
```
Для сериализации и подготовки ответа создаётся новый поток. После подготовки он переключает `SelectionKey` в режим записи и будит Selector.

### Дополнительно
`sessions` заменён с `HashMap` на `ConcurrentHashMap` для потокобезопасного доступа из разных потоков.

В `shutdown()` добавлена остановка пулов:
```java
readPool.shutdownNow();
processPool.shutdownNow();
```

---

## 12. Синхронизация доступа к коллекции — `Collections.synchronizedXXX`

### Что было в Lab6
Коллекция — обычный `HashSet<>()`, без синхронизации (однопоточный сервер).

### Что изменилось в Lab7
В [`CollectionManager`](file:///Users/igor/Projects/ITMO/Programming/lab7/server/src/main/java/com/itmo/managers/CollectionManager.java#L21-L26) коллекция оборачивается в `Collections.synchronizedSet()`:
```java
public CollectionManager(Collection<T> collection) {
    Collection<T> initial = new HashSet<>();
    if (collection != null) initial.addAll(collection);
    this.collection = Collections.synchronizedSet((HashSet<T>) initial);
}
```

Для составных операций (итерация, remove + add) используются явные `synchronized`-блоки:
```java
public Status updateById(int id, T newElement) {
    synchronized (collection) {
        // remove old + add new — атомарно
    }
}

public Collection<T> snapshot() {
    synchronized (collection) {
        return new ArrayList<>(collection);  // безопасная копия
    }
}
```

Это нужно потому, что `Collections.synchronizedSet` синхронизирует **отдельные** операции (add, remove, size), но **итерация** требует ручной синхронизации. Метод `snapshot()` создаёт копию для безопасного обхода без блокировки.

---

## Сводная таблица изменённых файлов

### Новые файлы
| Файл | Назначение |
|---|---|
| `DatabaseManager.java` | CRUD-операции с `music_bands` через JDBC |
| `UserManager.java` | Регистрация / аутентификация пользователей, хэширование MD5 |
| `Register.java` | Серверная команда `register` |
| `create_db.sql` | SQL-схема: таблицы `users` и `music_bands` + sequence |

### Изменённые файлы

| Файл | Суть изменений |
|---|---|
| `ServerMain.java` | Убран `DATA_FILE`, `RemoteRuntime()` без аргументов |
| `RemoteRuntime.java` | Загрузка из БД, авторизация `authorizeAndExecute()`, убран `save` |
| `Server.java` | Три потоковых этапа: `ForkJoinPool` × 2 + `new Thread`, `ConcurrentHashMap` |
| `CollectionManager.java` | `Collections.synchronizedSet()`, `synchronized`-блоки, `snapshot()`, убраны `maxId`/`saveCollection()` |
| `MusicBand.java` | Добавлено поле `ownerId` |
| Все Request-классы | Добавлены `login`, `password` |
| `RequestBuilder.java` | Передаёт `login`/`password` в каждый запрос |
| `LocalRuntime.java` | Меню входа/регистрации, проверка credentials |
| `Add`, `Update`, `RemoveById`, `Clear`, `RemoveGreater`, `RemoveLower`, `AddIfMax` | Принимают `DatabaseManager`, паттерн «БД → память», проверка `userId` |

### Удалённая функциональность
| Что убрано | Причина |
|---|---|
| Команда `Save` | Данные пишутся в БД при каждой операции |
| `DumpManager` (использование) | Файловое хранение заменено на PostgreSQL |
| `maxId` / `updateMaxId()` | ID генерируется sequence в БД |
| `DATA_FILE` | Файл больше не нужен |
