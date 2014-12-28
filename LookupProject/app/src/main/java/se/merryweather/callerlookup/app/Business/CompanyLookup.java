package se.merryweather.callerlookup.app.Business;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import se.merryweather.callerlookup.app.Data.JsonManager;
import se.merryweather.callerlookup.app.Interfaces.IOnTaskCompleted;
import se.merryweather.callerlookup.app.Models.CompanyModel;
import se.merryweather.callerlookup.app.Models.DetailsModel;
import se.merryweather.callerlookup.app.R;

public class CompanyLookup extends AsyncTask<Object, Void, Object> {
    private Context mContext;
    private IOnTaskCompleted listener;

    public CompanyLookup(Context context, IOnTaskCompleted listener) {
        super();
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Object personData) {
        this.listener.onTaskCompleted(personData);
    }

    @Override
    protected Object doInBackground(Object... params) {

        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            String telephoneNumber = params[0].toString();
            String key = mContext.getString(R.string.api_eniro_key_e12b909b8576413e8f9ad6e6f23c23ce);
            String profile = mContext.getString(R.string.api_eniro_profile_ee8aa20dba1b40a7b77ebe299384bf33);
            request.setURI(new URI(String.format("http://api.eniro.com/cs/search/basic?profile=%s&key=%s&country=se&version=1.1.3&search_word=%s", profile, key, telephoneNumber)));

            response = client.execute(request);
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        catch (ClientProtocolException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        if(response != null) {
            try {
                JsonManager jsonManager = new JsonManager();
                CompanyModel company = jsonManager.readJsonStream(response.getEntity().getContent());
                if(company.getCompanyName().trim() != "") {
                    return new DetailsModel(company.getCompanyName(), company.getAddress(), true);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new DetailsModel("", "", false);
    }
}
