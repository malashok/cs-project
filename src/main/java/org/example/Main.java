package org.example;

import db.Database;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
        Database db = new Database();
       // db.createUser("user", "12345");
        //db.createGroup("fruit", "dddd");
        //db.createProduct("test", 300, 28, "desc", "ukraine", "fruit");
        //db.createProduct("test2", 300, 28, "desc", "ukraine", "fruit");
        System.out.println(db.getAllGroups());
       // db.updateProduct(8,"apple", 300, 10000, "desc", "ukraine", "fruit");
       //db.deleteProduct("ORANGE");
        //db.deleteGroup("fruit");
        //db.updateGroup(2, "nnnn", "");
    }
}