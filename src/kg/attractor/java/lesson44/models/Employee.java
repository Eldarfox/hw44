package kg.attractor.java.lesson44.models;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;

    private String email;
    private String password;

    private List<Book> issuedBooks = new ArrayList<>();
    private List<Book> historyBooks = new ArrayList<>();

    public Employee(int id,
                    String firstName,
                    String lastName,
                    String email,
                    String password) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public List<Book> getHistoryBooks() {
        return historyBooks;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Book> getIssuedBooks() {
        return issuedBooks;
    }
}