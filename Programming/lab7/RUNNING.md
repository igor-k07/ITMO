# Запуск проекта

Ниже описаны два сценария запуска:

1. Сервер и клиент работают локально, PostgreSQL находится на удалённом сервере.
2. Сервер и PostgreSQL работают удалённо, клиент работает локально.

В примерах используются следующие значения:

```text
SSH_HOST  адрес удалённого сервера
SSH_USER  логин для SSH и PostgreSQL
SSH_PORT  2222
APP_PORT  5555
DB_PORT   5432
LOCAL_DB  15432
```

## Сборка

Выполнить в корне проекта:

```bash
cd /Users/igor/Projects/ITMO/Programming/lab7
mvn clean package
```

После сборки JAR-файлы находятся здесь:

```text
server/target/server-1.0-SNAPSHOT-jar-with-dependencies.jar
client/target/client-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Вариант 1: сервер и клиент локально, база удалённая

### 1. SSH-туннель к PostgreSQL

Открыть отдельный терминал и оставить его запущенным:

```bash
ssh -p 2222 -N \
-o ExitOnForwardFailure=yes \
-L 15432:pg:5432 \
SSH_USER@SSH_HOST
```

Например:

```bash
ssh -p 2222 -N \
-o ExitOnForwardFailure=yes \
-L 15432:pg:5432 \
s501603@se.ifmo.ru
```

Схема подключения:

```text
localhost:15432 -> удалённый сервер -> pg:5432
```

Проверить, что туннель слушает порт:

```bash
lsof -nP -iTCP:15432 -sTCP:LISTEN
```

### 2. Создание таблиц

Создать таблицы один раз:

```bash
psql -h localhost -p 15432 \
-U SSH_USER -d studs \
-f create_db.sql
```

Если `psql` не установлен на Mac, выполнить SQL на удалённом сервере:

```bash
ssh -p 2222 SSH_USER@SSH_HOST
psql -h pg -U SSH_USER -d studs -f ~/create_db.sql
```

### 3. Запуск сервера в IntelliJ IDEA

Открыть:

```text
Run -> Edit Configurations -> + -> Application
```

Указать:

```text
Name:
Server

Main class:
com.itmo.ServerMain

Use classpath of module:
server

Program arguments:
5555

Working directory:
/Users/igor/Projects/ITMO/Programming/lab7
```

В поле `Environment variables` добавить:

```text
DB_HOST=localhost;DB_PORT=15432;DB_NAME=studs;DB_USER=SSH_USER;DB_PASSWORD=POSTGRES_PASSWORD
```

После этого запустить конфигурацию `Server`.

### 4. Запуск клиента в IntelliJ IDEA

Создать вторую конфигурацию `Application`:

```text
Name:
Client

Main class:
com.itmo.ClientMain

Use classpath of module:
client

Program arguments:
localhost 5555

Working directory:
/Users/igor/Projects/ITMO/Programming/lab7
```

Переменные окружения клиенту не нужны.

Сначала запустить `Server`, затем `Client`.

### 5. Запуск через терминал

Вместо IntelliJ сервер можно запустить так:

```bash
export DB_HOST=localhost
export DB_PORT=15432
export DB_NAME=studs
export DB_USER=s501603
export DB_PASSWORD=quwWyeljqwj125fp

java -jar server/target/server-1.0-SNAPSHOT-jar-with-dependencies.jar 5555
```

В другом терминале запустить клиент:

```bash
java -jar client/target/client-1.0-SNAPSHOT-jar-with-dependencies.jar \
localhost 5555
```

## Вариант 2: сервер и база удалённые, клиент локальный

В этом варианте Java-сервер запускается на той же удалённой машине, через которую доступен PostgreSQL `pg`. Туннель к БД на Mac не нужен.

### 1. Передача файлов

Передать серверный JAR:

```bash
scp -P 2222 \
server/target/server-1.0-SNAPSHOT-jar-with-dependencies.jar \
SSH_USER@SSH_HOST:~/server.jar
```

Передать SQL-схему:

```bash
scp -P 2222 \
create_db.sql \
SSH_USER@SSH_HOST:~/create_db.sql
```

### 2. Подключение к удалённой машине

```bash
ssh -p 2222 SSH_USER@SSH_HOST
```

### 3. Создание таблиц

На удалённой машине выполнить один раз:

```bash
psql -h pg -U SSH_USER -d studs -f ~/create_db.sql
```

### 4. Настройка переменных окружения

На удалённой машине:

```bash
export DB_HOST=pg
export DB_PORT=5432
export DB_NAME=studs
export DB_USER=SSH_USER
export DB_PASSWORD=quwWyeljqwj125fp
```

### 5. Запуск сервера приложения

```bash
java -jar ~/server.jar 5555
```

Оставить это SSH-окно открытым.

### 6. Туннель приложения на Mac

В новом локальном терминале:

```bash
ssh -p 2222 -N \
-o ExitOnForwardFailure=yes \
-L 5555:localhost:5555 \
SSH_USER@SSH_HOST
```

Схема:

```text
localhost:5555 на Mac -> localhost:5555 на удалённой машине -> Java Server
```

### 7. Запуск локального клиента

```bash
java -jar client/target/client-1.0-SNAPSHOT-jar-with-dependencies.jar \
localhost 5555
```

Или в IntelliJ использовать конфигурацию клиента с аргументами:

```text
localhost 5555
```

## Пользователь приложения

После запуска клиента он попросит:

```text
Введите логин:
Введите пароль:
```

Это credentials пользователя приложения, а не обязательно пароль PostgreSQL.

Для создания нового пользователя выполнить команду:

```text
register
```

После регистрации доступны команды:

```text
show
info
add
update
remove_by_id
```

## Проверка портов

Проверить локальный порт базы:

```bash
lsof -nP -iTCP:15432 -sTCP:LISTEN
```

Проверить локальный порт приложения:

```bash
lsof -nP -iTCP:5555 -sTCP:LISTEN
```

Если порт `15432` занят, выбрать другой, например:

```bash
ssh -p 2222 -N -L 25432:pg:5432 SSH_USER@SSH_HOST
```

Тогда в IntelliJ нужно указать:

```text
DB_HOST=localhost;DB_PORT=25432;DB_NAME=studs;DB_USER=SSH_USER;DB_PASSWORD=POSTGRES_PASSWORD
```

## Остановка

Остановить сервер, клиент или SSH-туннель:

```text
Ctrl+C
```

## Разница между паролями

`DB_PASSWORD` используется Java-сервером для подключения к PostgreSQL.

Пароль, который вводится после запуска клиента, относится к пользователю приложения и хранится в таблице `users` в виде MD5-хэша.
