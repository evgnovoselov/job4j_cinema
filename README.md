# job4j_cinema

[![Java CI with Maven](https://github.com/evgnovoselov/job4j_cinema/actions/workflows/maven.yml/badge.svg)](https://github.com/evgnovoselov/job4j_cinema/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/evgnovoselov/job4j_cinema/branch/master/graph/badge.svg?token=CTZjFEJLum)](https://codecov.io/gh/evgnovoselov/job4j_cinema)

## Описание проекта
Данный проект представляет реализацию кинотеатра с выбором места на сеанс фильма и приобретение билета.

В системе есть авторизация и регистрации пользователя, и покупка билета возможна только авторизованному пользователю.

Используя Docker Compose и демонстрационные данные, можно легко запустить проект и посмотреть его работоспособность.

Проект использует пул соединения с базой данных и слоистую структуру разбитую по пакетам. Так же проект имеет большой процент покрытия тестами, что облегчает добавления нового функционала или изменение прежнего. За генерацию ответов на запросы браузера используется серверный шаблонизатор Thymeleaf.

## Стек технологий
При разработке и тестировании использовались следующие технологии:

- Java 17
- Spring Boot 3.1.1
- Liquibase 4.20.0
- PostgreSQL JDBC Driver 42.6.0
- Apache Commons DBCP 2.9.0
- Sql2o 1.6.0
- H2database 2.1.214
- Thymeleaf 3.1.1
- WebJars 0.52 (Для добавления библиотек: Bootstrap 5.3.0 и Bootstrap Icons 1.10.5)
- Checkstyle 3.3.0
- JaCoCo 0.8.10
- JUnit 5.9.3
- Mockito 5.3.1
- AssertJ 3.24.2
- Docker Compose 1.25.0 (Образы PostgreSQL 15 и Adminer 4)

## Требования к окружению

## Запуск проекта

## Взаимодействие с приложением

## Контакты

Спасибо, что дочитали до конца, данный сервис реализован мной. Открыт для вопросов и предложений.

С уважением Евгений Новоселов.

Телеграмм: https://t.me/enovoselov