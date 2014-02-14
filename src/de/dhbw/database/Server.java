package de.dhbw.database;

/**
 * Created by Mark on 21.11.13.
 */
public class Server {

    private int id;
    private String name;
    private String owner;
    private String domain;
    private int port;
    private String serverInformation;

    private boolean online;

    public Server() {

        setOnline(false);
    }

    public Server(String name, String owner, String domain, int port) {

        setName(name);
        setOwner(owner);
        setDomain(domain);
        setPort(port);
        setOnline(false);
    }

    public Server(String name, String owner) {

        setName(name);
        setOwner(owner);
        setDomain("dummy.domain");
        setPort(-1);
        setOnline(false);
    }

    public Server(String name) {

        setName(name);
        setOwner("Dummy Owner");
        setDomain("dummy.domain");
        setPort(-1);
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

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public String getServerInformation() {
        return serverInformation;
    }

    public void setServerInformation(String serverInformation) {
        this.serverInformation = serverInformation;
    }
}
