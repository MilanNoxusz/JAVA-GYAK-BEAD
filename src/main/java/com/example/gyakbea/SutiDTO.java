package com.example.gyakbea;

public class SutiDTO {
    private Long id;
    private String nev;
    private String tipus;
    private String arSzoveg; // Pl: "1200 Ft / db"
    private String mentesInfo; // Pl: "G, L" vagy üres
    private boolean dijazott; // Ez alapján döntjük el, hogy kap-e csillagot

    public SutiDTO(Long id, String nev, String tipus, String arSzoveg, String mentesInfo, boolean dijazott) {
        this.id = id;
        this.nev = nev;
        this.tipus = tipus;
        this.arSzoveg = arSzoveg;
        this.mentesInfo = mentesInfo;
        this.dijazott = dijazott;
    }

    // Getterek
    public Long getId() { return id; }
    public String getNev() { return nev; }
    public String getTipus() { return tipus; }
    public String getArSzoveg() { return arSzoveg; }
    public String getMentesInfo() { return mentesInfo; }
    public boolean isDijazott() { return dijazott; }
}