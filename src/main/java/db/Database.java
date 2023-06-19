package db;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    public Database(){
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
                    "NAME VARCHAR(50) UNIQUE NOT NULL ,"+
                    "DESCRIPTION VARCHAR(100)"
            );
            this.sql_query.execute();

            this.sql_query = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS PRODUCTS(" +
                            "ID SERIAL PRIMARY KEY," +
                            "NAME VARCHAR(50) UNIQUE NOT NULL," +
                            "PRICE NUMERIC(10, 2) NOT NULL," +
                            "AMOUNT INTEGER NOT NULL,"+
                            "DESCRIPTION VARCHAR(100),"+
                            "PRODUCER VARCHAR(50) NOT NULL,"+
                            "FOREIGN KEY (GROUP) REFERENCES GROUP (NAME) ON UPDATE CASCADE ON DELETE CASCADE");
            this.sql_query.execute();

            this.sql_query = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS USERS("+
                    "ID SERIAL PRIMARY KEY,"+
                    "NAME VARCHAR(50) UNIQUE NOT NULL,"+
                    "password VARCHAR(50)"
            );
            this.sql_query.execute();

            this.sql_query.getConnection().setAutoCommit(false);
            this.sql_query.getConnection().commit();
            this.create_user("superuser", "111111");
            this.create_test_data();
        } catch (ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public int create_user(String username, String password) throws SQLException, NoSuchAlgorithmException {
        this.sql_query = this.connection.prepareStatement("INSERT INTO User(username, password) VALUES (?, ?)");
        this.sql_query.setString(1, username);
        this.sql_query.setString(2, PasswordEncryptor.encrypt_password(password));
        int id = -1;
        try {
            this.sql_query.execute();
            this.sql_query.getConnection().commit();
            id = (int) this.sql_query.getGeneratedKeys().getLong(1);
        } catch (SQLException e) {
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
            return id;
        }
    }

    public int create_product(String name, double price, int amount, String groupName, String about, String producer) throws SQLException {
        this.sql_query = this.connection.prepareStatement("INSERT INTO Products(name, price, amount, groupName, about, producer) VALUES (?, ?, ?, ?, ?, ?)");
        this.sql_query.setString(1, name);
        this.sql_query.setDouble(2, price);
        this.sql_query.setInt(3, amount);
        this.sql_query.setString(4, groupName);
        this.sql_query.setString(5, about);
        this.sql_query.setString(6, producer);
        int id = -1;
        try {
            this.sql_query.execute();
            this.sql_query.getConnection().commit();
            id = (int) this.sql_query.getGeneratedKeys().getLong(1);
        } catch (SQLException e) {
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
            return id;
        }
    }

    public int create_product_group(String name, String about) throws SQLException {
        this.sql_query = this.connection.prepareStatement("INSERT INTO Group(name, about) VALUES (?, ?)");
        this.sql_query.setString(1, name);
        this.sql_query.setString(2, about);
        int id = -1;
        try {
            this.sql_query.execute();
            this.sql_query.getConnection().commit();
            id = (int) this.sql_query.getGeneratedKeys().getLong(1);
        } catch (SQLException e) {
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
            return id;
        }
    }

    /*public int get_model_value_by_id(String model_name, String value, int id) {
        try {
            this.sql_query = this.connection.prepareStatement("SELECT " + value + " FROM " + model_name + " WHERE id=?");
            this.sql_query.setInt(1, id);
            this.result_set = this.sql_query.executeQuery();
            this.result_set.next();
            int mod_value = this.result_set.getInt(value);
            this.result_set.close();
            return mod_value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public int get_model_value_by_name(String model_name, String value, String name) {
        try {
            this.sql_query = this.connection.prepareStatement("SELECT " + value + " FROM " + model_name + " WHERE name=?");
            this.sql_query.setString(1, name);
            this.result_set = this.sql_query.executeQuery();
            this.result_set.next();
            int mod_value = this.result_set.getInt(value);
            this.result_set.close();
            return mod_value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*public void add_product_by_id(int amount, int id) throws SQLException {
        int previous_amount = this.get_model_value_by_id("Products", "amount", id);
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET amount=? WHERE id=?");
        this.sql_query.setInt(1, previous_amount + amount);
        this.sql_query.setInt(2, id);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    public void add_product_by_name(int amount, String name) throws SQLException {
        int previous_amount = this.get_model_value_by_name("Products", "amount", name);
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET amount=? WHERE name=?");
        this.sql_query.setInt(1, previous_amount + amount);
        this.sql_query.setString(2, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    /*public void discard_product_by_id(int amount, int id) throws SQLException {
        int previous_amount = this.get_model_value_by_id("Products", "amount", id);
        if (amount > previous_amount) {
            throw new RuntimeException("You try to discard more than there is available!");
        }
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET amount=? WHERE id=?");
        this.sql_query.setInt(1, previous_amount - amount);
        this.sql_query.setInt(2, id);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    public void discard_product_by_name(int amount, String name) throws SQLException {
        int previous_amount = this.get_model_value_by_name("Products", "amount", name);
        if (amount > previous_amount) {
            throw new RuntimeException("You try to discard more than there is available!");
        }
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET amount=? WHERE name=?");
        this.sql_query.setInt(1, previous_amount - amount);
        this.sql_query.setString(2, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    /*public void set_product_price_by_id(double new_price, int id) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET price=? WHERE id=?");
        this.sql_query.setDouble(1, new_price);
        this.sql_query.setInt(2, id);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    public void set_product_price_by_name(double new_price, String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET price=? WHERE name=?");
        this.sql_query.setDouble(1, new_price);
        this.sql_query.setString(2, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    /*public void set_product_name_by_id(String new_name, int id) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET name=? WHERE id=?");
        this.sql_query.setString(1, new_name);
        this.sql_query.setInt(2, id);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    /*public void set_product_name_by_name(String new_name, String old_name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET name=? WHERE name=?");
        this.sql_query.setString(1, new_name);
        this.sql_query.setString(2, old_name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    /*public void set_product_group_by_id(int groupId, int ProductsId) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET groupId=? WHERE id=?");
        this.sql_query.setInt(1, groupId);
        this.sql_query.setInt(2, ProductsId);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    public void set_product_group_by_name(String groupName, String productName) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET groupName=? WHERE name=?");
        this.sql_query.setString(1, groupName);
        this.sql_query.setString(2, productName);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public void set_product_about_by_name(String new_about, String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET about=? WHERE name=?");
        this.sql_query.setString(1, new_about);
        this.sql_query.setString(2, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public void set_product_producer_by_name(String new_producer, String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE Products SET producer=? WHERE name=?");
        this.sql_query.setString(1, new_producer);
        this.sql_query.setString(2, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    /*public void delete_product_by_id(int id) throws SQLException {
        this.sql_query = this.connection.prepareStatement("DELETE FROM Products WHERE id=?");
        this.sql_query.setInt(1, id);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    public void delete_product_by_name(String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("DELETE FROM Products WHERE name=?");
        this.sql_query.setString(1, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public void delete_group_by_name(String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("DELETE FROM Group WHERE name=?");
        this.sql_query.setString(1, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public void delete_group_Products_by_name(String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("DELETE FROM Products WHERE groupName=?");
        this.sql_query.setString(1, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public List<Products> get_products_ordered_by(String field, String sorting) {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Products ORDER BY " + field + " " + sorting);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Products product = new Products(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getInt("amount"),
                        this.result_set.getString("groupName"),
                        this.result_set.getDouble("price"),
                        this.result_set.getString("about"),
                        this.result_set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.result_set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Products> get_products_where(String field, int value, String sign) {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Products WHERE " + field + sign + "?");
            this.sql_query.setInt(1, value);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Products product = new Products(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getInt("amount"),
                        this.result_set.getString("groupName"),
                        this.result_set.getDouble("price"),
                        this.result_set.getString("about"),
                        this.result_set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.result_set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Products> get_products_where(String field, String value, String sign) {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Products WHERE " + field + sign + "?");
            this.sql_query.setString(1, value);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Products product = new Products(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getInt("amount"),
                        this.result_set.getString("groupName"),
                        this.result_set.getDouble("price"),
                        this.result_set.getString("about"),
                        this.result_set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.result_set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Products> get_products_where(String field, int value) {
        return this.get_products_where(field, value, "=");
    }

    public List<Products> get_products_where(String field, String value) {
        return this.get_products_where(field, value, "=");
    }

    public List<Products> get_products_between(String field, int first_value, int second_value) {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Products WHERE " + field + " BETWEEN ? AND ?");
            this.sql_query.setInt(1, first_value);
            this.sql_query.setInt(2, second_value);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Products product = new Products(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getInt("amount"),
                        this.result_set.getString("groupName"),
                        this.result_set.getDouble("price"),
                        this.result_set.getString("about"),
                        this.result_set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.result_set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Products> get_products_between(String field, String first_value, String second_value) {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Products WHERE " + field + " BETWEEN ? AND ?");
            this.sql_query.setString(1, first_value);
            this.sql_query.setString(2, second_value);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Products product = new Products(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getInt("amount"),
                        this.result_set.getString("groupName"),
                        this.result_set.getDouble("price"),
                        this.result_set.getString("about"),
                        this.result_set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.result_set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Products> get_products() {
        try {
            ArrayList<Products> products = new ArrayList<Products>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Products");
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Products product = new Products(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getInt("amount"),
                        this.result_set.getString("groupName"),
                        this.result_set.getDouble("price"),
                        this.result_set.getString("about"),
                        this.result_set.getString("producer")
                );
                products.add(product);
                products.ensureCapacity(products.size() + 1);
            }
            this.result_set.close();
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*public void set_group_name_by_id(String new_name, int id) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE ProductsGroup SET name=? WHERE id=?");
        this.sql_query.setString(1, new_name);
        this.sql_query.setInt(2, id);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }*/

    public void set_group_about_by_name(String new_about, String name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("UPDATE ProductsGroup SET about=? WHERE name=?");
        this.sql_query.setString(1, new_about);
        this.sql_query.setString(2, name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public List<Group> get_groups() {
        try {
            ArrayList<Group> groups = new ArrayList<Group>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Group");
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Group group = new Group(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getString("about")
                );
                groups.add(group);
                groups.ensureCapacity(groups.size() + 1);
            }
            this.result_set.close();
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Group> get_groups_where(String field, String value, String sign) {
        try {
            ArrayList<Group> groups = new ArrayList<Group>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Group WHERE " + field + sign + "?");
            this.sql_query.setString(1, value);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Group group = new Group(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getString("about")
                );
                groups.add(group);
                groups.ensureCapacity(groups.size() + 1);
            }
            this.result_set.close();
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Group> get_groups_where(String field, int value, String sign) {
        try {
            ArrayList<Group> groups = new ArrayList<Group>();
            this.sql_query = this.connection.prepareStatement("SELECT * FROM Group WHERE " + field + sign + "?");
            this.sql_query.setInt(1, value);
            this.result_set = this.sql_query.executeQuery();
            while (this.result_set.next()) {
                Group group = new Group(
                        this.result_set.getInt("id"),
                        this.result_set.getString("name"),
                        this.result_set.getString("about")
                );
                groups.add(group);
                groups.ensureCapacity(groups.size() + 1);
            }
            this.result_set.close();
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Group> get_groups_where(String field, int value) {
        return this.get_groups_where(field, value, "=");
    }

    public List<Group> get_groups_where(String field, String value) {
        return this.get_groups_where(field, value, "=");
    }

    public User get_user_by_username(String username) throws SQLException {
        try {
            this.sql_query = this.connection.prepareStatement("SELECT * FROM User WHERE username=?");
            this.sql_query.setString(1, username);
            this.result_set = this.sql_query.executeQuery();
            boolean valid = this.result_set.next();
            if (valid == false) {
                return null;
            }
            User user = new User(
                    this.result_set.getInt("id"),
                    this.result_set.getString("username"),
                    this.result_set.getString("password")
            );
            this.result_set.close();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User get_user_by_id(int id) throws SQLException {
        try {
            this.sql_query = this.connection.prepareStatement("SELECT * FROM User WHERE id=?");
            this.sql_query.setInt(1, id);
            this.result_set = this.sql_query.executeQuery();
            boolean valid = this.result_set.next();
            if (valid == false) {
                return null;
            }
            User user = new User(
                    this.result_set.getInt("id"),
                    this.result_set.getString("username"),
                    this.result_set.getString("password")
            );
            this.result_set.close();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void drop_table(String table_name) throws SQLException {
        this.sql_query = this.connection.prepareStatement("DROP TABLE IF EXISTS " + table_name);
        try {
            this.sql_query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sql_query.getConnection().rollback();
        } finally {
            this.sql_query.getConnection().commit();
        }
    }

    public void end_work() throws SQLException {
        this.drop_table("Products");
        this.drop_table("Group");
        try { this.sql_query.close(); } catch (NullPointerException e) {}
        try { this.result_set.close(); } catch (NullPointerException e) {}
    }

    private void create_test_data() throws SQLException {
        this.create_product_group("Food", "Food test1");
        this.create_product_group("Clothes", "Clothes test1");
        this.create_product_group("Technics", "Technics test1");
        this.create_product("Berries", 50, 5, "Food", "Berries test1", "Berry-land");
        this.create_product("Jeans", 100, 10, "Clothes", "Jeans test1", "Collins");
        this.create_product("Macbook", 200, 25, "Technics", "Macbook test1", "Apple");
        this.create_product("Lenovo", 170, 50, "Technics", "Lenovo test1", "Lenovo");
    }

}
