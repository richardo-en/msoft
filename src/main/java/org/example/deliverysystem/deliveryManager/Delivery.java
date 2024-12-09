package org.example.deliverysystem.deliveryManager;

import org.example.deliverysystem.warehouseManager.Package;

import java.util.List;
import java.util.UUID;
public class Delivery {
    private String id;
    private List<Package> packages;
    private String deliveryDate;
    private String deliveryStartTime; // Formát HH:mm
    private int estimatedDuration; // Trvanie v hodinách
    private String assignedVehicle;
    private String status;


    public Delivery(String id, List<Package> packages, String deliveryDate, String deliveryStartTime, int estimatedDuration) {
        this.id = id;
        this.packages = packages;
        this.deliveryDate = deliveryDate;
        this.deliveryStartTime = deliveryStartTime;
        this.estimatedDuration = estimatedDuration;
        this.status = "Planned";
    }


    public String getId() {
        return id;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeliveryStartTime() {
        return deliveryStartTime;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public String getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(String assignedVehicle) {
        this.assignedVehicle = assignedVehicle;

    }

    public void setStatus(String Status) {
        this.status = Status;

    }

    public String getStatus(){return status;}
}