package kg.attractor.java.lesson44.service;

import kg.attractor.java.lesson44.models.Book;
import kg.attractor.java.lesson44.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserService {

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, User> sessions = new HashMap<>();

    private final List<Book> books = List.of(
            new Book(1, "Harry Potter", "J.K. Rowling", "Magic story", "AVAILABLE"),
            new Book(2, "Clean Code", "Robert Martin", "Programming book", "AVAILABLE"),
            new Book(3, "1984", "George Orwell", "Dystopia", "AVAILABLE")
    );
    public List<Book> getAllBooks() {
        return books;
    }

    public Book getBookById(int id) {
        return books.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public User getUserBySession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public boolean issueBook(User user, Book book) {
        if (user.getIssuedBooks().size() >= 2) {
            return false;
        }

        if (!book.getStatus().equals("AVAILABLE")) {
            return false;
        }

        boolean alreadyHas = user.getIssuedBooks()
                .stream()
                .anyMatch(b -> b.getId() == book.getId());

        if (alreadyHas) {
            return false;
        }

        book.setStatus("ISSUED");
        user.getIssuedBooks().add(book);

        return true;
    }

    public void returnBook(User user, Book book) {
        user.getIssuedBooks().remove(book);
        book.setStatus("AVAILABLE");
    }
    public boolean register(String email, String name, String password) {

        if (users.containsKey(email)) {
            return false;
        }

        users.put(email, new User(email, name, password));
        return true;
    }

    public User login(String email, String password) {

        User user = users.get(email);

        if (user == null) return null;

        if (!user.getPassword().equals(password)) return null;

        return user;
    }
}