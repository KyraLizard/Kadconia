package de.dhbw.database;

/**
 * Created by Mark on 19.11.13.
 */
public class Info {

    private int id;
    private String name;
    private String image;

    public Info() {
    }

    public Info(String name, String image) {
        setName(name);
        setImage(image);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
