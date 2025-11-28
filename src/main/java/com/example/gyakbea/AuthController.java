package com.example.gyakbea;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthController {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private SutiRepository sutiRepo;
    @Autowired
    private ArRepository arRepo;
    @Autowired
    private TartalomRepository tartalomRepo;

    // --- ALAP OLDALAK ---
    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

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

    // --- STATIKUS OLDALAK ---
    @GetMapping("/diagram")
    public String showDiagram(Model model) {
        // 1. Lekérjük az összes sütit
        List<Suti> sutik = sutiRepo.findAll();

        // 2. Létrehozzuk a listákat a címkéknek (nevek) és az adatoknak (árak)
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        // 3. Végigmegyünk a sütiken, és megkeressük a hozzájuk tartozó árat
        for (Suti s : sutik) {
            List<Ar> arak = arRepo.findBySutiid(s.getId());

            // Csak akkor adjuk hozzá, ha van ára
            if (!arak.isEmpty()) {
                labels.add(s.getNev());           // A süti neve lesz a címke (X tengely)
                data.add(arak.get(0).getErtek()); // Az ár lesz az érték (Y tengely)
            }
        }

        // 4. Átadjuk az adatokat a Thymeleaf sablonnak
        model.addAttribute("labels", labels);
        model.addAttribute("data", data);

        return "diagram";
    }

    @GetMapping("/rest")
    public String showRest() { return "rest"; }

    @GetMapping("/admin")
    public String adminRedirect() { return "redirect:/crud"; }

    // --- KAPCSOLAT (ITT A VÁLTOZÁS) ---
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        Message message = new Message();
        boolean isLoggedIn = false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            // Ha be van lépve: Kitöltjük a nevet és emailt
            isLoggedIn = true;
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            message.setName(userDetails.getFullName());
            message.setEmail(userDetails.getUsername());
        } else {
            // Ha VENDÉG: A nevet beállítjuk "Vendég"-re
            message.setName("Vendég");
        }

        model.addAttribute("messageObj", message);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "contact";
    }

    @PostMapping("/contact")
    public String sendMessage(@Valid @ModelAttribute("messageObj") Message message, BindingResult bindingResult, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails;

        if (bindingResult.hasErrors()) {
            model.addAttribute("isLoggedIn", isLoggedIn);
            return "contact";
        }
        messageRepository.save(message);
        return "redirect:/contact?success";
    }

    @GetMapping("/messages")
    public String showMessages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return "redirect:/login";
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String currentEmail = userDetails.getUsername();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Message> messages;
        if (isAdmin) {
            messages = messageRepository.findAllByOrderByCreatedAtDesc();
        } else {
            messages = messageRepository.findByEmailOrderByCreatedAtDesc(currentEmail);
        }
        model.addAttribute("messages", messages);
        return "messages";
    }

    // --- ADATBÁZIS (Sütik) ---
    @GetMapping("/adatbazis")
    public String adatbazisPage(Model model) {
        List<SutiDTO> kartyak = new ArrayList<>();
        List<Suti> sutik = sutiRepo.findAll();
        Collections.shuffle(sutik);

        int limit = 0;
        for (Suti s : sutik) {
            if (limit >= 8) break;
            List<Ar> arak = arRepo.findBySutiid(s.getId());
            String arStr = (!arak.isEmpty()) ? arak.get(0).getErtek() + " Ft / " + arak.get(0).getEgyseg() : "Nincs ár";

            List<Tartalom> tartalmak = tartalomRepo.findBySutiid(s.getId());
            String mentesStr = (!tartalmak.isEmpty()) ? tartalmak.stream().map(Tartalom::getMentes).collect(Collectors.joining(", ")) : "";

            kartyak.add(new SutiDTO(s.getId(), s.getNev(), s.getTipus(), arStr, mentesStr, s.getDijazott()));
            limit++;
        }
        model.addAttribute("kartyak", kartyak);
        return "adatbazis";
    }

    // --- CRUD ---
    @GetMapping("/crud")
    public String listSutik(Model model) {
        List<Suti> sutik = sutiRepo.findAll();
        model.addAttribute("sutik", sutik);
        return "crud";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crud/delete/{id}")
    public String deleteSuti(@PathVariable Long id) {
        sutiRepo.deleteById(id);
        return "redirect:/crud";
    }
}
