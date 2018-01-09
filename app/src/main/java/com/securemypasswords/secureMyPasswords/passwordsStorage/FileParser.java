package com.securemypasswords.secureMyPasswords.passwordsStorage;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

public class FileParser {

    private static final String xmlGroup = "group";
    private static final String xmlPassword = "password";

    public FileParser() {

    }

    public String objectToXml(ArrayList<AppElements> elements) throws IOException {
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<Password> passwords = new ArrayList<>();
        for(AppElements appElements : elements){
            if(appElements instanceof Group){
                groups.add((Group) appElements);
            }else if(appElements instanceof Password){
                passwords.add((Password) appElements);
            }
        }

        return objectToXml(groups.toArray(new Group[0]),passwords.toArray(new Password[0]));
    }

    public String objectToXml(Group[] groups, Password[] passwords) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);

        if(groups != null) {
            for (Group group : groups) {
                groupToXml(xmlSerializer, group);
            }
        }
        if(passwords != null){
            for(Password password:passwords){
                passwordToXml(xmlSerializer, password);
            }
        }

        xmlSerializer.endDocument();
        xmlSerializer.flush();
        return writer.toString();
    }

    private void groupToXml(XmlSerializer xmlSerializer, Group group) throws IOException {
        xmlSerializer.startTag(null, xmlGroup);
        xmlSerializer.attribute(null,"name",group.getName());
        Group[] tempGroups = group.getGroups();
        if(tempGroups !=null) {
            for (Group tempGroup : tempGroups) {
                groupToXml(xmlSerializer, tempGroup);
            }
        }
        Password[] tempPasswords = group.getPasswords();
        if(tempPasswords !=null) {
            for (Password password : tempPasswords) {
                passwordToXml(xmlSerializer, password);
            }
        }
        xmlSerializer.endTag(null, xmlGroup);
    }

    private void passwordToXml(XmlSerializer xmlSerializer, Password password) throws IOException {
        xmlSerializer.startTag(null, xmlPassword);
        xmlSerializer.attribute(null, "name", password.getName());
        xmlSerializer.attribute(null, "url", password.getUrl());
        xmlSerializer.attribute(null, "user_name", password.getUserName());
        xmlSerializer.attribute(null, "password", password.getPassword());
        xmlSerializer.attribute(null, "note", password.getNote());
        xmlSerializer.endTag(null, xmlPassword);
    }

    public ArrayList<AppElements> xmlToObject(String xmlFile) throws XmlPullParserException, IOException {
        ArrayList<AppElements> elements = new ArrayList<>();

        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        xmlFactoryObject.setNamespaceAware(true);
        XmlPullParser xmlParser = xmlFactoryObject.newPullParser();
        xmlParser.setInput(new StringReader(xmlFile));
        int eventType = xmlParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            if (eventType == XmlPullParser.START_TAG) {
                if(xmlParser.getName().equals(xmlGroup)){
                    Group tempGroup = new Group(xmlParser.getAttributeValue(null, "name"));
                    elements.add(tempGroup);
                    if(!xmlParser.isEmptyElementTag()){
                        groupToObject(tempGroup, xmlParser);
                    }
                }else if( xmlParser.getName().equals(xmlPassword)){
                    String name = xmlParser.getAttributeValue(null,"name");
                    String url = xmlParser.getAttributeValue(null,"url");
                    String userName = xmlParser.getAttributeValue(null,"user_name");
                    String password = xmlParser.getAttributeValue(null, "password");
                    String note = xmlParser.getAttributeValue(null,"note");
                    elements.add(new Password(password,name,url,userName,note));
                }
            }
            eventType = xmlParser.next();
        }
        return elements;
    }

    private void groupToObject(Group group, XmlPullParser xpp) throws XmlPullParserException, IOException {
        int eventType = xpp.next();
        while (eventType != XmlPullParser.END_TAG || xpp.getName().equals(xmlPassword)){
            if (eventType == XmlPullParser.START_TAG) {
                if(xpp.getName().equals(xmlGroup)){
                    String name = xpp.getAttributeValue(null, "name");
                    Group tempGroup = new Group(name);
                    group.addGroup(tempGroup);
                    if(!xpp.isEmptyElementTag()){
                        groupToObject(tempGroup, xpp);
                    }else{
                        xpp.next();
                    }
                }else if( xpp.getName().equals(xmlPassword)){
                    String name = xpp.getAttributeValue(null,"name");
                    String url = xpp.getAttributeValue(null,"url");
                    String userName = xpp.getAttributeValue(null,"user_name");
                    String password = xpp.getAttributeValue(null, "password");
                    String note = xpp.getAttributeValue(null,"note");
                    group.addPassword(new Password(password,name,url,userName,note));

                }
            }
            eventType = xpp.next();
        }
    }

}
