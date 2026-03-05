package kg.attractor.java.lesson44.models;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String status;
    private String image;

    public Book(int id, String title, String author, String description, String status, String image) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.status = status;
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getImage() { return image; }
}