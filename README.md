# Filmorate Java Project
Это учебный проект от Yandex Practicum.

## Структура Базы данных(ER-модель)
![Untitled (3)](https://github.com/Eenot/java-filmorate/assets/142352553/43fb8379-9976-4001-b4e8-4b4445762094)



## Таблица film

Таблица ```film``` включает в себя основную информацию о фильмах:
* ```film_id```: Primary Key, уникальный идентификатор фильма
* ```name```: название фильма
* ```description```: описание фильма
* ```release_date```: дата выхода фильма в прокат
* ```duration```: продолжительность фильма
* ```rating_id```: возрастной рейтинг фильма

## Таблица users
Таблица ```users``` содержит всю основную информацию о пользователях:
* ```user_id```: первичный ключ, уникальный идентификатор пользователя
* ```user_name```: имя пользователя
* ```login```: логин
* ```email```: электронная почта
* ```birthday```: день рождения пользователя


## _Основные SQL-запросы_
### Основные запросы к базе данных(таблица фильмов)

**Get All Films**
```
SELECT * FROM film;
```

**Get Film By Id**
```
SELECT * FROM film WHERE film_id = ?;
```

### Основные запросы к базе данных(таблица пользователей)

**Get all users**
```
SELECT * FROM users;
```

**Get User By Id**
```
SELECT * FROM users WHERE user_id = ?;
```
