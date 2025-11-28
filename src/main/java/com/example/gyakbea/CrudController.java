package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CrudController {

    @Autowired
    private SutiRepository sutiRepository;


    @GetMapping("/crud")
    public String listSutik(Model model) {
        List<Suti> sutik = sutiRepository.findAll();
        model.addAttribute("sutik", sutik);
        return "crud"; // crud.html-t tölti be
    }

    //  Új süti űrlap megjelenítése
    @GetMapping("/crud/uj")
    public String showCreateForm(Model model) {
        model.addAttribute("suti", new Suti());
        model.addAttribute("isNew", true); // Jelezzük, hogy ez új rekord
        return "crud_form";
    }

    //  Mentés feldolgozása
    @PostMapping("/crud/mentes")
    public String saveSuti(@ModelAttribute Suti suti) {
        sutiRepository.save(suti);
        return "redirect:/crud";
    }

    //  Szerkesztő űrlap megjelenítése
    @GetMapping("/crud/szerkeszt/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Suti> suti = sutiRepository.findById(id);
        if (suti.isPresent()) {
            model.addAttribute("suti", suti.get());
            model.addAttribute("isNew", false); // Jelezzük, hogy ez szerkesztés
            return "crud_form";
        } else {
            return "redirect:/crud";
        }
    }

    //  Törlés
    @GetMapping("/crud/torles/{id}")
    public String deleteSuti(@PathVariable Long id) {
        sutiRepository.deleteById(id);
        return "redirect:/crud";
    }
}