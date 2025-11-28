package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// ÚJ IMPORTÓK A LISTÁKHOZ ÉS A MAP-HEZ
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // EZEKET ADJUK HOZZÁ, HOGY ELÉRJÜK AZ ADATOKAT
    @Autowired
    private SutiRepository sutiRepository;

    @Autowired
    private ArRepository arRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Főoldal
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Login oldal
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Regisztrációs űrlap megjelenítése
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Regisztráció feldolgozása
    @PostMapping("/regisztral_feldolgoz")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Ez az email cím már foglalt!");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return "redirect:/login?success";
    }

    // Admin oldal (csak teszt)
    @GetMapping("/admin")
    public String adminPage() {
        return "index";
    }

    // Üzenetek oldal (csak teszt)
    @GetMapping("/messages")
    public String messagesPage() {
        return "index";
    }

    // --- ÚJ: DIAGRAM OLDAL ---
    @GetMapping("/diagram")
    public String diagramPage(Model model) {
        // 1. Minden adat lekérése
        List<Suti> sutik = sutiRepository.findAll();
        List<Ar> arak = arRepository.findAll();

        // 2. Segédmap készítése: Süti ID -> Süti Név
        // Így könnyen megtaláljuk, melyik árhoz melyik név tartozik
        Map<Long, String> sutiNevMap = sutik.stream()
                .collect(Collectors.toMap(Suti::getId, Suti::getNev));

        // 3. Adatok előkészítése a Chart.js számára
        List<String> labels = new ArrayList<>(); // X tengely (Nevek)
        List<Integer> data = new ArrayList<>();  // Y tengely (Árak)

        for (Ar ar : arak) {
            String nev = sutiNevMap.get(ar.getSutiid());
            // Csak akkor adjuk hozzá, ha van érvényes név hozzá
            if (nev != null) {
                labels.add(nev);
                data.add(ar.getErtek());
            }
        }

        // 4. Adatok átadása a HTML-nek
        model.addAttribute("labels", labels);
        model.addAttribute("data", data);

        return "diagram"; // diagram.html-t fogja keresni
    }
}