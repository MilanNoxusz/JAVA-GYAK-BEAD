package com.example.gyakbea;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "suti")
public class Suti {

    @Id
    private Long id; // Nem generáljuk, mert a txt fájlban fix ID-k vannak

    private String nev;
    private String tipus;
    private Boolean dijazott;

    public Suti() {}

    public Suti(Long id, String nev, String tipus, Boolean dijazott) {
        this.id = id;
        this.nev = nev;
        this.tipus = tipus;
        this.dijazott = dijazott;
    }

    // Getterek és Setterek
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNev() { return nev; }
    public void setNev(String nev) { this.nev = nev; }
    public String getTipus() { return tipus; }
    public void setTipus(String tipus) { this.tipus = tipus; }
    public Boolean getDijazott() { return dijazott; }
    public void setDijazott(Boolean dijazott) { this.dijazott = dijazott; }
}