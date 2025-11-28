package com.example.gyakbea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SutiRepository sutiRepository;

    @Autowired
    private ArRepository arRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

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

    @GetMapping("/admin")
    public String adminPage() {
        return "index";
    }

    @GetMapping("/messages")
    public String messagesPage() {
        return "index";
    }

    @GetMapping("/diagram")
    public String diagramPage(Model model) {
        List<Suti> sutik = sutiRepository.findAll();
        List<Ar> arak = arRepository.findAll();

        Map<Long, String> sutiNevMap = sutik.stream()
                .collect(Collectors.toMap(Suti::getId, Suti::getNev));

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (Ar ar : arak) {
            String nev = sutiNevMap.get(ar.getSutiid());
            if (nev != null) {
                labels.add(nev);
                data.add(ar.getErtek());
            }
        }

        model.addAttribute("labels", labels);
        model.addAttribute("data", data);

        return "diagram";
    }

    // --- ÚJ: REST API UI Oldal ---
    @GetMapping("/restapi")
    public String restApiPage() {
        return "rest";
    }
}