package kg.attractor.java.lesson44;

import kg.attractor.java.lesson44.controller.AuthController;
import kg.attractor.java.lesson44.service.UserService;
import kg.attractor.java.server.BasicServer;

import java.io.IOException;

public class Lesson44Server extends BasicServer {

    private final UserService userService = new UserService();
    private final AuthController authController = new AuthController(userService);

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);

        registerGet("/register", ex -> authController.registerGet(this, ex));
        registerPost("/register", ex -> authController.registerPost(this, ex));

        registerGet("/login", ex -> authController.loginGet(this, ex));
        registerPost("/login", ex -> authController.loginPost(this, ex));

        registerGet("/profile", ex -> authController.profileGet(this, ex));
    }
}