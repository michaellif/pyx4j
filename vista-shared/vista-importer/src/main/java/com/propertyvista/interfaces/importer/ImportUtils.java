/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.xml.XMLEntityParser;

import com.propertyvista.interfaces.importer.xml.ImportXMLEntityFactory;

public class ImportUtils {

    public static <T extends IEntity> T parse(Class<T> entityClass, InputSource input) {
        XMLEntityParser parser = new XMLEntityParser(new ImportXMLEntityFactory());
        return parser.parse(entityClass, newDocument(input).getDocumentElement());
    }

    public static Document newDocument(InputSource input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
        builder.setErrorHandler(null);
        try {
            return builder.parse(input);
        } catch (SAXException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
