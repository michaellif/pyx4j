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
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(ReportsTestBase.class);

    private static XPath xPath;

    private static Document document;

    private static ArrayList<String> textItems = new ArrayList<String>();

    protected static void createReport(String designFileName, Map<String, String> parameters, JRDataSource dataSource) throws Exception {
        ByteArrayOutputStream bos = null;
        try {

            log.debug("Creating report {}", designFileName);

            JasperReport jasperReport = JasperCompileManager.compileReport(designFileName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, designFileName + ".pdf");

            bos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToXmlStream(jasperPrint, bos);
            bos.flush();

            log.debug(new String(bos.toByteArray()));

            document = parseXML(bos.toByteArray());
            XPathFactory xpathFactory = XPathFactory.newInstance();
            xPath = xpathFactory.newXPath();

            NodeList nodes = evaluate("/jasperPrint/page/text/textContent");
            for (int i = 0; i < nodes.getLength(); i++) {
                textItems.add(nodes.item(i).getTextContent());
            }

        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    protected static boolean containsText(String expression) {
        return textItems.contains(expression);
    }

    protected static NodeList evaluate(String expression) throws XPathExpressionException {
        return (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
    }

    private static Document parseXML(byte[] xml) throws SAXException, IOException, ParserConfigurationException {
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

}
