package org.example.deliverysystem.deliveryManager;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.example.deliverysystem.warehouseManager.Package;
import org.example.deliverysystem.warehouseManager.WarehouseManager;

import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryManager deliveryManager;
    private final WarehouseManager warehouseManager;

    public DeliveryController(DeliveryManager deliveryManager, WarehouseManager warehouseManager) {
        this.deliveryManager = deliveryManager;
        this.warehouseManager = warehouseManager;
    }

    @GetMapping("/")
    public String loginPage() {
        return "delivery/login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, HttpSession session) {
        if ("planovanie".equals(username)) {
            session.setAttribute("loggedInUser", "planovanie");
            return "redirect:/delivery/dashboard";
        } else if ("priprava".equals(username)) {
            session.setAttribute("loggedInUser", "priprava");
            return "redirect:/delivery/preparation-dashboard";
        } else if ("vodic".equals(username)) {
            session.setAttribute("loggedInUser", "driver");
            return "redirect:/delivery/driver-dashboard";
        }
        return "redirect:/delivery/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"planovanie".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        model.addAttribute("storedPackages", deliveryManager.getStoredPackages());
        return "delivery/dashboard";
    }

    @PostMapping("/create-delivery")
    public String showCreateDeliveryForm(
            @RequestParam("selectedPackages") List<String> selectedPackageIds,
            HttpSession session,
            Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"planovanie".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        List<Package> selectedPackages = selectedPackageIds.stream()
                .map(warehouseManager::findPackageById)
                .filter(pkg -> pkg != null)
                .toList();

        if (selectedPackages.isEmpty()) {
            return "redirect:/delivery/dashboard?error=no-packages";
        }

        model.addAttribute("selectedPackages", selectedPackages);
        return "delivery/create-delivery";
    }

    @PostMapping("/submit-delivery")
    public String submitDelivery(
            @RequestParam("deliveryDate") String deliveryDate,
            @RequestParam("deliveryStartTime") String deliveryStartTime,
            @RequestParam("estimatedDuration") int estimatedDuration,
            @RequestParam("packageIds") List<String> packageIds,
            Model model,
            HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"planovanie".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        List<Package> selectedPackages = packageIds.stream()
                .map(warehouseManager::findPackageById)
                .filter(pkg -> pkg != null)
                .toList();

        if (selectedPackages.isEmpty()) {
            return "redirect:/delivery/dashboard?error=empty-delivery";
        }

        try {
            Delivery newDelivery = deliveryManager.createDelivery(
                    selectedPackages,
                    deliveryDate,
                    deliveryStartTime,
                    estimatedDuration
            );


            return "redirect:/delivery/summary?deliveryId=" + newDelivery.getId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("selectedPackages", selectedPackages);
            return "delivery/create-delivery";
        }
    }

    @GetMapping("/summary")
    public String showDeliverySummary(@RequestParam("deliveryId") String deliveryId, Model model) {
        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);

        if (delivery == null) {
            return "redirect:/delivery/dashboard?error=delivery-not-found";
        }

        model.addAttribute("delivery", delivery);
        return "delivery/summary";
    }

    @GetMapping("/preparation-dashboard")
    public String showPreparationDashboard(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"priprava".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        List<Delivery> plannedDeliveries = deliveryManager.getDeliveriesByStatus("Planned");
        model.addAttribute("plannedDeliveries", plannedDeliveries);

        return "delivery/preparation-dashboard";
    }

    @GetMapping("/prepare-delivery")
    public String showDeliveryPackages(@RequestParam("deliveryId") String deliveryId, HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"priprava".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);
        if (delivery == null) {
            return "redirect:/delivery/preparation-dashboard?error=delivery-not-found";
        }

        List<Package> packagesToPrepare = delivery.getPackages().stream()
                .filter(pkg -> "Planned".equals(pkg.getStatus()))
                .toList();

        model.addAttribute("delivery", delivery);
        model.addAttribute("packagesToPrepare", packagesToPrepare);

        return "delivery/prepare-delivery";
    }

    @PostMapping("/prepare-package")
    public String preparePackage(
            @RequestParam("packageId") String packageId,
            @RequestParam("deliveryId") String deliveryId,
            HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"priprava".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        Package pkg = warehouseManager.findPackageById(packageId);
        if (pkg != null && "Planned".equals(pkg.getStatus())) {
            pkg.setStatus("In Preparation");
            warehouseManager.updatePackageStatus(pkg);
        }

        return "redirect:/delivery/prepare-delivery?deliveryId=" + deliveryId;
    }


    @PostMapping("/load-delivery")
    public String loadDelivery(
            @RequestParam("deliveryId") String deliveryId,
            @RequestParam("vehicleId") String vehicleId,
            HttpSession session,
            Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"priprava".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);
        if (delivery == null || !vehicleId.equals(delivery.getAssignedVehicle())) {
            model.addAttribute("error", "Invalid vehicle ID");
            return "redirect:/delivery/prepare-delivery?deliveryId=" + deliveryId;
        }

        delivery.setStatus("Ready to Ship");
        return "redirect:/delivery/preparation-dashboard";
    }

    @GetMapping("/driver-dashboard")
    public String showDriverDashboard(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"driver".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        List<Delivery> readyToShipDeliveries = deliveryManager.getDeliveriesByStatus("Ready to Ship");
        model.addAttribute("readyToShipDeliveries", readyToShipDeliveries);

        return "delivery/driver-dashboard";
    }

    @GetMapping("/deliver-packages")
    public String showDeliverPackages(@RequestParam("deliveryId") String deliveryId, HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"driver".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);
        if (delivery == null) {
            return "redirect:/delivery/driver-dashboard?error=delivery-not-found";
        }

        List<Package> packagesToDeliver = delivery.getPackages().stream()
                .filter(pkg -> "Planned".equals(pkg.getStatus()) || "In Preparation".equals(pkg.getStatus()))
                .toList();

        model.addAttribute("delivery", delivery);
        model.addAttribute("packagesToDeliver", packagesToDeliver);

        return "delivery/deliver-packages";
    }

    @PostMapping("/update-package-status")
    public String updatePackageStatus(
            @RequestParam("packageId") String packageId,
            @RequestParam("deliveryId") String deliveryId,
            @RequestParam("status") String status,
            HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"driver".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        // Nájdeme balík pomocou ID a aktualizujeme jeho stav
        Package packageToUpdate = warehouseManager.findPackageById(packageId);
        if (packageToUpdate != null && ("Delivered".equals(status) || "Not Delivered".equals(status))) {
            packageToUpdate.setStatus(status);
            warehouseManager.updatePackageStatus(packageToUpdate);
        }

        // Získame doručovaciu skupinu podľa ID
        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);
        if (delivery == null) {
            return "redirect:/delivery/driver-dashboard?error=delivery-not-found";
        }

        // Skontrolujeme, či ešte existujú balíky na doručenie
        boolean hasRemainingPackages = delivery.getPackages().stream()
                .anyMatch(pkg -> !"Delivered".equals(pkg.getStatus()) && !"Not Delivered".equals(pkg.getStatus()));

        if (!hasRemainingPackages) {
            // Skontrolujeme, či existujú balíky s `Not Delivered`
            boolean hasUndeliveredPackages = delivery.getPackages().stream()
                    .anyMatch(pkg -> "Not Delivered".equals(pkg.getStatus()));

            if (hasUndeliveredPackages) {
                return "redirect:/delivery/undelivered-packages?deliveryId=" + deliveryId;
            }

            // Ak sú všetky balíky `Delivered`
            delivery.setStatus("Completed");
            return "redirect:/delivery/driver-dashboard";
        }

        return "redirect:/delivery/deliver-packages?deliveryId=" + deliveryId;
    }

    @GetMapping("/undelivered-packages")
    public String showUndeliveredPackages(@RequestParam("deliveryId") String deliveryId, HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"driver".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);
        if (delivery == null) {
            return "redirect:/delivery/driver-dashboard?error=delivery-not-found";
        }

        List<Package> undeliveredPackages = delivery.getPackages().stream()
                .filter(pkg -> "Not Delivered".equals(pkg.getStatus()))
                .toList();

        model.addAttribute("delivery", delivery);
        model.addAttribute("undeliveredPackages", undeliveredPackages);

        return "delivery/undelivered-packages";
    }

    @PostMapping("/store-undelivered-package")
    public String storeUndeliveredPackage(
            @RequestParam("packageId") String packageId,
            @RequestParam("deliveryId") String deliveryId,
            HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (!"driver".equals(loggedInUser)) {
            return "redirect:/delivery/login";
        }

        // Nájdeme balík pomocou ID
        Package packageToStore = warehouseManager.findPackageById(packageId);
        if (packageToStore != null && "Not Delivered".equals(packageToStore.getStatus())) {
            packageToStore.setStatus("Stored");
            warehouseManager.updatePackageStatus(packageToStore);
        }

        // Získame doručovaciu skupinu
        Delivery delivery = deliveryManager.getDeliveryById(deliveryId);
        if (delivery == null) {
            return "redirect:/delivery/driver-dashboard?error=delivery-not-found";
        }

        // Skontrolujeme, či zostávajú nedoručené balíky
        boolean hasRemainingUndelivered = delivery.getPackages().stream()
                .anyMatch(pkg -> "Not Delivered".equals(pkg.getStatus()));

        if (!hasRemainingUndelivered) {
            delivery.setStatus("Completed");
            return "redirect:/delivery/driver-dashboard";
        }

        return "redirect:/delivery/undelivered-packages?deliveryId=" + deliveryId;
    }
}
