package se.merryweather.callerlookup.app.Models;

public class PersonModel {

    private String firstName = "";
    private String lastName  = "";
    private String address  = "";
    private String zipCode  = "";
    private String city  = "";
    private String telephone = "";

    public PersonModel() {}

    public PersonModel(String firstName, String lastName, String address, String zipCode, String city, String telephone ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.telephone = telephone;
    }

    public String getName() {
        if(firstName == "" && lastName == "") {
            return null;
        }
        return String.format("%s %s", firstName, lastName);
    }

    public String getAddress() {
        return String.format("%s, %s %s", address, zipCode, city);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
