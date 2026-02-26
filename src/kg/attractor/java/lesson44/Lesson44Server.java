package kg.attractor.java.lesson44;

import kg.attractor.java.lesson44.models.Book;
import kg.attractor.java.lesson44.models.Employee;
import java.util.*;
import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;
import java.util.HashMap;
import java.util.Map;

import java.io.*;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/sample", this::freemarkerSampleHandler);
        registerGet("/books", this::booksHandler);
        registerGet("/book", this::bookHandler);
        registerGet("/employee", this::employeeHandler);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void freemarkerSampleHandler(HttpExchange exchange) {
        renderTemplate(exchange, "sample.html", getSampleDataModel());
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(dataModel, writer);
                writer.flush();
                var data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private SampleDataModel getSampleDataModel() {
        return new SampleDataModel();
    }

    private void booksHandler(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

        List<Book> books = List.of(
                new Book(1, "Harry Potter", "J.K. Rowling", "Magic story", "AVAILABLE"),
                new Book(2, "Clean Code", "Robert Martin", "Programming book", "ISSUED"),
                new Book(3, "1984", "George Orwell", "Dystopia", "AVAILABLE")
        );

        Map<String, Object> data = new HashMap<>();
        data.put("books", books);

        renderTemplate(exchange, "books.ftl", data);
    }

    private void bookHandler(com.sun.net.httpserver.HttpExchange exchange) throws IOException {

        Book book = new Book(
                1,
                "Harry Potter",
                "J.K. Rowling",
                "Magic story about a wizard.",
                "AVAILABLE"
        );

        Map<String, Object> data = new HashMap<>();
        data.put("book", book);

        renderTemplate(exchange, "book.ftl", data);
    }

    private void employeeHandler(HttpExchange exchange) throws IOException {
        List<Book> current = List.of(
                new Book(1, "Clean Code", "Robert Martin", "Programming book", "ISSUED")
        );

        List<Book> history = List.of(
                new Book(2, "1984", "George Orwell", "Dystopia", "RETURNED")
        );

        Employee employee = new Employee(
                1,
                "Ivan",
                "Ivanov",
                current,
                history
        );

        Map<String, Object> data = new HashMap<>();
        data.put("employee", employee);

        renderTemplate(exchange, "employee.ftl", data);
    }

    public static void main(String[] args) throws IOException {
        new Lesson44Server("localhost", 8089).start();
    }
}