package service;

import db.Database;
import db.PasswordEncrypt;
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
        String login = (String) user.get("name");
        String password = (String) user.get("password");
        User userInDb = this.getUserByName(login);
        return Objects.equals(userInDb.getPassword(), PasswordEncrypt.encrypt(password));
    }
}
