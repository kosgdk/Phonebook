# Phonebook

Простейшая телефонная книга на Java + Spring MVC + Hibernate.
Работает на embedded сервере Tomcat 7.
Использует in-memory базу данных HSQL.

### Сборка и запуск

Необходино наличие установленных JRE 1.7 и Maven

1 шаг:

```
mvn package
```

2 шаг:
```
java -jar target/phonebook.jar
```

3 шаг:
```
http://localhost:8080/phonebook/
```