package kg.attractor.java.lesson44.service;

import kg.attractor.java.lesson44.models.Book;
import kg.attractor.java.lesson44.models.Employee;

import java.io.*;
import java.util.*;

public class EmployeeService {

    private final String file = "data/employees.json";

    private List<Employee> employees = new ArrayList<>();
    private Map<String, Employee> sessions = new HashMap<>();

    private List<Book> books = List.of(
            new Book(1,"Harry Potter","Rowling","Magic story","AVAILABLE" ,"2.jpg"),
            new Book(2,"Clean Code","Robert Martin","Programming","AVAILABLE","3.jpg"),
            new Book(3,"1984","Orwell","Dystopia","AVAILABLE","4.jpg")
    );

    public EmployeeService(){
        load();
    }

    private void load(){
        try{
            File f = new File(file);
            if(!f.exists())
                return;

            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;

            while((line = reader.readLine()) != null){
                String[] p = line.split(";");
                Employee e = new Employee(
                        Integer.parseInt(p[0]),
                        p[1],
                        p[2],
                        p[3],
                        p[4]
                );
                if(p.length > 5){
                    String[] issuedIds = p[5].split(",");
                    for(String id : issuedIds){

                        if(id.isEmpty())
                            continue;

                        Book b = getBookById(Integer.parseInt(id));

                        if(b != null){
                            e.getIssuedBooks().add(b);
                            b.setStatus("ISSUED");
                        }
                    }
                }
                if(p.length > 6){
                    String[] historyIds = p[6].split(",");
                    for(String id : historyIds){

                        if(id.isEmpty())
                            continue;

                        Book b = getBookById(Integer.parseInt(id));

                        if(b != null){
                            e.getHistoryBooks().add(b);
                        }
                    }
                }
                employees.add(e);
            }
            reader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void save(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for(Employee e : employees){

                StringBuilder issued = new StringBuilder();
                for(Book b : e.getIssuedBooks()){
                    issued.append(b.getId()).append(",");
                }

                if(issued.length() > 0)
                    issued.deleteCharAt(issued.length()-1);

                StringBuilder history = new StringBuilder();
                for(Book b : e.getHistoryBooks()){
                    history.append(b.getId()).append(",");
                }

                if(history.length() > 0)
                    history.deleteCharAt(history.length()-1);

                writer.write(
                        e.getId()+";"
                                +e.getFirstName()+";"
                                +e.getLastName()+";"
                                +e.getEmail()+";"
                                +e.getPassword()+";"
                                +issued+";"
                                +history
                );

                writer.newLine();
            }

            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getNextId(){
        if(employees.isEmpty())
            return 1;
        return employees.get(employees.size()-1).getId()+1;
    }

    public boolean register(Employee employee){
        for(Employee e : employees){
            if(e.getEmail().equals(employee.getEmail()))
                return false;
        }
        employees.add(employee);
        save();
        return true;
    }

    public Employee login(String email,String password){
        for(Employee e : employees){
            if(e.getEmail().equals(email)
                    && e.getPassword().equals(password))
                return e;
        }
        return null;
    }

    public String createSession(Employee employee){
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, employee);
        return sessionId;
    }

    public Employee getUserBySession(String sessionId){
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId){
        sessions.remove(sessionId);
    }

    public List<Book> getAllBooks(){
        return books;
    }

    public Book getBookById(int id){
        for(Book b : books){
            if(b.getId()==id)
                return b;
        }
        return null;
    }

    public boolean issueBook(Employee employee, Book book){
        if(employee.getIssuedBooks().size() >= 2)
            return false;

        for(Book b : employee.getIssuedBooks()){
            if(b.getId() == book.getId())
                return false;
        }

        employee.getIssuedBooks().add(book);

        if(!employee.getHistoryBooks().contains(book)){
            employee.getHistoryBooks().add(book);
        }
        book.setStatus("ISSUED");
        save();
        return true;
    }

    public void returnBook(Employee employee, Book book) {
        employee.getIssuedBooks().removeIf(
                b -> b.getId() == book.getId()
        );
        book.setStatus("AVAILABLE");
        save();
    }
}