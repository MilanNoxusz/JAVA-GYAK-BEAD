package com.example.gyakbea;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Keresés email alapján (hogy a felhasználó lássa a saját üzeneteit)
    List<Message> findByEmailOrderByCreatedAtDesc(String email);

    // Adminnak minden üzenet időrendben
    List<Message> findAllByOrderByCreatedAtDesc();
}