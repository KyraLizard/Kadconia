package de.dhbw.database;

/**
 * Created by Mark on 21.11.13.
 */
public class Server {

    private int id;
    private String name;
    private String owner;
    private String ip;
    private int port;

    private boolean online;

    public Server() {
    }

    public Server(String name, String owner, String ip, int port) {

        setName(name);
        setOwner(owner);
        setIp(ip);
        setPort(port);
        setOnline(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public String getFormattedOwner () {
        return Character.toUpperCase(getOwner().charAt(0)) + getOwner().substring(1);
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
