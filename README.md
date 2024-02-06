# Filmorate Java Project
Это учебный проект от Yandex Practicum.

## Структура Базы данных(ER-модель)
![Untitled (2)](https://github.com/Eenot/java-filmorate/assets/142352553/4bb98a4c-1f73-4f60-897c-4d8db98e1632)


## Таблица Films

Таблица ```Films``` включает в себя основную информацию о фильмах:
* ```Film_id```: Primary Key, уникальный идентификатор фильма
* ```Name```: название фильма
* ```Description```: описание фильма
* ```Release_date```: дата выхода фильма в прокат
* ```Duration```: продолжительность фильма
* ```Rating_id```: возрастной рейтинг фильма

## Таблица Users
Таблица ```APP_USERS``` содержит всю основную информацию о пользователях:
* ```USER_ID```: первичный ключ, уникальный идентификатор пользователя
* ```USER_NAME```: имя пользователя
* ```LOGIN```: логин
* ```EMAIL```: электронная почта
* ```BIRTHDAY```: день рождения пользователя


## _Основные SQL-запросы_
### Основные запросы к базе данных(таблица фильмов)

**Get All Films**
```
SELECT * FROM Film;
```

**Get Film By Id**
```
SELECT * FROM Film WHERE Film_id = ?;
```

**Get Film Rating**
```
SELECT r.Rating_id, r.Rating_name
FROM Ratings AS r
JOIN Films AS f ON r.Rating_id = f.Rating_id
WHERE f.Film_id = ?;
```


### Основные запросы к базе данных(таблица пользователей)

**Get all users**
```
SELECT * FROM Users;
```

**Get User By Id**
```
SELECT * FROM Users WHERE User_id = ?;
```
