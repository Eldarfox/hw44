package kg.attractor.java.lesson44.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.lesson44.ParseUrlEncoded;
import kg.attractor.java.lesson44.models.Employee;
import kg.attractor.java.lesson44.service.EmployeeService;
import kg.attractor.java.server.BasicServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthController {

    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void registerGet(BasicServer server, HttpExchange exchange) throws IOException {
        server.renderTemplate(exchange, "register.ftl", null);
    }

    public void registerPost(BasicServer server, HttpExchange exchange) throws IOException {

        String raw = server.getBody(exchange);
        Map<String, String> data = ParseUrlEncoded.parse(raw, "&");

        Employee employee = new Employee(
                employeeService.getNextId(),
                data.get("name"),
                "",
                data.get("email"),
                data.get("password")
        );

        boolean success = employeeService.register(employee);

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

        Employee employee = employeeService.login(
                data.get("email"),
                data.get("password")
        );

        if (employee == null) {

            Map<String, Object> model = new HashMap<>();
            model.put("error", "Неверный email или пароль");

            server.renderTemplate(exchange, "login.ftl", model);
            return;
        }

        String sessionId = employeeService.createSession(employee);

        String cookie = "SESSION_ID=" + sessionId +
                "; Max-Age=600; HttpOnly; Path=/";

        server.setCookie(exchange, cookie);

        server.redirect303(exchange, "/profile");
    }

    public void profileGet(BasicServer server, HttpExchange exchange) throws IOException {

        Employee employee = server.getAuthorizedUser(exchange, employeeService);

        if (employee == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("employee", employee);

        server.renderTemplate(exchange, "profile.ftl", model);
    }

    public void logout(BasicServer server, HttpExchange exchange) throws IOException {

        String raw = server.getCookies(exchange);

        if (raw != null) {

            String[] parts = raw.split(";");

            for (String part : parts) {

                if (part.trim().startsWith("SESSION_ID=")) {

                    String sessionId = part.split("=")[1];
                    employeeService.removeSession(sessionId);

                }
            }
        }

        server.setCookie(exchange,
                "SESSION_ID=; Max-Age=0; HttpOnly; Path=/");

        server.redirect303(exchange, "/login");
    }
}