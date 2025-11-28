package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private SutiRepository sutiRepo;

    @Autowired
    private TartalomRepository tartalomRepo;

    @Autowired
    private ArRepository arRepo;

    @Override
    public void run(String... args) throws Exception {
        if (sutiRepo.count() == 0) {
            loadSuti();
        }

        if (tartalomRepo.count() == 0) {
            loadTartalom();
        }

        if (arRepo.count() == 0) {
            loadAr();
        } else {
            System.out.println("Az 'ar' tábla nem üres, a betöltés kihagyva.");
        }
    }

    private void loadSuti() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("suti.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\t");

                if (data.length >= 4) { // Ellenőrzés
                    Long id = Long.parseLong(data[0].trim());
                    String nev = data[1].trim();
                    String tipus = data[2].trim();
                    Boolean dijazott = data[3].trim().equals("-1");
                    sutiRepo.save(new Suti(id, nev, tipus, dijazott));
                }
            }
            System.out.println("Sütik betöltve!");
        } catch (Exception e) {
            System.out.println("Hiba a suti.txt betöltésekor: " + e.getMessage());
        }
    }

    private void loadTartalom() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("tartalom.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\t");

                if (data.length >= 3) { // Ellenőrzés
                    Long id = Long.parseLong(data[0].trim());
                    Long sutiid = Long.parseLong(data[1].trim());
                    String mentes = data[2].trim();
                    tartalomRepo.save(new Tartalom(id, sutiid, mentes));
                }
            }
            System.out.println("Tartalom betöltve!");
        } catch (Exception e) {
            System.out.println("Hiba a tartalom.txt betöltésekor: " + e.getMessage());
        }
    }

    private void loadAr() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("ar.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine(); // Fejléc átugrása
            int count = 0;
            while ((line = reader.readLine()) != null) {
                // Üres sorok átugrása
                if (line.trim().isEmpty()) continue;

                String[] data = line.split("\t");

                // JAVÍTÁS: Ellenőrizzük, hogy megvan-e mind a 4 adat
                if (data.length < 4) {
                    System.out.println("HIBÁS SOR (kihagyva): " + line);
                    continue; // Átugorjuk ezt a hibás sort, de nem állunk le!
                }

                try {
                    Long id = Long.parseLong(data[0].trim());
                    Long sutiid = Long.parseLong(data[1].trim());
                    Integer ertek = Integer.parseInt(data[2].trim());
                    String egyseg = data[3].trim();

                    arRepo.save(new Ar(id, sutiid, ertek, egyseg));
                    count++;
                } catch (NumberFormatException e) {
                    System.out.println("Számformátum hiba ebben a sorban: " + line);
                }
            }
            System.out.println("Árak sikeresen betöltve! Összesen: " + count + " db.");
        } catch (Exception e) {
            System.out.println("Kritikus hiba az ar.txt betöltésekor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}