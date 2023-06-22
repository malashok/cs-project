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

    public JSONObject getProductByName(String name) {
        Products products = db.getProductByName(name);
        JSONObject productJson = new JSONObject();
        productJson.put("group_name", products.getGroup());
        productJson.put("name", name);
        productJson.put("amount", products.getAmount());
        productJson.put("price", products.getPrice());
        productJson.put("description", products.getDescription());
        productJson.put("producer", products.getProducer());
        productJson.put("id", products.getId());
        return productJson;
    }

    public JSONObject getProductById(int name) {
        Products products = db.getProductById(name);
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

    public JSONObject getAll(){
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
                    (Double) productJson.get("price"),
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
        //Products products = db.getProductById(id).get(0);
        db.updateProduct(id, (String) productJson.get("name"), (Double) productJson.get("price"), Integer.parseInt(productJson.get("amount").toString()), (String) productJson.get("description"), (String) productJson.get("producer"), (String) productJson.get("group_name"));
//        if (productJson.containsKey("amount")) {
//            long amount = (long) productJson.get("amount");
//            if (products.getAmount() > amount) {
//                db.updateProduct(productJson.get("id"),  (int) (products.getAmount() - amount), name);
//            } else {
//                db.add_product_by_name((int) (amount - products.getAmount()), name);
//            }
//        }
//        if (productJson.containsKey("price")) {
//            double price = Double.parseDouble(String.valueOf(productJson.get("price")));
//            if (products.getPrice() != price) {
//                db.set_product_price_by_name(price, name);
//            }
//        }
//        if (productJson.containsKey("group_name")) {
//            String group_name = (String) productJson.get("group_name");
//            if (products.getGroup() != group_name) {
//                db.set_product_group_by_name(group_name, name);
//            }
//        }
//        if (productJson.containsKey("description")) {
//            String description = (String) productJson.get("description");
//            if (products.getDescription() != description) {
//                db.set_product_description_by_name(description, name);
//            }
//        }
//        if (productJson.containsKey("producer")) {
//            String producer = (String) productJson.get("producer");
//            if (products.getProducer() != producer) {
//                db.set_product_producer_by_name(producer, name);
//            }
//        }
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
