package org.example.deliverysystem;
import org.example.deliverysystem.request_manager.RequestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.example.deliverysystem.request_manager.CustomRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class MainController {
    private final RequestController requestController;

    public MainController(RequestController requestController) {
        this.requestController = requestController;
    }
    @GetMapping("/")
    public String homePage() {
        return "index"; // Musí zodpovedať názvu súboru
    }


    @GetMapping("/choose-recipients")
    public String chooseRecipientsForm(Model model) {
        model.addAttribute("recipientsCount", 1); // Defaultný počet klientov
        return "requests/choose-recipients"; // Musí zodpovedať názvu súboru
    }

    @PostMapping("/choose-recipients")
    public String generateRecipientForms(@RequestParam("recipientsCount") int recipientsCount, Model model) {
        model.addAttribute("recipientsCount", recipientsCount);
        return "requests/create-request"; // Dynamický formulár
    }

    @GetMapping
    public String showRequestForm() {
        // Zobrazí formulár na zadanie mena
        return "requests/request-form"; // Šablóna pre formulár
    }


    @GetMapping(params = "senderName")
    public String viewFilteredRequests(@RequestParam String senderName, Model model) {
        List<CustomRequest> filteredRequests = requestController.getRequestsBySender(senderName);
        model.addAttribute("requests", filteredRequests);
        model.addAttribute("filterMessage", "Showing requests for sender: " + senderName);
        model.addAttribute("senderName", senderName); // Pridáme do modelu
        return "requests/requests"; // Spoločná šablóna pre zobrazenie requestov
    }

    @PostMapping
    public String handleRequestForm(@RequestParam String senderName) {
        // Presmerovanie na stránku s filtrovanými requestami
        return "redirect:/requests?senderName=" + senderName;
    }



    @GetMapping("/overview-access")
    public String accessOverviewForm(Model model) {
        model.addAttribute("errorMessage", null);
        return "requests/overview-access"; // Stránka na zadanie mena
    }

    @PostMapping("/overview-access")
    public String handleOverviewAccess(@RequestParam String accessName, HttpSession session, Model model) {
        if ("administrativa".equalsIgnoreCase(accessName)) {
            // Nastavíme session flag, ktorý signalizuje, že užívateľ je autorizovaný
            session.setAttribute("accessGranted", "true");
            return "redirect:/requests/overview"; // Presmerujeme na overview
        }
        model.addAttribute("errorMessage", "Access denied. Incorrect name.");
        return "requests/overview-access"; // Znovu zobrazíme formulár
    }

    @GetMapping("/overview")
    public String viewPaidRequests(HttpSession session, Model model) {
        // Skontrolujeme, či je užívateľ autorizovaný
        String accessGranted = (String) session.getAttribute("accessGranted");
        if (accessGranted == null || !accessGranted.equals("true")) {
            // Presmerovanie na stránku s prístupovým formulárom
            return "redirect:/requests/overview-access";
        }

        // Ak je autorizovaný, zobrazíme len zaplatené requesty
        List<CustomRequest> filteredRequests = requestController.getRequests()
                .stream()
                .filter(CustomRequest::isPaid) // Iba zaplatené requesty
                .toList();
        model.addAttribute("requests", filteredRequests);
        return "requests/requests-overview";
    }

    @GetMapping("/overview/details")
    public String viewRequestDetails(@RequestParam int requestId, Model model) {
        CustomRequest request = requestController.getRequests()
                .stream()
                .filter(r -> r.getId() == requestId)
                .findFirst()
                .orElse(null);

        if (request == null) {
            model.addAttribute("errorMessage", "Request not found.");
            return "error"; // Ak request neexistuje
        }

        model.addAttribute("request", request);
        return "requests/request-details"; // Šablóna pre detaily requestu
    }
}
