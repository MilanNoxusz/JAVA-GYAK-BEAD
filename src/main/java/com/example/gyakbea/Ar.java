package com.example.gyakbea;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ar")
public class Ar {

    @Id
    private Long id;

    private Long sutiid;
    private Integer ertek;
    private String egyseg;

    public Ar() {}

    public Ar(Long id, Long sutiid, Integer ertek, String egyseg) {
        this.id = id;
        this.sutiid = sutiid;
        this.ertek = ertek;
        this.egyseg = egyseg;
    }

    // Getterek és Setterek
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSutiid() { return sutiid; }
    public void setSutiid(Long sutiid) { this.sutiid = sutiid; }
    public Integer getErtek() { return ertek; }
    public void setErtek(Integer ertek) { this.ertek = ertek; }
    public String getEgyseg() { return egyseg; }
    public void setEgyseg(String egyseg) { this.egyseg = egyseg; }
}