<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${book.title}</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<div class="container">
    <h1>${book.title}</h1>

    <img src="/images/${book.image}" width="200">

    <p><strong>Автор:</strong> ${book.author}</p>

    <p>
        <strong>Описание:</strong><br>
        ${book.description}
    </p>

    <p class="status
        <#if book.status == "AVAILABLE">available<#else>issued</#if>">
        Статус: ${book.status}
    </p>

    <a class="button" href="/books">Назад к списку</a>
</div>

</body>
</html>