package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

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
        // Ellenőrizzük, hogy van-e már ilyen email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Ez az email cím már foglalt!");
            return "register";
        }

        // Jelszó titkosítása
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Alapértelmezett szerepkör: Regisztrált látogató (ROLE_USER)
        // Ha admint akarsz tesztelni, itt írd át ROLE_ADMIN-ra ideiglenesen, vagy az adatbázisban módosítsd
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return "redirect:/login?success";
    }

    // Admin oldal (csak teszt)
    @GetMapping("/admin")
    public String adminPage() {
        return "index"; // Vagy külön admin.html
    }

    // Üzenetek oldal (csak teszt)
    @GetMapping("/messages")
    public String messagesPage() {
        return "index"; // Vagy külön messages.html
    }
}