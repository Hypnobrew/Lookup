package se.merryweather.callerlookup.app.Data;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;

import se.merryweather.callerlookup.app.Models.PersonModel;

public class XmlManager {

    private PersonModel personModel = new PersonModel();
    private static final String ns = null;

    public PersonModel parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private PersonModel readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "queryResponse");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("response")) {
                readPersonHits(parser);
            } else {
                skip(parser);
            }
        }
        return personModel;
    }

    private void readPersonHits(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "response");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("personHits")) {
                readPerson(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readPerson(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "personHits");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("person")) {
                readInd(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readInd(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "person");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("individual")) {
                readIndividual(parser);
            }
            else if(name.equals("address")) {
                readAddress(parser);
            }
            else {
                skip(parser);
            }
        }
    }

    private void readIndividual(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "individual");
        String tempName = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("name")) {
                if(tempName != "") {
                    tempName += " ";
                }
                tempName +=readTag(parser, "name");
            }
            else {
                skip(parser);
            }
        }
        personModel.setFirstName(tempName);
    }

    private void readAddress(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "address");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("street")) {
                personModel.setAddress(readAdressDetails(parser));
            }
            else if(name.equals("zip")) {
                personModel.setZipCode(readTag(parser, "zip"));
            }
            else if(name.equals("city")) {
                personModel.setCity(readTag(parser, "city"));
            }
            else {
                skip(parser);
            }
        }
    }

    private String readAdressDetails(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "street");
        String address = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("name")) {
                address += readTag(parser, "name");
            }
            else if(name.equals("number")) {
                address += " " + readTag(parser, "number");
            }
            else {
                skip(parser);
            }
        }
        return address;
    }

    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String first = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return first;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
