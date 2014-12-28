package se.merryweather.callerlookup.app.Models;

public class CompanyModel {

    private String companyName = "";
    private String address  = "";
    private String zipCode  = "";
    private String city  = "";
    private String telephone = "";

    public CompanyModel() {}

    public CompanyModel(String companyName, String address, String zipCode, String city, String telephone ) {
        this.companyName = companyName;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.telephone = telephone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return String.format("%s, %s %s", address, zipCode, city);
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
