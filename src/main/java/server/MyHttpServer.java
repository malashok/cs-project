package server;

import com.sun.net.httpserver.*;

import db.Database;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.GroupService;
import service.ProductsService;
import service.UserService;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;

public class MyHttpServer {

    public static HttpsServer server;
    public static HttpServer serverHttp;
    static Database db = new Database();
    static ProductsService serviceProd = new ProductsService(db);
    static GroupService serviceGroup = new GroupService(db);
    static UserService serviceUser = new UserService(db);
    public static final int PORT = 5001;

    public static void main(String[] args) throws Exception {
        start_http();
    }

    public static void start_http() throws Exception {
        serverHttp = HttpServer.create();
        serverHttp.bind(new InetSocketAddress(PORT), 0);
        serverHttp.createContext("/", new Handler());
        serverHttp.setExecutor(Executors.newFixedThreadPool(8));
        serverHttp.start();
        System.out.println("Sever started on port: "+ PORT);
    }
    
    static class Handler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream in;
            JSONObject products;
            JSONObject group;
            JSONParser parser = new JSONParser();
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if (method.equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PATCH, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
            }

            System.out.println(path + " " + method);
            if (path.startsWith("/api/goods")) {
                String[] splittedPathArr = path.split("/");
                System.out.println(Arrays.toString(splittedPathArr));
                int productsId = -1;
                if (splittedPathArr.length > 3) {
                    try {
                        productsId = Integer.parseInt(splittedPathArr[3]);
                    } catch (NumberFormatException e) {
                        sendResponse("404: Resource not found", 404, exchange);
                        return;
                    }
                }
                try {
                    switch (method) {
                        case "POST" -> {
                            in = exchange.getRequestBody();
                            products = (JSONObject) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
                            System.out.println("products" + products);

                            if (!validateProducts(products, method)) {
                                System.out.println("invalid");
                                sendResponse("409: Conflict", 409, exchange);
                                return;
                            }
                            serviceProd.createProduct(products);
                            sendResponse("204: Product was created", 204, exchange);

                        }
                        case "GET" -> {
                            if (productsId != -1) {
                                sendResponse(serviceProd.getProductById(productsId).toJSONString(), 200, exchange);
                            } else {
                                sendResponse(serviceProd.getAll().toJSONString(), 200, exchange);
                            }
                        }
                        case "PATCH" -> {
                            if (productsId == -1) {
                                sendResponse("400: Bad request", 400, exchange);
                            }
                            System.out.println(exchange.getRequestBody());
                            in = exchange.getRequestBody();
                            products = (JSONObject) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
                            serviceProd.updateProduct(productsId, products);
                            sendResponse("204: Product was updated", 204, exchange);
                        }
                        case "DELETE" -> {
                            if (productsId == -1) {
                                sendResponse("400: Bad request", 400, exchange);
                            }
                            serviceProd.deleteProduct(productsId);
                            sendResponse("204: Product was deleted", 204, exchange);
                        }
                        default -> {}
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }

            if (path.startsWith("/reduce/amount") && Objects.equals("PATCH", method)) {
                String[] splittedPathArr = path.split("/");
                int productsId = -1;
                int reduceAmont = -1;
                if (splittedPathArr.length > 3) {
                    try {
                        productsId = Integer.parseInt(splittedPathArr[3]);
                        reduceAmont = Integer.parseInt(splittedPathArr[4]);
                    } catch (NumberFormatException e) {
                        sendResponse("404: Resource not found", 404, exchange);
                        return;
                    }
                }
                if (productsId == -1) {
                    sendResponse("400: Bad request", 400, exchange);
                }
                try {
                    serviceProd.reduceAmountOfProducts(reduceAmont, productsId);
                } catch (SQLException e) {
                    throw new RuntimeException("Error reduce " + e);
                }
                sendResponse("204: Object was updated ", 204, exchange);
            }

            if (path.startsWith("/add/amount") && Objects.equals("PATCH", method)) {
                String[] splittedPathArr = path.split("/");
                int productsId = -1;
                int reduceAmont = -1;
                if (splittedPathArr.length > 3) {
                    try {
                        productsId = Integer.parseInt(splittedPathArr[3]);
                        reduceAmont = Integer.parseInt(splittedPathArr[4]);
                    } catch (NumberFormatException e) {
                        sendResponse("404: Resource not found", 404, exchange);
                        return;
                    }
                }
                if (productsId == -1) {
                    sendResponse("400: Bad request", 400, exchange);
                }
                try {
                    serviceProd.addAmountOfProducts(reduceAmont, productsId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                sendResponse("204: object was updated ", 204, exchange);
            }
            if (path.startsWith("/login") && Objects.equals("POST", method)) {
                try {
                    in = exchange.getRequestBody();
                    JSONObject inputUser = (JSONObject) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
                    boolean verified = serviceUser.login(inputUser);

                    if (verified) {
                        String jwt = JWT.createJwt((String) inputUser.get("name"));
                        exchange.getResponseHeaders().add("Authorization", "Bearer " + jwt);
                        JSONObject jwtJson = new JSONObject();
                        jwtJson.put("name", JWT.takeNameFromJwt(jwt));
                        sendResponse(jwtJson.toJSONString(), 200, exchange);
                    } else {
                        sendResponse("401: Authentication failed", 401, exchange);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
            if (path.startsWith("/api/group")) {
                String[] splittedPathArr = path.split("/");
                int groupName = -1;
                if (splittedPathArr.length > 3) {
                    try {
                        groupName = Integer.parseInt(String.valueOf(splittedPathArr[3]));
                    } catch (NumberFormatException e) {
                        sendResponse("404: Resource not found", 404, exchange);
                        return;
                    }
                }
                try {
                    switch (method) {
                        case "GET" -> {
                            if (groupName != -1) {
                                sendResponse(serviceGroup.getGroupById(groupName).toJSONString(), 200, exchange);
                            } else {
                                sendResponse(serviceGroup.getAllGroups().toJSONString(), 200, exchange);
                            }
                        }
                        case "POST" -> {
                            in = exchange.getRequestBody();
                            group = (JSONObject) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
                            serviceGroup.createGroup(group);
                            sendResponse("Group was added", 201, exchange);
                        }
                        case "PATCH" -> {
                            if (groupName == -1) {
                                sendResponse("400: Bad request", 400, exchange);
                            }
                            in = exchange.getRequestBody();
                            group = (JSONObject) parser.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
                            serviceGroup.updateGroup(groupName, group);
                            sendResponse("204: object was updated ", 204, exchange);
                        }
                        case "DELETE" -> {
                            if (groupName == -1) {
                                sendResponse("400: Bad Request", 400, exchange);
                            }
                            serviceGroup.deleteGroup(groupName);
                            sendResponse("204: object was deleted ", 204, exchange);
                        }
                        default -> {}
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
            sendResponse("404: Not Found", 404, exchange);
        }

        public void sendResponse(String message, int status, HttpExchange exchange) throws IOException {
            System.out.println("send response");
            exchange.sendResponseHeaders(status, message.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }

        public boolean validateProducts(JSONObject products, String method) {
            switch (method) {
                case "PATCH" -> {
                    return (
                            products.containsKey("name") &&
                                    products.containsKey("groupName") &&
                                    products.containsKey("amount") &&
                                    products.containsKey("price") &&
                                    products.containsKey("about") &&
                                    products.containsKey("producer") &&
                                    (!products.get("groupName").equals("")) &&
                                    (Integer.parseInt(String.valueOf(products.get("amount"))) >= 0) &&
                                    (Double.parseDouble(String.valueOf(products.get("price"))) > 0) &&
                                    (!products.get("producer").equals(""))
                    );
                }
                case "POST" -> {
                    if (products.containsKey("groupName") && (products.get("groupName").equals(""))) {
                        return false;
                    }
                    if (products.containsKey("amount") && (Integer.parseInt(String.valueOf(products.get("amount"))) <= 0)) {
                        return false;
                    }
                    if (products.containsKey("price") && (Double.parseDouble(String.valueOf(products.get("price"))) <= 0)) {
                        return false;
                    }
                    if (products.containsKey("producer") && (products.get("producer").equals(""))) {
                        return false;
                    }
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
    }


}
