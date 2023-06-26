package service;

import db.Database;
import db.PasswordEncryptor;
import db.User;
import org.json.simple.JSONObject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;

public class UserService {
    public Database db;

    public UserService(Database db) {this.db = db;}
    public User getUserByName(String name) throws SQLException {
        return this.db.getUserByName(name);
    }

    public boolean login(JSONObject user) throws SQLException, NoSuchAlgorithmException {
        String password = (String) user.get("password");
        String login = (String) user.get("name");
        User userInDb = this.getUserByName(login);
        System.out.println(userInDb);
        if (userInDb != null) {
            System.out.println("is equal " + Objects.equals(userInDb.getPassword(), PasswordEncryptor.encrypt(password)));
            return Objects.equals(userInDb.getPassword(), PasswordEncryptor.encrypt(password));
        } else {
            return false;
        }
    }
}
