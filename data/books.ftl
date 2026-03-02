<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список книг</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<h1>Библиотека офиса</h1>

<#list books as book>
    <div class="book-card">
        <h2>${book.title}</h2>
        <p><strong>Автор:</strong> ${book.author}</p>

        <p class="status
            <#if book.status == "AVAILABLE">available<#else>issued</#if>">
            Статус: ${book.status}
        </p>

        <a href="/book?id=${book.id}">Подробнее</a>
        <#if book.status == "AVAILABLE">
            <a href="/issue?bookId=${book.id}">Взять книгу</a>
        </#if>
        <a href="/profile">← Назад в профиль</a>
    </div>
</#list>

</body>
</html>