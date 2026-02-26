<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<div class="container">
    <h2>Вход</h2>

    <form method="post" action="/login">
        <input type="email" name="email" placeholder="Email" required>
        <input type="password" name="password" placeholder="Пароль" required>
        <button type="submit">Войти</button>
    </form>

    <p>Нет аккаунта? <a href="/register">Регистрация</a></p>

    <#if error??>
        <p class="error">${error}</p>
    </#if>
</div>

</body>
</html>