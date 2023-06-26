package db;

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

    private ResultSet set;
    private Connection connection;
    private PreparedStatement statement;

    public void initialization() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "root", "root");
            this.statement = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS GROUPS (" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL ," +
                            "DESCRIPTION VARCHAR(100))"
            );
            this.statement.execute();

            this.statement = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS PRODUCTS(" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL," +
                            "PRICE NUMERIC(10, 2) NOT NULL," +
                            "AMOUNT INTEGER NOT NULL," +
                            "DESCRIPTION VARCHAR(100)," +
                            "PRODUCER VARCHAR(50) NOT NULL," +
                            "GROUP_NAME VARCHAR(50) NOT NULL,"+
                            "FOREIGN KEY (GROUP_NAME) REFERENCES GROUPS (NAME) ON UPDATE CASCADE ON DELETE CASCADE)");
            this.statement.execute();

            this.statement = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS USERS(" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL," +
                            "PASSWORD VARCHAR(50))"
            );
            this.statement.execute();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void createGroup(String name, String description) throws SQLException {
        try {
        this.statement = this.connection.prepareStatement("INSERT INTO GROUPS(name, description) VALUES (?, ?)");
        this.statement.setString(1, name);
        this.statement.setString(2, description);
            this.statement.execute();

        } catch (SQLException e) {
           System.out.println(new SQLException(e));
        }
    }

    public void createProduct(String name, double price, int amount, String description, String producer, String groupName) throws SQLException {
        try {
            this.statement = this.connection.prepareStatement("INSERT INTO PRODUCTS(name, price, amount, group_name, description, producer) VALUES (?, ?, ?, ?, ?, ?)");
        this.statement.setString(1, name);
        this.statement.setDouble(2, price);
        this.statement.setInt(3, amount);
        this.statement.setString(4, groupName);
        this.statement.setString(5, description);
        this.statement.setString(6, producer);

            this.statement.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
    }
    }

    public void createUser(String name, String password) throws SQLException, NoSuchAlgorithmException {
        try {
        this.statement = this.connection.prepareStatement("INSERT INTO USERS(name, password) VALUES (?, ?)");
        this.statement.setString(1, name);
        this.statement.setString(2, PasswordEncryptor.encrypt(password));

            this.statement.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public void deleteProduct(int id) throws SQLException {
        try {
        this.statement = this.connection.prepareStatement("DELETE FROM PRODUCTS WHERE id=?");
        this.statement.setInt(1, id);
            this.statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.statement.getConnection().rollback();
        }
    }

    public void deleteGroup(int id) throws SQLException {
        try {
        this.statement = this.connection.prepareStatement("DELETE FROM GROUPS WHERE id=?");
        this.statement.setInt(1, id);
            this.statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.statement.getConnection().rollback();
        }
    }

    public void updateProduct(int id, String name, double price, int amount, String description, String producer, String groupName) throws SQLException {
        try{
            System.out.println(description);
        this.statement = this.connection.prepareStatement("UPDATE PRODUCTS SET NAME = ? , "
                + "PRICE = ? , "
                + "AMOUNT = ? , "
                + "GROUP_NAME = ? ,"
                + "DESCRIPTION = ?,"
                + "PRODUCER = ? "
                + "WHERE ID = ?");

            this.statement.setString(1, name);
            this.statement.setDouble(2, price);
            this.statement.setInt(3, Integer.parseInt("10"));
            this.statement.setString(4, groupName);
            this.statement.setString(5, description);
            this.statement.setString(6, producer);
            this.statement.setInt(7, id);
            this.statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateGroup(int id, String name, String description) throws SQLException {
        try{
            this.statement = this.connection.prepareStatement("UPDATE GROUPS SET NAME = ? , "
                    + "DESCRIPTION = ? "
                    + "WHERE ID = ?");
            this.statement.setString(1, name);
            this.statement.setString(2, description);
            this.statement.setInt(3, id);
            this.statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Products> getAllProducts() {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.statement = this.connection.prepareStatement("SELECT * FROM PRODUCTS");
            this.set = this.statement.executeQuery();
            while (this.set.next()) {
                Products product = new Products(
                        this.set.getInt("id"),
                        this.set.getString("name"),
                        this.set.getInt("amount"),
                        this.set.getString("group_name"),
                        this.set.getDouble("price"),
                        this.set.getString("description"),
                        this.set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Group> getAllGroups() {
        try {
            ArrayList<Group> groups = new ArrayList<Group>();
            this.statement = this.connection.prepareStatement("SELECT * FROM GROUPS");
            this.set = this.statement.executeQuery();
            while (this.set.next()) {
                Group group = new Group(
                        this.set.getInt("id"),
                        this.set.getString("name"),
                        this.set.getString("description")
                );
                groups.add(group);
                groups.ensureCapacity(groups.size() + 1);
            }
            this.set.close();
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Products getProductById(int id) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM PRODUCTS WHERE id=?");
            this.statement.setInt(1, id);
            this.set = this.statement.executeQuery();
            Products product = null;
            while(this.set.next()){
                product = new Products(this.set.getInt("id"),
                        this.set.getString("name"),
                        this.set.getInt("amount"),
                        this.set.getString("group_name"),
                        this.set.getDouble("price"),
                        this.set.getString("description"),
                        this.set.getString("producer"));
            }
            this.set.close();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Group getGroupById(int id) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM GROUPS WHERE id=?");
            this.statement.setInt(1, id);
            this.set = this.statement.executeQuery();
            Group group = null;
            while(this.set.next()){
                group = new Group(this.set.getInt("id"),
                        this.set.getString("name"),
                        this.set.getString("description"));
            }
            this.set.close();
            return group;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void reduceAmountOfProducts(int amount, int id) throws SQLException {
        try {
        int prevAmount = this.getProductById(id).getAmount();
        if (amount > prevAmount) {
            throw new RuntimeException("Error when reduce products!");
        }
        this.statement = this.connection.prepareStatement("UPDATE PRODUCTS SET amount=? WHERE id=?");
        this.statement.setInt(1, prevAmount - amount);
        this.statement.setInt(2, id);
            this.statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.statement.getConnection().rollback();
        }
        }

    public void addAmountOfProducts(int amount, int id) throws SQLException {
        try {
        int prevAmount =  this.getProductById(id).getAmount();
        this.statement = this.connection.prepareStatement("UPDATE PRODUCTS SET amount=? WHERE id=?");
        this.statement.setInt(1, prevAmount + amount);
        this.statement.setInt(2, id);
            this.statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Products> filter(Filter fp) {
        ArrayList<Products> res = new ArrayList<>();
        String str = "SELECT * FROM PRODUCTS";
        if (!fp.isEmpty()) {
            str += " WHERE ";
            if (fp.getCategory() != null) {
                str += "GROUP_NAME = \'" + fp.getCategory() + "\'" + " AND ";
            }
            if (fp.getToPrice() != null) {
                str += "PRICE <= " + fp.getToPrice() + " AND ";
            }
            if (fp.getFromPrice() != null) {
                str += "PRICE >= " + fp.getFromPrice();
            }
            str = str.replaceAll("\\s+AND\\s*$", "");
        }
        try (PreparedStatement s = connection.prepareStatement(str)) {
            set = s.executeQuery();
            Products next;
            while (set.next()) {
                next = new Products(this.set.getInt("id"),
                        this.set.getString("name"),
                        this.set.getInt("amount"),
                        this.set.getString("group_name"),
                        this.set.getDouble("price"),
                        this.set.getString("description"),
                        this.set.getString("producer"));
                res.add(next);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public User getUserByName(String name) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM USERS WHERE NAME=?");
            this.statement.setString(1, name);
            this.set = this.statement.executeQuery();
            boolean valid = this.set.next();
            if (valid == false) { return null; }
            User user = new User(
                    this.set.getInt("id"),
                    this.set.getString("name"),
                    this.set.getString("password")
            );
            this.set.close();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
