package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DatabaseController {

    @Autowired
    private SutiRepository sutiRepo;
    @Autowired
    private ArRepository arRepo;
    @Autowired
    private TartalomRepository tartalomRepo;

    @GetMapping("/adatbazis")
    public String adatbazisPage(Model model) {
        List<SutiDTO> kartyak = new ArrayList<>();

        // Lekérjük az összes sütit
        List<Suti> sutik = sutiRepo.findAll();

        // VÉLETLENSZERŰSORREND: Megkeverjük a listát minden frissítésnél
        Collections.shuffle(sutik);

        int limit = 0;
        for (Suti s : sutik) {
            if (limit >= 8) break; // Csak 8 db-ot veszünk a megkevert listából

            // Ár keresése
            List<Ar> arak = arRepo.findBySutiid(s.getId());
            String arStr = "Nincs ár";
            if (!arak.isEmpty()) {
                Ar ar = arak.get(0);
                arStr = ar.getErtek() + " Ft / " + ar.getEgyseg();
            }

            // Mentesség keresése
            List<Tartalom> tartalmak = tartalomRepo.findBySutiid(s.getId());
            String mentesStr = "";
            if (!tartalmak.isEmpty()) {
                mentesStr = tartalmak.stream()
                        .map(Tartalom::getMentes)
                        .collect(Collectors.joining(", "));
            }

            // DTO létrehozása a díjazott infóval együtt
            kartyak.add(new SutiDTO(s.getId(), s.getNev(), s.getTipus(), arStr, mentesStr, s.getDijazott()));
            limit++;
        }

        model.addAttribute("kartyak", kartyak);
        return "adatbazis";
    }
}