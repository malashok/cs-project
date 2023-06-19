package org.example;

import db.Database;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
        Database db = new Database();
        db.createUser("user", "12345");
        //db.createGroup("fruit", "dddd");
        //db.createProduct("terst1", 300, 28, "desc", "ukraine", "fruit");
    }
}