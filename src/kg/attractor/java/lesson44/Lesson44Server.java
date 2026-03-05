package kg.attractor.java.lesson44;

import kg.attractor.java.lesson44.controller.AuthController;
import kg.attractor.java.lesson44.controller.BookController;
import kg.attractor.java.lesson44.service.EmployeeService;
import kg.attractor.java.server.BasicServer;

import java.io.IOException;

public class Lesson44Server extends BasicServer {

    private final EmployeeService employeeService = new EmployeeService();

    private final AuthController authController = new AuthController(employeeService);
    private final BookController bookController = new BookController(employeeService);

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);

        registerGet("/register", ex -> authController.registerGet(this, ex));
        registerPost("/register", ex -> authController.registerPost(this, ex));

        registerGet("/login", ex -> authController.loginGet(this, ex));
        registerPost("/login", ex -> authController.loginPost(this, ex));

        registerGet("/profile", ex -> authController.profileGet(this, ex));
        registerGet("/logout", ex -> authController.logout(this, ex));

        registerGet("/books", ex -> bookController.booksGet(this, ex));
        registerGet("/book", ex -> bookController.bookGet(this, ex));
        registerGet("/issue", ex -> bookController.issue(this, ex));
        registerGet("/return", ex -> bookController.returnBook(this, ex));
    }
}