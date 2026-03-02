package kg.attractor.java.lesson44.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String email;
    private String name;
    private String password;

    private List<Book> issuedBooks = new ArrayList<>();

    public List<Book> getIssuedBooks() {
        return issuedBooks;
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}