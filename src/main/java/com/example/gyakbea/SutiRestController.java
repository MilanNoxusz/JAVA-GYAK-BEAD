package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sutik")
public class SutiRestController {

    @Autowired
    private SutiRepository sutiRepository;

    // Összes süti lekérdezése
    @GetMapping
    public List<Suti> getAllSuti() {
        return sutiRepository.findAll();
    }

    //  Egy süti lekérdezése ID alapján
    @GetMapping("/{id}")
    public ResponseEntity<Suti> getSutiById(@PathVariable Long id) {
        Optional<Suti> suti = sutiRepository.findById(id);
        return suti.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Új süti létrehozása
    @PostMapping
    public ResponseEntity<Suti> createSuti(@RequestBody Suti suti) {
        try {
            Suti savedSuti = sutiRepository.save(suti);
            return new ResponseEntity<>(savedSuti, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  Meglévő süti módosítása
    @PutMapping("/{id}")
    public ResponseEntity<Suti> updateSuti(@PathVariable Long id, @RequestBody Suti sutiDetails) {
        Optional<Suti> sutiData = sutiRepository.findById(id);

        if (sutiData.isPresent()) {
            Suti suti = sutiData.get();
            suti.setNev(sutiDetails.getNev());
            suti.setTipus(sutiDetails.getTipus());
            suti.setDijazott(sutiDetails.getDijazott());
            return new ResponseEntity<>(sutiRepository.save(suti), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 5. DELETE: Süti törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSuti(@PathVariable Long id) {
        try {
            sutiRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}