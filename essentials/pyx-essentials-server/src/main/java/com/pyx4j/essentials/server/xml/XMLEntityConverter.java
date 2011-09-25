/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-12-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.server.IOUtils;

public class XMLEntityConverter {

    public static void write(XMLStringWriter xml, IEntity entity) {
        XMLEntityWriter writer = new XMLEntityWriter(xml);
        writer.setEmitOnlyOwnedReferences(true);
        writer.writeRoot(entity, null);
    }

    public static IEntity parse(Element node) {
        return new XMLEntityParser().parse(node);
    }

    public static IEntity parse(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(null);
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        return parse(doc.getDocumentElement());
    }

    public static void writeFile(IEntity entity, File file, boolean emitId) {
        FileWriter w = null;
        try {
            w = new FileWriter(file);
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml);
            xmlWriter.setEmitId(emitId);
            xmlWriter.write(entity);
            w.write(xml.toString());
            w.flush();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(w);
        }
    }

    public static <T extends IEntity> T readFile(Class<T> entityClass, File file) {
        Reader in = null;
        try {
            return parse(entityClass, new InputSource(in = new FileReader(file)), new XMLEntityFactoryDefault());
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static <T extends IEntity> T parse(Class<T> entityClass, InputSource input, XMLEntityFactory factory) {
        XMLEntityParser parser = new XMLEntityParser(factory);
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
