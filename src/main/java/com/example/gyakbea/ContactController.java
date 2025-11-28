package com.example.gyakbea;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ContactController {

    @Autowired
    private MessageRepository messageRepository;

    // 1. Kapcsolat űrlap megjelenítése
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        Message message = new Message();
        boolean isLoggedIn = false;

        // Lekérjük a hitelesítési infót
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Ellenőrizzük, hogy be van-e jelentkezve
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            isLoggedIn = true;
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            // Ha be van lépve, kitöltjük az adatait
            message.setName(userDetails.getFullName());
            message.setEmail(userDetails.getUsername());
        }

        model.addAttribute("messageObj", message);
        model.addAttribute("isLoggedIn", isLoggedIn); // Ezt felhasználjuk a HTML-ben a figyelmeztetéshez
        return "contact";
    }

    // 2. Üzenet elküldése
    @PostMapping("/contact")
    public String sendMessage(@Valid @ModelAttribute("messageObj") Message message, BindingResult bindingResult, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails;

        // Ha be van jelentkezve, szerver oldalon is felülírhatjuk/ellenőrizhetjük az emailt a biztonság kedvéért,
        // de a feladat szerint "NE TUDJA MÓDOSÍTANI" -> ezt a HTML-ben `readonly` attribútummal oldjuk meg.

        if (bindingResult.hasErrors()) {
            model.addAttribute("isLoggedIn", isLoggedIn); // Hiba esetén is kell a változó
            return "contact";
        }

        messageRepository.save(message);
        return "redirect:/contact?success";
    }

    // 3. Üzenetek listázása (Csak belépett felhasználóknak - WebSecurityConfig védi)
    @GetMapping("/messages")
    public String showMessages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Extra védelem, bár a config is szűri
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return "redirect:/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String currentEmail = userDetails.getUsername();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Message> messages;
        if (isAdmin) {
            messages = messageRepository.findAllByOrderByCreatedAtDesc();
        } else {
            messages = messageRepository.findByEmailOrderByCreatedAtDesc(currentEmail);
        }

        model.addAttribute("messages", messages);
        return "messages";
    }
}