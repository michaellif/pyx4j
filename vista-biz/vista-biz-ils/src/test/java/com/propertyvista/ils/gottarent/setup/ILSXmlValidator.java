/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 17, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.setup;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ILSXmlValidator extends DefaultHandler {
    private static final String[] USAGE = {
            "The program validates an XML file against an XSD file.",
            "Usage: ",
            "/xml <xml file path>",
            "/xsd <xsd file path>",
            "For example:",
            "/xsd \"C:/projects/ils/propertyvista/vista-ils/vista-ils-feed-gen/src/main/resources/xsd/gottarent/ILS_Gottarent.xsd\" /xml \"C:/tmp/0034-Gottarent-String.log\"",
            "" };

    public static void main(String[] args) {
        String xmlPath = null, xsdPath = null;
        if (args == null || args.length != 4) {
            printUsage();
            return;
        }
        int i = 0;
        int argslen = args.length;
        while (i < argslen) {
            String a = args[i];

            if (a.equalsIgnoreCase("/xml")) {
                if (i + 1 >= argslen) {
                    System.out.println("No xml file has been provided.\n");
                    printUsage();
                    return;
                }
                xmlPath = args[i + 1];
            }
            if (a.equalsIgnoreCase("/xsd")) {
                if (i + 1 >= argslen) {
                    System.out.println("No xsd file has been provided.\n");
                    printUsage();
                    return;
                }
                xsdPath = args[i + 1];
            }
            i += 2;
        }
        if (xmlPath == null) {
            System.out.println("No xml file has been provided.\n");
            printUsage();
        }
        if (xsdPath == null) {
            System.out.println("No xsd file has been provided.\n");
            printUsage();
        }
        File schemaFile = new File(xsdPath);
        if (!schemaFile.exists()) {
            System.out.println("Xsd file does not exists.\n");
            printUsage();
        }

        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists()) {
            System.out.println("Xsd file does not exists.\n");
            printUsage();
        }

        check(xmlFile, schemaFile);
    }

    private static void check(File xmlFile, File schemaFile) {
        Source schemaSource = new StreamSource(schemaFile);
        Source xmlSource = new StreamSource(xmlFile);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
            System.out.println(xmlSource.getSystemId() + " is valid");
        } catch (SAXException e) {
            System.out.println(xmlSource.getSystemId() + " is NOT valid");
            System.out.println("Reason: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println(xmlSource.getSystemId() + " is NOT valid");
            System.out.println("Reason: " + e.getLocalizedMessage());
        }
    }

    private static void printUsage() {
        for (final String usage : USAGE) {
            System.out.println(usage);
        }
    }
}
