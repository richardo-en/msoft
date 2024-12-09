package org.example.deliverysystem.warehouseManager;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseManager warehouseManager;

    @GetMapping("/")
    public String loginPage() {
        return "warehouse/login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, HttpSession session) {
        if ("prijem".equals(username)) {
            session.setAttribute("loggedInUser", username);
            return "redirect:/warehouse/enter-uid";
        }
        return "redirect:/warehouse/login?error=true";
    }

    @GetMapping("/enter-uid")
    public String enterUIDPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"prijem".equals(loggedInUser)) {
            return "redirect:/warehouse/login";
        }

        // Zobrazenie dočasne akceptovaných balíkov
        List<Package> temporaryPackages = warehouseManager.getTemporaryAcceptedPackages();
        model.addAttribute("temporaryPackages", temporaryPackages);
        return "warehouse/enter-uid";
    }

    @GetMapping("/save-packages")
    public String showSavePackagesPage(Model model) {
        List<Package> temporaryPackages = warehouseManager.getTemporaryAcceptedPackages();
        model.addAttribute("temporaryPackages", temporaryPackages);
        return "warehouse/save-packages";
    }

    @PostMapping("/save-packages")
    public String saveIndividualPackage() {
        return "redirect:/warehouse/save-packages";
    }

    @PostMapping("/remove-package")
    public String RemoveIndividualPackage(@RequestParam String packageId) {
        Package pkg = warehouseManager.findTemporaryPackageById(packageId);
        if (pkg != null) {
            warehouseManager.storePackage(pkg); // Presun balíka a aktualizácia stavu
        } else {
            System.out.println("Package not found in temporary list!"); // Debug výpis
        }
        return "redirect:/warehouse/save-packages";
    }

    @GetMapping("/receive-packages")
    public String showPackages(@RequestParam String uid, Model model) {
        List<Package> packages = warehouseManager.findPackagesByUID(uid)
                .stream()
                .filter(pkg -> "Pending Delivery".equals(pkg.getStatus())) // Filtrovanie na "Pending Delivery"
                .toList();

        if (packages.isEmpty()) {
            model.addAttribute("error", "No packages found for the provided UID or UID does not exist.");
            return "redirect:/warehouse/enter-uid";
        }

        model.addAttribute("packages", packages);
        model.addAttribute("uid", uid);
        return "warehouse/receive-packages";
    }

    @PostMapping("/receive-packages")
    public String handleUIDSubmission(@RequestParam String uid) {
        return "redirect:/warehouse/receive-packages?uid=" + uid;
    }

    @PostMapping("/accept")
    public String acceptPackage(@RequestParam String packageId, @RequestParam String uid) {
        Package currentPackage = warehouseManager.findPackageById(packageId);
        if (currentPackage != null) {
            warehouseManager.acceptPackage(currentPackage);
        }

        // Skontrolovať, či existujú ďalšie balíky pre zadané UID
        boolean hasPendingPackages = warehouseManager.findPackagesByUID(uid)
                .stream()
                .anyMatch(pkg -> "Pending Delivery".equals(pkg.getStatus()));

        if (!hasPendingPackages) {
            return "redirect:/warehouse/enter-uid";
        }

        return "redirect:/warehouse/receive-packages?uid=" + uid;
    }

    @PostMapping("/reject")
    public String rejectPackage(@RequestParam String packageId, @RequestParam String uid) {
        Package rejectedPackage = warehouseManager.findPackageById(packageId);
        if (rejectedPackage != null) {
            warehouseManager.rejectPackage(rejectedPackage);
        }

        // Skontrolovať, či existujú ďalšie balíky pre zadané UID
        boolean hasPendingPackages = warehouseManager.findPackagesByUID(uid)
                .stream()
                .anyMatch(pkg -> "Pending Delivery".equals(pkg.getStatus()));

        if (!hasPendingPackages) {
            return "redirect:/warehouse/enter-uid";
        }

        return "redirect:/warehouse/receive-packages?uid=" + uid;
    }

    @GetMapping("/store-packages")
    public String showTemporaryPackages(Model model) {
        List<Package> temporaryPackages = warehouseManager.getTemporaryAcceptedPackages();
        model.addAttribute("temporaryPackages", temporaryPackages);
        return "warehouse/store-packages";
    }

    @PostMapping("/store-package")
    public String storePackage(@RequestParam String packageId) {
        Package pkg = warehouseManager.findTemporaryPackageById(packageId);
        if (pkg != null) {
            warehouseManager.storePackage(pkg);
        }
        return "redirect:/warehouse/store-packages";
    }


}