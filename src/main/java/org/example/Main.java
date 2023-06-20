package org.example;

import db.Database;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
        Database db = new Database();
        //db.createUser("user", "12345");
        //db.createGroup("cat", "dddd");
        //db.createProduct("test", 300, 28, "desc", "ukraine", "fruit");
        //System.out.println(db.getProductById(21));
        //db.createProduct("test2", 300, 28, "desc", "ukraine", "fruit");
       // System.out.println(db.getAllGroups());
        //db.updateProduct(8,"apple", 300, 10000, "desc", "ukraine", "fruit");
      // db.deleteProduct(//"ORANGE");
        db.deleteGroup(3);
       // db.updateGroup(2, "nnnn", "");
       //db.addAmountOfProduct(100, 22);
    }
}