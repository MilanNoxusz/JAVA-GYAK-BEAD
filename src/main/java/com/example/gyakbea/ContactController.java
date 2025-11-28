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

    // 1. Kapcsolat űrlap megjelenítése (Bárki láthatja)
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("messageObj", new Message());
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
        String currentEmail = ((CustomUserDetails) auth.getPrincipal()).getUsername();
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