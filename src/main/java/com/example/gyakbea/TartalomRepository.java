package com.example.gyakbea;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TartalomRepository extends JpaRepository<Tartalom, Long> {
    // SutiID alapján keressük a mentességeket (lehet több is egy sütihez)
    List<Tartalom> findBySutiid(Long sutiid);
}