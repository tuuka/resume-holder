# RESUME HOLDER
Игрушечный проект в рамках изучения языка Java. Представляет собой веб-приложение для хранения резюме, реализующее функции [CRUD](https://ru.wikipedia.org/wiki/CRUD). Работающее приложение с фейковыми данными можно посмотреть [здесь](https://tuuka-cv-demo.appspot.com).

## Реализация
Итоговая реализация использует в качестве хранилища PostgreSQL базу данных. Однако, в проекте реализованы хранилища как на основе различных списков в памяти, так и файловые хранилища на основе Json, XML и пр. Все реализации хранилищ покрыты тестами JUnit5. 

## Особенности
Ввиду того, что на проект несет некий "базовый" характер, все выполнено без каких-либо сборщиков (типа Maven), без каких-либо фреймворков (типа Spring) с использованием древней технологии сервлетов и JSP исключительно средствами [IntelliJ IDEA](https://www.jetbrains.com/ru-ru/idea/), что оказалось весьма изощрённым (но поучительным) извращением.
Фронтенд слеплен "на коленке" чисто для демонстрации java-бекенда.