package com.example.gyakbea;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArRepository extends JpaRepository<Ar, Long> {
    // SutiID alapján keressük az árat
    List<Ar> findBySutiid(Long sutiid);
}