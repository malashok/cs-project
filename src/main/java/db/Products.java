package db;

import java.util.Objects;

public class Products {

        private int id;
        private String name;
        private int amount;

        private String group;
        private double price;

        private String description;

        private String producer;

        public Products(int id, String name, int amount, String group, double price, String description, String producer) {
            this.id = id;
            this.name = name;
            this.amount = amount;
            this.group = group;
            this.price = price;
            this.description = description;
            this.producer = producer;
        }

        public Products(String name, int amount, String group, double price, String description, String producer) {
            this.name = name;
            this.amount = amount;
            this.group = group;
            this.price = price;
            this.description = description;
            this.producer = producer;
        }

        public Products(int id) {
            this.id = id;
        }

        public Products(){}

        public int getId() {
            return id;
        }

        public double getPrice() {
            return price;
        }

        public int getAmount() {
            return amount;
        }

        public String getGroup() {
            return group;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getProducer() {
            return producer;
        }

        @Override
        public String toString() {
            return "Products{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", amount=" + amount +
                    ", group=" + group +
                    ", price=" + price +
                    ", description='" + description + '\'' +
                    ", producer='" + producer + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Products Products = (Products) o;
            return amount == Products.amount && group == Products.group && Double.compare(Products.price, price) == 0 && name.equals(Products.name) && Objects.equals(description, Products.description) && Objects.equals(producer, Products.producer);
        }
}
