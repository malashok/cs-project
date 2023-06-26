package service;

import db.Products;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import db.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductsService {
    public Database db;

    public ProductsService(Database db) {
        this.db = db;
    }

    public JSONObject getProductById(int id) {
        Products products = db.getProductById(id);
        JSONObject productJson = new JSONObject();
        productJson.put("name", products.getName());
        productJson.put("group_name", products.getGroup());
        productJson.put("amount", products.getAmount());
        productJson.put("price", products.getPrice());
        productJson.put("description", products.getDescription());
        productJson.put("producer", products.getProducer());
        productJson.put("id", products.getId());
        return productJson;
    }

    public JSONObject getAll() {
        ArrayList<Products> products = db.getAllProducts();
        JSONArray products_array = new JSONArray();
        JSONObject productJson = new JSONObject();
        int i = 0;
        while(!products.isEmpty()){
            JSONObject good_json = new JSONObject();
            good_json.put("group_name", products.get(i).getGroup());
            good_json.put("name", products.get(i).getName());
            good_json.put("amount", products.get(i).getAmount());
            good_json.put("price", products.get(i).getPrice());
            good_json.put("description", products.get(i).getDescription());
            good_json.put("producer", products.get(i).getProducer());
            good_json.put("id", products.get(i).getId());
            products.remove(i);
            products_array.add(good_json);
        }
        productJson.put("result",products_array);
        return productJson;
    }

    public void createProduct(JSONObject productJson) throws SQLException {
        System.out.println("creating " + productJson.toJSONString());

        try {
            db.createProduct(
                    (String) productJson.get("name"),
                    Double.parseDouble(productJson.get("price").toString()),
                    Integer.parseInt(productJson.get("amount").toString()),
                    (String) productJson.get("description"),
                    (String) productJson.get("producer"),
                    (String) productJson.get("group_name")
            );
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateProduct(int id, JSONObject productJson) throws SQLException {
        db.updateProduct(id, (String) productJson.get("name"),
                Double.parseDouble(productJson.get("price").toString()),
                Integer.parseInt(productJson.get("amount").toString()),
                (String) productJson.get("description"),
                (String) productJson.get("producer"), (String) productJson.get("group_name"));
    }

    public void deleteProduct(int product) throws SQLException {
        db.deleteProduct(product);
    }

    public void reduceAmountOfProducts(int amount, int id) throws SQLException {
        db.reduceAmountOfProducts(amount, id);
    }

    public void addAmountOfProducts(int amount, int id) throws SQLException {
        db.addAmountOfProducts(amount, id);
    }
}
