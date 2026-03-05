<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Profile</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<div class="container">
    <h2>Профиль</h2>
    <p><strong>Имя:</strong> ${employee.firstName}</p>
    <p><strong>Email:</strong> ${employee.email}</p>

    <h3>Книги на руках</h3>

    <#if employee.issuedBooks?size == 0>
        <p>У вас нет книг на руках</p>
    <#else>
        <#list employee.issuedBooks as book>
            <div>
                ${book.title}
                <a href="/return?bookId=${book.id}">Вернуть</a>
            </div>
        </#list>
    </#if>

    <hr>

    <h3>Книги которые вы брали</h3>

    <#if employee.historyBooks?size == 0>
        <p>Вы ещё не брали книги</p>
    <#else>
        <#list employee.historyBooks as book>
            <div>
                ${book.title}
            </div>
        </#list>
    </#if>

    <hr>
    <a href="/books">К библиотеке</a>
    <br><br>
    <a href="/logout">Выйти</a>
</div>

</body>
</html>