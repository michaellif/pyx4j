package com.pyx4j.entity.report.test;

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
 * Created on Mar 23, 2011
 * @author michaellif
 * @version $Id$
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class ReportsTestBase extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(ReportsTestBase.class);

    private XPath xPath;

    private Document document;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ByteArrayOutputStream bos = null;
        try {

            log.debug("Creating report {}", getDesignFileName());

            JasperReport jasperReport = JasperCompileManager.compileReport(getDesignFileName());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, getParameters(), getDataSource());

            bos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToXmlStream(jasperPrint, bos);
            bos.flush();

            log.debug(new String(bos.toByteArray()));

            document = parseXML(bos.toByteArray());
            XPathFactory xpathFactory = XPathFactory.newInstance();
            xPath = xpathFactory.newXPath();

        } finally {
            if (bos != null) {
                bos.close();
            }
        }

    }

    protected String evaluate(String expression) throws XPathExpressionException {
        return xPath.evaluate(expression, document);
    }

    private Document parseXML(byte[] xml) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setExpandEntityReferences(false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(xml));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected abstract String getDesignFileName();

    protected abstract Map<String, String> getParameters();

    protected abstract JRDataSource getDataSource();

}
