package org.example.deliverysystem.request_manager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomRequest {
    private static final AtomicInteger idCounter = new AtomicInteger(0); // Generátor ID
    private final int id; // Unikátne ID pre každý request
    private String senderName;
    private String senderAddress;
    private String senderContact;
    private List<Recipient> recipients;
    private boolean isPaid; // Indikuje, či je request zaplatený
    private double totalAmount; // Celková suma na úhradu
    public CustomRequest(String senderName, String senderAddress, String senderContact, List<Recipient> recipients) {
        this.id = idCounter.incrementAndGet(); // Automatické generovanie ID
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.senderContact = senderContact;
        this.recipients = recipients;
        this.isPaid = false; // Defaultná hodnota
        calculateTotalAmount(); // Vypočíta celkovú sumu
    }

    private void calculateTotalAmount() {
        this.totalAmount = recipients.stream()
                .mapToDouble(Recipient::getPackageWeight)
                .sum() * 5; // Cena za kilogram
    }

    public int getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderContact() {
        return senderContact;
    }

    public void setSenderContact(String senderContact) {
        this.senderContact = senderContact;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
        calculateTotalAmount(); // Aktualizuje celkovú sumu pri zmene recipientov
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTotalWeight() {
        return recipients.stream()
                .mapToDouble(Recipient::getPackageWeight)
                .sum();
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", senderContact='" + senderContact + '\'' +
                ", recipients=" + recipients +
                ", isPaid=" + isPaid +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
