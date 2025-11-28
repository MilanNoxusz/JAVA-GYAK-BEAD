package com.example.gyakbea;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Diagram oldal
    @GetMapping("/diagram")
    public String showDiagram() {
        return "diagram"; // diagram.html-t keres
    }

    // CRUD oldal
    @GetMapping("/crud")
    public String showCrud() {
        return "crud"; // crud.html-t keres
    }

    // RESTful oldal
    @GetMapping("/rest")
    public String showRest() {
        return "rest"; // rest.html-t keres
    }

    // Admin oldal -> Átirányít a CRUD-ra (Kérésed szerint)
    @GetMapping("/admin")
    public String adminRedirect() {
        return "redirect:/crud";
    }
}