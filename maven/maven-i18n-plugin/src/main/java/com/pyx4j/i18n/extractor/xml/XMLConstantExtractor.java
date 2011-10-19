/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.extractor.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.pyx4j.i18n.extractor.Extractor;

public class XMLConstantExtractor {

    private final Extractor extractor;

    public XMLConstantExtractor(Extractor extractor) {
        this.extractor = extractor;
    }

    public void readFile(InputStream in, final String name) throws IOException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance("org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl", this.getClass().getClassLoader());
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        InputSource is = new InputSource(new InputStreamReader(in, "UTF-8"));
        is.setEncoding("UTF-8");

        DefaultHandler handler = new DefaultHandler() {

            private Locator locator = null;

            @Override
            public void setDocumentLocator(Locator locator) {
                super.setDocumentLocator(locator);
                this.locator = locator;
            }

            private boolean i18nTag = false;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.endsWith(":i18n")) {
                    i18nTag = true;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                i18nTag = false;
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (i18nTag) {
                    System.out.println(new String(ch, start, length));
                    extractor.addEntry(name, locator.getLineNumber(), new String(ch, start, length), false);
                }
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                throw new SAXException(e.getMessage() + " on line " + locator.getLineNumber(), e);
            }
        };

        saxParser.parse(is, handler);
    }
}
