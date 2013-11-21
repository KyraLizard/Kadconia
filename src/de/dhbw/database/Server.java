package de.dhbw.database;

/**
 * Created by Mark on 21.11.13.
 */
public class Server {

    private int id;
    private String name;
    private String ip;
    private int port;

    public Server() {
    }

    public Server(String name, String ip, int port) {

        this.name = name;
        this.ip = ip;
        this.port = port;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
