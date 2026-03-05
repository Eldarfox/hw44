package kg.attractor.java.lesson44.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.lesson44.ParseUrlEncoded;
import kg.attractor.java.lesson44.models.Book;
import kg.attractor.java.lesson44.models.Employee;
import kg.attractor.java.lesson44.service.EmployeeService;
import kg.attractor.java.server.BasicServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookController {

    private final EmployeeService employeeService;

    public BookController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void booksGet(BasicServer server, HttpExchange exchange) throws IOException {

        Employee employee = server.getAuthorizedUser(exchange, employeeService);

        if (employee == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        List<Book> books = employeeService.getAllBooks();

        Map<String, Object> model = new HashMap<>();
        model.put("books", books);

        server.renderTemplate(exchange, "books.ftl", model);
    }

    public void bookGet(BasicServer server, HttpExchange exchange) throws IOException {

        Employee employee = server.getAuthorizedUser(exchange, employeeService);

        if (employee == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ParseUrlEncoded.parse(query, "&");

        int id = Integer.parseInt(params.get("id"));

        Book book = employeeService.getBookById(id);

        if (book == null) {
            server.redirect303(exchange, "/books");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("book", book);

        server.renderTemplate(exchange, "book.ftl", model);
    }

    public void issue(BasicServer server, HttpExchange exchange) throws IOException {

        Employee employee = server.getAuthorizedUser(exchange, employeeService);

        if (employee == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ParseUrlEncoded.parse(query, "&");

        int bookId = Integer.parseInt(params.get("bookId"));

        Book book = employeeService.getBookById(bookId);

        if (book == null) {
            server.redirect303(exchange, "/books");
            return;
        }

        employeeService.issueBook(employee, book);

        server.redirect303(exchange, "/profile");
    }

    public void returnBook(BasicServer server, HttpExchange exchange) throws IOException {

        Employee employee = server.getAuthorizedUser(exchange, employeeService);

        if (employee == null) {
            server.redirect303(exchange, "/login");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = ParseUrlEncoded.parse(query, "&");

        int bookId = Integer.parseInt(params.get("bookId"));

        Book book = employeeService.getBookById(bookId);

        if (book == null) {
            server.redirect303(exchange, "/profile");
            return;
        }

        employeeService.returnBook(employee, book);

        server.redirect303(exchange, "/profile");
    }
}