package org.example.deliverysystem.request_manager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;


@Controller
public class RouteController {
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to the Delivery System!");
        return "navbar";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
