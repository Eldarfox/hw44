package kg.attractor.java.lesson44.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.lesson44.ParseUrlEncoded;
import kg.attractor.java.lesson44.models.User;
import kg.attractor.java.lesson44.service.UserService;
import kg.attractor.java.server.BasicServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public void registerGet(BasicServer server, HttpExchange exchange) throws IOException {
        server.renderTemplate(exchange, "register.ftl", null);
    }

    public void registerPost(BasicServer server, HttpExchange exchange) throws IOException {

        String raw = server.getBody(exchange);
        Map<String, String> data = ParseUrlEncoded.parse(raw, "&");

        boolean success = userService.register(
                data.get("email"),
                data.get("name"),
                data.get("password")
        );

        if (!success) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Пользователь уже существует");
            server.renderTemplate(exchange, "register.ftl", model);
            return;
        }

        server.redirect303(exchange, "/login");
    }

    public void loginGet(BasicServer server, HttpExchange exchange) throws IOException {
        server.renderTemplate(exchange, "login.ftl", null);
    }

    public void loginPost(BasicServer server, HttpExchange exchange) throws IOException {

        String raw = server.getBody(exchange);
        Map<String, String> data = ParseUrlEncoded.parse(raw, "&");

        User user = userService.login(
                data.get("email"),
                data.get("password")
        );

        if (user == null) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Неверный email или пароль");
            server.renderTemplate(exchange, "login.ftl", model);
            return;
        }

        String sessionId = userService.createSession(user);

        String cookie = "SESSION_ID=" + sessionId +
                "; Max-Age=600; HttpOnly; Path=/";

        server.setCookie(exchange, cookie);

        server.redirect303(exchange, "/profile");
    }

    public void profileGet(BasicServer server, HttpExchange exchange) throws IOException {

        User user = server.getAuthorizedUser(exchange, userService);

        if (user == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("user", user);

        server.renderTemplate(exchange, "profile.ftl", model);
    }

    public void logout(BasicServer server, HttpExchange exchange) throws IOException {

        User user = server.getAuthorizedUser(exchange, userService);

        if (user != null) {
            String raw = server.getCookies(exchange);

            if (raw != null) {
                String[] parts = raw.split(";");

                for (String part : parts) {
                    if (part.trim().startsWith("SESSION_ID=")) {
                        String sessionId = part.split("=")[1];
                        userService.removeSession(sessionId);
                    }
                }
            }
        }

        server.setCookie(exchange,
                "SESSION_ID=; Max-Age=0; HttpOnly; Path=/");

        server.redirect303(exchange, "/login");
    }
}