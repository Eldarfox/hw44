package kg.attractor.java.lesson44.models;

import java.util.List;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private List<Book> currentBooks;
    private List<Book> historyBooks;

    public Employee(int id, String firstName, String lastName,
                    List<Book> currentBooks,
                    List<Book> historyBooks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentBooks = currentBooks;
        this.historyBooks = historyBooks;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public List<Book> getCurrentBooks() { return currentBooks; }
    public List<Book> getHistoryBooks() { return historyBooks; }
}