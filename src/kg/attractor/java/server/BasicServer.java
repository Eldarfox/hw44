package kg.attractor.java.server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import freemarker.template.*;
import kg.attractor.java.lesson44.models.User;
import kg.attractor.java.lesson44.service.UserService;

public abstract class BasicServer {

    private final HttpServer server;
    private final String dataDir = "data";
    private Map<String, RouteHandler> routes = new HashMap<>();
    private final Configuration freemarker = initFreeMarker();

    protected BasicServer(String host, int port) throws IOException {
        server = createServer(host, port);
        registerCommonHandlers();
    }

    private Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new java.io.File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void renderTemplate(HttpExchange exchange,
                               String templateFile,
                               Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);

            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(stream)) {

                temp.process(dataModel, writer);
                writer.flush();

                byte[] data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }


    public String getBody(HttpExchange exchange) {
        try (InputStream input = exchange.getRequestBody();
             InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            return reader.lines().collect(Collectors.joining());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void redirect303(HttpExchange exchange, String path) {
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, -1);
            exchange.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final void registerGet(String route, RouteHandler handler) {
        routes.put("GET " + route, handler);
    }

    protected final void registerPost(String route, RouteHandler handler) {
        routes.put("POST " + route, handler);
    }

    protected final Map<String, RouteHandler> getRoutes() {
        return routes;
    }

    private static String makeKey(String method, String route) {
        return method.toUpperCase() + " " + route;
    }

    private static String makeKey(HttpExchange exchange) {
        var method = exchange.getRequestMethod();
        var path = exchange.getRequestURI().getPath();

        var index = path.lastIndexOf(".");
        var extOrPath = index != -1 ? path.substring(index).toLowerCase() : path;

        return makeKey(method, extOrPath);
    }

    private static HttpServer createServer(String host, int port) throws IOException {
        System.out.printf("Starting server on http://%s:%s/%n", host, port);
        var address = new InetSocketAddress(host, port);
        return HttpServer.create(address, 50);
    }

    private void registerCommonHandlers() {
        server.createContext("/", this::handleIncomingServerRequests);

        registerGet("/", exchange ->
                redirect303(exchange, "/login")
        );

        registerFileHandler(".css", ContentType.TEXT_CSS);
        registerFileHandler(".html", ContentType.TEXT_HTML);
        registerFileHandler(".jpg", ContentType.IMAGE_JPEG);
        registerFileHandler(".png", ContentType.IMAGE_PNG);
    }

    protected final void registerFileHandler(String fileExt, ContentType type) {
        registerGet(fileExt,
                exchange -> sendFile(exchange, makeFilePath(exchange), type));
    }

    private void handleIncomingServerRequests(HttpExchange exchange) {
        var route = routes.getOrDefault(makeKey(exchange), this::respond404);

        try {
            route.handle(exchange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final void sendFile(HttpExchange exchange,
                                  Path pathToFile,
                                  ContentType contentType) {

        try {
            if (Files.notExists(pathToFile)) {
                respond404(exchange);
                return;
            }

            var data = Files.readAllBytes(pathToFile);
            sendByteData(exchange, ResponseCodes.OK, contentType, data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path makeFilePath(HttpExchange exchange) {
        return makeFilePath(exchange.getRequestURI().getPath());
    }

    protected Path makeFilePath(String... s) {
        return Path.of(dataDir, s);
    }

    protected final void sendByteData(HttpExchange exchange,
                                      ResponseCodes responseCode,
                                      ContentType contentType,
                                      byte[] data) throws IOException {

        exchange.getResponseHeaders().set("Content-Type", contentType.toString());
        exchange.sendResponseHeaders(responseCode.getCode(), data.length);

        try (var output = exchange.getResponseBody()) {
            output.write(data);
        }
    }

    private void respond404(HttpExchange exchange) {
        try {
            var data = "404 Not found".getBytes();
            sendByteData(exchange,
                    ResponseCodes.NOT_FOUND,
                    ContentType.TEXT_PLAIN,
                    data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void start() {
        server.start();
    }

    public String getCookies(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                .getOrDefault("Cookie", List.of(""))
                .get(0);
    }

    public void setCookie(HttpExchange exchange, String cookie) {
        exchange.getResponseHeaders().add("Set-Cookie", cookie);
    }
    public User getAuthorizedUser(HttpExchange exchange, UserService userService) {

        String raw = getCookies(exchange);
        if (raw == null || raw.isEmpty()) return null;

        String[] parts = raw.split(";");

        for (String part : parts) {
            if (part.trim().startsWith("SESSION_ID=")) {
                String sessionId = part.split("=")[1];
                return userService.getUserBySession(sessionId);
            }
        }

        return null;
    }
}