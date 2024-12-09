package org.example.deliverysystem.warehouseManager;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseManager {
    private List<Package> packages;
    private List<Package> temporaryAcceptedPackages;

    public WarehouseManager() {
        this.packages = new ArrayList<>();
        this.temporaryAcceptedPackages = new ArrayList<>();
    }

    // Pridanie balíka na sklad
    public void addPackage(Package pkg) {
        packages.add(pkg);
    }

    public List<Package> findPackagesByUID(String uid) {
        return packages.stream()
                .filter(pkg -> pkg.getSenderUID().equals(uid))
                .collect(Collectors.toList());
    }
    public void acceptPackage(Package pkg) {
        pkg.setStatus("Accepted");
        temporaryAcceptedPackages.add(pkg);
        System.out.println("Printing id: " + pkg.getId());
    }

    // Odmietnutie balíka
    public void rejectPackage(Package pkg) {
        pkg.setStatus("Rejected");
        packages.remove(pkg);
    }

    public List<Package> getTemporaryAcceptedPackages() {
        return temporaryAcceptedPackages;
    }

    // Odstránenie balíka zo skladu podľa ID
    public void removePackage(String packageId) {
        packages.removeIf(pkg -> pkg.getId().equals(packageId));
    }

    // Vyhľadanie balíka podľa ID
    public Package findPackageById(String packageId) {
        return packages.stream()
                .filter(pkg -> pkg.getId().equals(packageId))
                .findFirst()
                .orElse(null);
    }

    // Zobrazenie všetkých balíkov
    public List<Package> getAllPackages() {
        return new ArrayList<>(packages);
    }


    public Package findTemporaryPackageById(String packageId) {
        return temporaryAcceptedPackages.stream()
                .filter(pkg -> pkg.getId().equals(packageId))
                .findFirst()
                .orElse(null);
    }

    public void storePackage(Package pkg) {
        pkg.setStatus("Stored");
        temporaryAcceptedPackages.remove(pkg); // Odstránenie z dočasného zoznamu
    }

    public void updatePackageStatus(Package pkg) {
        Package existingPackage = findPackageById(pkg.getId());
        if (existingPackage != null) {
            existingPackage.setStatus(pkg.getStatus());
        }
    }

}