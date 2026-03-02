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

    <p><strong>Имя:</strong> ${user.name}</p>
    <p><strong>Email:</strong> ${user.email}</p>

    <h3>Мои книги</h3>

    <#if user.issuedBooks?size == 0>
        <p>У вас нет книг</p>
    <#else>
        <#list user.issuedBooks as book>
            <div>
                ${book.title}
                <a href="/return?bookId=${book.id}">Вернуть</a>
            </div>
        </#list>
    </#if>

    <hr>
    <a href="/books"> К библиотеке</a>
    <br><br>
    <a href="/logout">Выйти</a>
</div>

</body>
</html>