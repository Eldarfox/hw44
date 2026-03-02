package kg.attractor.java.lesson44.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.lesson44.ParseUrlEncoded;
import kg.attractor.java.lesson44.models.Book;
import kg.attractor.java.lesson44.models.User;
import kg.attractor.java.lesson44.service.UserService;
import kg.attractor.java.server.BasicServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookController {

    private final UserService userService;

    public BookController(UserService userService) {
        this.userService = userService;
    }

    public void booksGet(BasicServer server, HttpExchange exchange) throws IOException {

        User user = server.getAuthorizedUser(exchange, userService);

        if (user == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        List<Book> books = userService.getAllBooks();

        Map<String, Object> model = new HashMap<>();
        model.put("books", books);

        server.renderTemplate(exchange, "books.ftl", model);
    }

    public void bookGet(BasicServer server, HttpExchange exchange) throws IOException {

        User user = server.getAuthorizedUser(exchange, userService);

        if (user == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ParseUrlEncoded.parse(query, "&");

        int id = Integer.parseInt(params.get("id"));

        Book book = userService.getBookById(id);

        if (book == null) {
            server.redirect303(exchange, "/books");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("book", book);

        server.renderTemplate(exchange, "book.ftl", model);
    }

    public void issue(BasicServer server, HttpExchange exchange) throws IOException {

        User user = server.getAuthorizedUser(exchange, userService);

        if (user == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ParseUrlEncoded.parse(query, "&");

        int bookId = Integer.parseInt(params.get("bookId"));

        Book book = userService.getBookById(bookId);

        if (book == null) {
            server.redirect303(exchange, "/books");
            return;
        }

        boolean success = userService.issueBook(user, book);

        server.redirect303(exchange, "/profile");
    }

    public void returnBook(BasicServer server, HttpExchange exchange) throws IOException {

        User user = server.getAuthorizedUser(exchange, userService);

        if (user == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ParseUrlEncoded.parse(query, "&");

        int bookId = Integer.parseInt(params.get("bookId"));

        Book book = userService.getBookById(bookId);

        if (book == null) {
            server.redirect303(exchange, "/profile");
            return;
        }

        userService.returnBook(user, book);

        server.redirect303(exchange, "/profile");
    }
}