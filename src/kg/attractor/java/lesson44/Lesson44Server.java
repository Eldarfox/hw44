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
            // путь к каталогу в котором у нас хранятся шаблоны
            // это может быть совершенно другой путь, чем тот, откуда сервер берёт файлы
            // которые отправляет пользователю
            cfg.setDirectoryForTemplateLoading(new File("data"));

            // прочие стандартные настройки о них читать тут
            // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html
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
            // Загружаем шаблон из файла по имени.
            // Шаблон должен находится по пути, указанном в конфигурации
            Template temp = freemarker.getTemplate(templateFile);

            // freemarker записывает преобразованный шаблон в объект класса writer
            // а наш сервер отправляет клиенту массивы байт
            // по этому нам надо сделать "мост" между этими двумя системами

            // создаём поток, который сохраняет всё, что в него будет записано в байтовый массив
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // создаём объект, который умеет писать в поток и который подходит для freemarker
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                // обрабатываем шаблон заполняя его данными из модели
                // и записываем результат в объект "записи"
                temp.process(dataModel, writer);
                writer.flush();

                // получаем байтовый поток
                var data = stream.toByteArray();

                // отправляем результат клиенту
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private SampleDataModel getSampleDataModel() {
        // возвращаем экземпляр тестовой модели-данных
        // которую freemarker будет использовать для наполнения шаблона
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