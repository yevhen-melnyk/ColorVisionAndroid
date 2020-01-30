package com.example.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class CbType {
    int id;
    String name;
    String localName;
    String description;
    String colors;

    public CbType(int id, String name, String localName, String description, String colors) {
        this.id = id;
        this.name = name;
        this.localName = localName;
        this.description = description;
        this.colors = colors;
    }

    static public ArrayList<CbType> parseFromXml(InputStream resource){
        ArrayList<CbType> result = new ArrayList<>();

        try {
            //Parsing xml document
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse( resource);

            //Creating simple XPath for data location
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression typeExpression = xpath.compile("//resources/Type");

            //Getting list of nodes by the generated XPath from parsed xml document
            NodeList cbTypeNodes = (NodeList) typeExpression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < cbTypeNodes.getLength(); i++) {
                Node type = cbTypeNodes.item(i);
                NodeList childNodes = type.getChildNodes();
                //Creating a new object from data read from xml file
                result.add(new CbType(
                        Integer.parseInt(childNodes.item(1).getTextContent()),
                        childNodes.item(3).getTextContent(),
                        childNodes.item(5).getTextContent(),
                        childNodes.item(7).getTextContent(),
                        childNodes.item(9).getTextContent()
                ));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return this.localName;
    }
}
