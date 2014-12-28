package se.merryweather.callerlookup.app.Models;

public class DetailsModel {

    private String name = "";
    private String address = "";
    private Boolean result = false;

    public DetailsModel(String name, String address, Boolean result) {
        this.name = name;
        this.address = address;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Boolean haveResult() { return result; }
}
