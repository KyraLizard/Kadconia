package de.dhbw.database;

/**
 * Created by Mark on 05.12.13.
 */
public class Kontoeintrag {

    private int id;
    private long date;
    private String userKontoName;
    private double betrag;
    private String partnerKontoName;
    private String type;
    private String item;
    private String server;
    private double newSaldo;

    public Kontoeintrag() {
    }

    public Kontoeintrag(int id, long date, String userKontoName, double betrag, String partnerKontoName, String type, String item, String server, double newSaldo) {
        this.id = id;
        this.date = date;
        this.userKontoName = userKontoName;
        this.betrag = betrag;
        this.partnerKontoName = partnerKontoName;
        this.type = type;
        this.newSaldo = newSaldo;
        this.server = server;
        this.item = item;
    }

    public Kontoeintrag(long date, String userKontoName, double betrag, String partnerKontoName, String type, String item, String server, double newSaldo) {
        this.date = date;
        this.userKontoName = userKontoName;
        this.betrag = betrag;
        this.partnerKontoName = partnerKontoName;
        this.type = type;
        this.newSaldo = newSaldo;
        this.server = server;
        this.item = item;
    }

    public String toString() {
        String string = "";
        string += "Id: " + id + ", ";
        string += "Date: " + date + ", ";
        string += "UserKonto: " + userKontoName + ", ";
        string += "Betrag: " + betrag + ", ";
        string += "PartnerKonto: " + partnerKontoName + ", ";
        string += "Typ: " + type + ", ";
        string += "Item: " + item + ", ";
        string += "Server: " + server + ", ";
        string += "NewSaldo: " + newSaldo;

        return string;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns date of transaction in milliseconds
     * @return date of transaction in milliseconds
     */
    public long getDate() {
        return date;
    }

    /**
     * Save date of transaction in milliseconds
     * @param date date of transaction in milliseconds
     */
    public void setDate(long date) {
        this.date = date;
    }

    public String getUserKontoName() {
        return userKontoName;
    }

    public void setUserKontoName(String userKontoName) {
        this.userKontoName = userKontoName;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public String getPartnerKontoName() {
        return partnerKontoName;
    }

    public void setPartnerKontoName(String partnerKontoName) {
        this.partnerKontoName = partnerKontoName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getNewSaldo() {
        return newSaldo;
    }

    public void setNewSaldo(double newSaldo) {
        this.newSaldo = newSaldo;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
