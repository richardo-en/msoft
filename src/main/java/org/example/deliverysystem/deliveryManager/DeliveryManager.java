package org.example.deliverysystem.deliveryManager;

import org.example.deliverysystem.warehouseManager.Package;
import org.springframework.stereotype.Component;
import org.example.deliverysystem.warehouseManager.WarehouseManager;
import org.example.deliverysystem.deliveryManager.Delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
@Component
public class DeliveryManager {

    private final WarehouseManager warehouseManager;
    private static final List<String> vehicles = List.of("ABC-123", "XYZ-789", "MNO-456");

    public DeliveryManager(WarehouseManager warehouseManager) {
        this.warehouseManager = warehouseManager;
    }
    private static List<Delivery> deliveries = new ArrayList<>();

    public Delivery createDelivery(List<Package> packages, String deliveryDate, String deliveryStartTime, int estimatedDuration) {
        // Kontrola dostupnosti vozidla
        String availableVehicle = findAvailableVehicle(deliveryDate, deliveryStartTime, estimatedDuration);

        if (availableVehicle == null) {
            throw new RuntimeException("No available vehicles for the selected time.");
        }

        // Vytvorenie objektu Delivery so všetkými požadovanými parametrami
        Delivery newDelivery = new Delivery(
                UUID.randomUUID().toString(),
                packages,
                deliveryDate,
                deliveryStartTime,
                estimatedDuration
        );
        newDelivery.setAssignedVehicle(availableVehicle);

        deliveries.add(newDelivery);

        packages.forEach(pkg -> {
            pkg.setStatus("Planned");
            warehouseManager.updatePackageStatus(pkg);
        });

        return newDelivery;
    }

    // Získať balíky so stavom "Stored"
    public List<Package> getStoredPackages() {
        return warehouseManager.getAllPackages().stream()
                .filter(pkg -> "Stored".equals(pkg.getStatus()))
                .collect(Collectors.toList());
    }

    // Pridať novú skupinu balíkov na doručenie
    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    public static List<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveries);
    }

    public Delivery getDeliveryById(String deliveryId) {
        return deliveries.stream()
                .filter(delivery -> delivery.getId().equals(deliveryId))
                .findFirst()
                .orElse(null);
    }


    private String findAvailableVehicle(String date, String startTime, int duration) {
        for (String vehicle : vehicles) {
            boolean isAvailable = deliveries.stream()
                    .filter(delivery -> delivery.getAssignedVehicle() != null && delivery.getAssignedVehicle().equals(vehicle))
                    .noneMatch(delivery -> isOverlapping(date, startTime, duration, delivery));

            if (isAvailable) {
                return vehicle;
            }
        }
        return null;
    }

    private boolean isOverlapping(String date, String startTime, int duration, Delivery delivery) {
        if (!delivery.getDeliveryDate().equals(date)) {
            return false;
        }

        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int deliveryStartHour = Integer.parseInt(delivery.getDeliveryStartTime().split(":")[0]);
        int deliveryEndHour = deliveryStartHour + delivery.getEstimatedDuration();

        return (startHour >= deliveryStartHour && startHour < deliveryEndHour) ||
                (startHour + duration > deliveryStartHour && startHour + duration <= deliveryEndHour);
    }

    public List<Delivery> getDeliveriesByStatus(String status) {
        return deliveries.stream()
                .filter(delivery -> status.equals(delivery.getStatus()))
                .collect(Collectors.toList());
    }


}
