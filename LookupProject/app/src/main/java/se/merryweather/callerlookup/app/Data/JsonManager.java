package se.merryweather.callerlookup.app.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;

import se.merryweather.callerlookup.app.Models.CompanyModel;

public class JsonManager {

    public CompanyModel readJsonStream(InputStream inputStream) throws IOException {

        try {
            String jsonResult = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            jsonResult = sb.toString();
            JSONObject jsonObject = new JSONObject(jsonResult);
            return readCompany(jsonObject);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private CompanyModel readCompany(JSONObject json) {
        CompanyModel companyModel = new CompanyModel();

        try {
            JSONArray element = json.getJSONArray("adverts");
            for (int i = 0; i < element.length(); i++) {
                JSONObject advert = element.getJSONObject(i);

                JSONObject companyInfo = advert.getJSONObject("companyInfo");
                companyModel.setCompanyName(companyInfo.getString("companyName"));

                JSONObject address = advert.getJSONObject("address");
                companyModel.setAddress(address.getString("streetName"));
                companyModel.setZipCode(address.getString("postCode"));
                companyModel.setCity(address.getString("postArea"));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        return companyModel;
    }

    private void readAddress() {

    }
}