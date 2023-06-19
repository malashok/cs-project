package db;

import java.util.Objects;

public class Group {
    private int id;

    private String name;

    private String about;

    public Group(int id) {
        this.id = id;
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(int id, String name, String about) {
        this.id = id;
        this.name = name;
        this.about = about;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbout() {
        return about;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", about='" + about + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && name.equals(group.name) && Objects.equals(about, group.about);
    }
}
