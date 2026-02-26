package kg.attractor.java.lesson44.service;

import kg.attractor.java.lesson44.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final Map<String, User> users = new HashMap<>();

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