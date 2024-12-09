package org.example.deliverysystem.request_manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.deliverysystem.warehouseManager.WarehouseManager;
import org.example.deliverysystem.warehouseManager.Package;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {
    private final List<CustomRequest> requests;

    @Autowired
    private WarehouseManager warehouseManager;

    public RequestController() {
        this.requests = new ArrayList<>();
    }

    public List<CustomRequest> getRequests() {
        return requests;
    }

    public List<CustomRequest> getRequestsBySender(String senderName) {
        return requests.stream()
                .filter(request -> request.getSenderName().equalsIgnoreCase(senderName))
                .toList();
    }

    @PostMapping("/create-request")
    public String handleCreateRequest(
            @RequestParam String senderName,
            @RequestParam String senderAddress,
            @RequestParam String senderContact,
            @RequestParam List<String> recipientNames,
            @RequestParam List<String> recipientAddresses,
            @RequestParam List<String> recipientContacts,
            @RequestParam List<String> packageContents,
            @RequestParam List<Double> packageWeights,
            Model model
    ) {

        List<Recipient> recipients = new ArrayList<>();
        for (int i = 0; i < recipientNames.size(); i++) {
            recipients.add(new Recipient(
                    recipientNames.get(i),
                    recipientAddresses.get(i),
                    recipientContacts.get(i),
                    packageContents.get(i),
                    packageWeights.get(i)
            ));
        }

        CustomRequest newRequest = new CustomRequest(senderName, senderAddress, senderContact, recipients);
        requests.add(newRequest); // Pridáme do zoznamu
        model.addAttribute("message", "Request successfully created!");
        return "redirect:/requests?senderName=" + senderName;
    }

    @PostMapping("/mark-paid")
    public String markRequestAsPaid(@RequestParam("requestId") int requestId,
                                    @RequestParam(value = "senderName", required = false) String senderName) {
        // Označenie ako zaplatené
        requests.stream()
                .filter(request -> request.getId() == requestId)
                .findFirst()
                .ifPresent(request -> request.setPaid(true));

        // Zachovanie parametra senderName v presmerovaní
        if (senderName != null && !senderName.isEmpty()) {
            return "redirect:/requests?senderName=" + senderName;
        }

        // Presmerovanie na všetky requests, ak senderName nie je prítomné
        return "redirect:/requests";
    }

    @PostMapping("/approve")
    public String approveRequest(
            @RequestParam int requestId,
            @RequestParam String warehouseLocation,
            @RequestParam String deliveryDates,
            Model model) {
        System.out.println("Approving request with ID: " + requestId);

        // Generovanie UID
        String uid = UUID.randomUUID().toString();


        requests.stream()
                .filter(request -> request.getId() == requestId)
                .findFirst()
                .ifPresentOrElse(request -> {
                    request.getRecipients().forEach(recipient -> {
                        Package newPackage = new Package(
                                uid,
                                "Pending Delivery",
                                recipient.getAddress(),
                                recipient.getPackageWeight(),
                                request.getSenderName(),
                                recipient.getContact(),
                                warehouseLocation
                        );
                        System.out.println("Osoba " + request.getSenderName() + " moze dojst odovzdat balik do skladu " +deliveryDates + " s identiifkacnym cislom " + uid);
                        warehouseManager.addPackage(newPackage);
                    });
                    requests.remove(request);
                }, () -> System.out.println("Request with ID " + requestId + " not found."));

        return "redirect:/requests/overview";
    }

    @PostMapping("/reject")
    public String rejectRequest(
            @RequestParam int requestId,
            @RequestParam String rejectionReason) {
        if (rejectionReason.length() >= 5) {
            requests.removeIf(request -> request.getId() == requestId);
            System.out.println("Request rejected for user with ID " + requestId
                    + ". Reason: " + rejectionReason);
        }
        return "redirect:/requests/overview"; // Návrat na overview
    }
}