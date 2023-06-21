package db;

import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getname() {
        return this.name;
    }
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return (Objects.equals(this.name, this.getname())) && (Objects.equals(this.password, this.getPassword()));
    }

}

