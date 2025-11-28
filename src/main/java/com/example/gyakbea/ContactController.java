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

    // 1. Kapcsolat űrlap megjelenítése (Autokitöltéssel)
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        Message message = new Message();

        // Lekérjük a jelenlegi hitelesítési információt
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Ellenőrizzük, hogy a felhasználó be van-e jelentkezve (és nem csak "anonymous")
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            // Ha be van lépve, elkérjük az adatait
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            // Beállítjuk a nevet és az emailt az űrlap objektumba
            // Így a HTML űrlap mezői automatikusan kitöltődnek
            message.setName(userDetails.getFullName());
            message.setEmail(userDetails.getUsername()); // A username nálunk az email
        }

        model.addAttribute("messageObj", message);
        return "contact";
    }

    // 2. Üzenet elküldése (Validációval)
    @PostMapping("/contact")
    public String sendMessage(@Valid @ModelAttribute("messageObj") Message message, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "contact"; // Ha hiba van, visszaküldjük az űrlapra a hibaüzenetekkel
        }

        messageRepository.save(message); // Mentés az adatbázisba
        return "redirect:/contact?success";
    }

    // 3. Üzenetek listázása (Csak belépett felhasználóknak)
    @GetMapping("/messages")
    public String showMessages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Biztonsági ellenőrzés (bár a WebSecurityConfig is védi)
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return "redirect:/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String currentEmail = userDetails.getUsername();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Message> messages;
        if (isAdmin) {
            // Admin minden üzenetet lát
            messages = messageRepository.findAllByOrderByCreatedAtDesc();
        } else {
            // Sima felhasználó csak a sajátjait (email alapján)
            messages = messageRepository.findByEmailOrderByCreatedAtDesc(currentEmail);
        }

        model.addAttribute("messages", messages);
        return "messages";
    }
}