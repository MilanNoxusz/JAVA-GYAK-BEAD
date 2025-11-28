package com.example.gyakbea;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tartalom")
public class Tartalom {

    @Id
    private Long id;

    private Long sutiid; // Egyszerűsítve Long-ként tároljuk a kapcsolatot
    private String mentes;

    public Tartalom() {}

    public Tartalom(Long id, Long sutiid, String mentes) {
        this.id = id;
        this.sutiid = sutiid;
        this.mentes = mentes;
    }

    // Getterek és Setterek
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSutiid() { return sutiid; }
    public void setSutiid(Long sutiid) { this.sutiid = sutiid; }
    public String getMentes() { return mentes; }
    public void setMentes(String mentes) { this.mentes = mentes; }
}