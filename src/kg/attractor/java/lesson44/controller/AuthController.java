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
    private User currentUser;

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

        currentUser = user;
        server.redirect303(exchange, "/profile");
    }

    public void profileGet(BasicServer server, HttpExchange exchange) throws IOException {

        Map<String, Object> model = new HashMap<>();

        if (currentUser == null) {
            model.put("name", "Некий пользователь");
            model.put("email", "unknown@mail.com");
        } else {
            model.put("name", currentUser.getName());
            model.put("email", currentUser.getEmail());
        }

        server.renderTemplate(exchange, "profile.ftl", model);
    }
}