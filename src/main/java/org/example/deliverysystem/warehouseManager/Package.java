package org.example.deliverysystem.warehouseManager;

import java.util.UUID;

public class Package {
    private String id;
    private String status;
    private String deliveryAddress;
    private double weight;
    private String senderName;
    private String recipientContact;
    private String warehouseLocation;
    private String UId;

    public Package(String senderId, String status, String deliveryAddress, double weight, String senderName, String recipientContact, String warehouseLocation) {
        this.id = UUID.randomUUID().toString();;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.weight = weight;
        this.senderName = senderName;
        this.recipientContact = recipientContact;
        this.warehouseLocation = warehouseLocation;
        this.UId = senderId;
    }

    // Gettery a settery
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientContact() {
        return recipientContact;
    }

    public void setRecipientContact(String recipientContact) {
        this.recipientContact = recipientContact;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", weight=" + weight +
                ", senderName='" + senderName + '\'' +
                ", recipientContact='" + recipientContact + '\'' +
                ", warehouseLocation='" + warehouseLocation + '\'' +
                '}';
    }

    public String getSenderUID() {
        return UId;
    }
    public String getID() {
        return id;
    }
}