package db;

import javax.crypto.spec.DESedeKeySpec;
import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    public Database() {
        try {
            this.initialization();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet result_set;
    private Connection connection;
    private PreparedStatement sql_query;

    public void initialization() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "root", "root");
            this.sql_query = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS GROUPS (" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL ," +
                            "DESCRIPTION VARCHAR(100))"
            );
            this.sql_query.execute();

            this.sql_query = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS PRODUCTS(" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL," +
                            "PRICE NUMERIC(10, 2) NOT NULL," +
                            "AMOUNT INTEGER NOT NULL," +
                            "DESCRIPTION VARCHAR(100)," +
                            "PRODUCER VARCHAR(50) NOT NULL," +
                            "GROUP_NAME VARCHAR(50) NOT NULL,"+
                            "FOREIGN KEY (GROUP_NAME) REFERENCES GROUPS (NAME) ON UPDATE CASCADE ON DELETE CASCADE)");
            this.sql_query.execute();

            this.sql_query = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS USERS(" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL," +
                            "PASSWORD VARCHAR(50))"
            );
            this.sql_query.execute();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void createGroup(String name, String description) throws SQLException {
        try {
        this.sql_query = this.connection.prepareStatement("INSERT INTO GROUPS(name, description) VALUES (?, ?)");
        this.sql_query.setString(1, name);
        this.sql_query.setString(2, description);
            this.sql_query.execute();

        } catch (SQLException e) {
            this.sql_query.getConnection().rollback();
        }
    }

    public void createProduct(String name, double price, int amount, String description, String producer, String groupName) throws SQLException {
        try {
            this.sql_query = this.connection.prepareStatement("INSERT INTO PRODUCTS(name, price, amount, group_name, description, producer) VALUES (?, ?, ?, ?, ?, ?)");
        this.sql_query.setString(1, name);
        this.sql_query.setDouble(2, price);
        this.sql_query.setInt(3, amount);
        this.sql_query.setString(4, groupName);
        this.sql_query.setString(5, description);
        this.sql_query.setString(6, producer);

            this.sql_query.execute();
        } catch (SQLException e) {
            //this.sql_query.getConnection().rollback();
            throw new SQLException(e);
    }
    }

    public void createUser(String name, String password) throws SQLException, NoSuchAlgorithmException {
        try {
        this.sql_query = this.connection.prepareStatement("INSERT INTO USERS(name, password) VALUES (?, ?)");
        this.sql_query.setString(1, name);
        this.sql_query.setString(2, PasswordEncrypt.encrypt_password(password));

            this.sql_query.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public void deleteProduct(String name) throws SQLException {
        try {
        this.sql_query = this.connection.prepareStatement("DELETE FROM PRODUCTS WHERE name=?");
        this.sql_query.setString(1, name);
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        }
    }

    public void deleteGroup(String name) throws SQLException {
        try {
        this.sql_query = this.connection.prepareStatement("DELETE FROM GROUPS WHERE name=?");
        this.sql_query.setString(1, name);
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        }
    }

    public void updateProduct(int id, String name, double price, int amount, String description, String producer, String groupName) throws SQLException {
        try{
        this.sql_query = this.connection.prepareStatement("UPDATE PRODUCTS SET NAME = ? , "
                + "PRICE = ? , "
                + "AMOUNT = ? , "
                + "GROUP_NAME = ? ,"
                + "DESCRIPTION = ?,"
                + "PRODUCER = ? "
                + "WHERE ID = ?");
            this.sql_query.setString(1, name);
            this.sql_query.setDouble(2, price);
            this.sql_query.setInt(3, amount);
            this.sql_query.setString(4, groupName);
            this.sql_query.setString(5, description);
            this.sql_query.setString(6, producer);
            this.sql_query.setInt(7, id);
            this.sql_query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateGroup(int id, String name, String description) throws SQLException {
        try{
            this.sql_query = this.connection.prepareStatement("UPDATE GROUPS SET NAME = ? , "
                    + "DESCRIPTION = ? "
                    + "WHERE ID = ?");
            this.sql_query.setString(1, name);
            this.sql_query.setString(2, description);
            this.sql_query.setInt(3, id);
            this.sql_query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
