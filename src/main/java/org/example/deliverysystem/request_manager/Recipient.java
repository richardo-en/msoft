package org.example.deliverysystem.request_manager;

public class Recipient {
    private String name;
    private String address;
    private String contact;
    private String packageContent;
    private double packageWeight;

    public Recipient(String name, String address, String contact, String packageContent, double packageWeight) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.packageContent = packageContent;
        this.packageWeight = packageWeight;
    }

    // Gettery a settery
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(String packageContent) {
        this.packageContent = packageContent;
    }

    public double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public String getContactInfo() {
        return contact;
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contact='" + contact + '\'' +
                ", packageContent='" + packageContent + '\'' +
                ", packageWeight=" + packageWeight +
                '}';
    }
}