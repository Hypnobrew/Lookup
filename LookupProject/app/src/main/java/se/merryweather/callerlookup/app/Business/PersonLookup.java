package se.merryweather.callerlookup.app.Business;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import se.merryweather.callerlookup.app.Data.XmlManager;
import se.merryweather.callerlookup.app.Interfaces.IOnTaskCompleted;
import se.merryweather.callerlookup.app.Models.DetailsModel;
import se.merryweather.callerlookup.app.Models.PersonModel;
import se.merryweather.callerlookup.app.R;

public class PersonLookup extends AsyncTask<Object, Void, Object> {
    private Context mContext;
    private IOnTaskCompleted listener;

    public PersonLookup(Context context, IOnTaskCompleted listener) {
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
            String key = mContext.getString(R.string.api_118100_key_8cfc6258473a469d8301edcbbc216e89);

            request.setURI(new URI(String.format("http://developer.118100.se:8080/openapi-1.1/appetizing?query=%s&pageSize=10&key=%s", telephoneNumber, key)));
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
                XmlManager xmlManager = new XmlManager();
                PersonModel person = xmlManager.parse(response.getEntity().getContent());
                if(person.getName() != null) {
                    return new DetailsModel(person.getName(), person.getAddress(), true);
                }
            }
            catch(XmlPullParserException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new DetailsModel("", "", false);
    }
}
