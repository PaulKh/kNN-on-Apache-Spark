package src.knn.utilities;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import src.knn.model.Point;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 21.02.2015.
 */
public class GeographicLocationSaxParser {
    public static void saxParser(){
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                public boolean value[]=new boolean[8];
                public int counter = 0;
                public void startElement(String uri, String localName,String qName,
                                         Attributes attributes) throws SAXException{
                    counter++;
                    if (qName.equalsIgnoreCase("node") && counter%10 == 0) {
                        System.out.print(Double.parseDouble(attributes.getValue("lat"))*10000 + "," + Double.parseDouble(attributes.getValue("lon"))*10000 + " ");
                        value[0] = true;
                    }
                }

                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {
                }

                public void characters(char ch[], int start, int length) throws SAXException {

                    if (value[0]) {
                        value[0] = false;
                    }
                }

            };

            saxParser.parse("D:\\Intellij Idea projects\\knn\\src\\main\\resources\\liechtenstein-latest.osm", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
