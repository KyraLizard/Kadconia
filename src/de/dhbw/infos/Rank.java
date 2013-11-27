package de.dhbw.infos;

/**
 * Created by Mark on 27.11.13.
 */
public class Rank {

    private String name;
    private String file;

    public Rank(String name, String file) {
        setName(name);
        setFile(file);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
