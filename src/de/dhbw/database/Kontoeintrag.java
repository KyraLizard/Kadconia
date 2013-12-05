package de.dhbw.database;

import java.util.Date;

/**
 * Created by Mark on 05.12.13.
 */
public class Kontoeintrag {

    private int id;
    private int date;
    private String userKontoName;
    private float betrag;
    private String partnerKontoName;
    private String type;
    private float newSaldo;

    public Kontoeintrag() {
    }

    public Kontoeintrag(int id, int date, String userKontoName, float betrag, String partnerKontoName, String type, float newSaldo) {
        this.id = id;
        this.date = date;
        this.userKontoName = userKontoName;
        this.betrag = betrag;
        this.partnerKontoName = partnerKontoName;
        this.type = type;
        this.newSaldo = newSaldo;
    }

    public Kontoeintrag(int date, String userKontoName, float betrag, String partnerKontoName, String type, float newSaldo) {
        this.date = date;
        this.userKontoName = userKontoName;
        this.betrag = betrag;
        this.partnerKontoName = partnerKontoName;
        this.type = type;
        this.newSaldo = newSaldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getUserKontoName() {
        return userKontoName;
    }

    public void setUserKontoName(String userKontoName) {
        this.userKontoName = userKontoName;
    }

    public float getBetrag() {
        return betrag;
    }

    public void setBetrag(float betrag) {
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

    public float getNewSaldo() {
        return newSaldo;
    }

    public void setNewSaldo(float newSaldo) {
        this.newSaldo = newSaldo;
    }
}
