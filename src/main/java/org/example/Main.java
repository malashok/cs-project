package org.example;

import db.Database;

import java.sql.SQLException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws SQLException {
        Database db = new Database();
        db.create_group("fruit", "dddd");
        db.createProduct("terst1", 300, 28, "desc", "ukraine", "fruit");
    }
}