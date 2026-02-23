<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Сотрудник</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<div class="container">
    <h1>${employee.firstName} ${employee.lastName}</h1>

    <h2>Текущие книги</h2>

    <#if employee.currentBooks?size == 0>
        <p class="empty">Книг на руках нет</p>
    <#else>
        <#list employee.currentBooks as book>
            <div class="book-item">${book.title}</div>
        </#list>
    </#if>

    <h2>История</h2>

    <#if employee.historyBooks?size == 0>
        <p class="empty">Истории пока нет</p>
    <#else>
        <#list employee.historyBooks as book>
            <div class="book-item">${book.title}</div>
        </#list>
    </#if>

</div>

</body>
</html>