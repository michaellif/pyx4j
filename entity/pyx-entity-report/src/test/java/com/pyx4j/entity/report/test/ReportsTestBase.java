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
 */
package com.pyx4j.entity.report.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportFactory;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.gwt.server.IOUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public abstract class ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(ReportsTestBase.class);

    private XPath xPath;

    private Document document;

    protected ArrayList<String> textItems = new ArrayList<String>();

    protected static File debugFileName(String designName, String ext) {
        File dir = new File("target", "reports-dump");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Error("Can't create directory " + dir.getAbsolutePath());
            }
        }
        String uniqueId = new SimpleDateFormat("_MM-dd_HH-mm-ss").format(new Date());
        File file = new File(dir, designName + uniqueId + ext);
        if (file.exists()) {
            if (!file.delete()) {
                throw new Error("Can't delete file " + file.getAbsolutePath());
            }
        }
        return file;
    }

    @Deprecated
    protected void createReport(String designName, Map<String, Object> parameters, JRDataSource dataSource) throws Exception {
        ByteArrayOutputStream bos = null;
        try {
            JasperReport jasperReport = JasperReportFactory.create(designName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, debugFileName(designName, ".pdf").getAbsolutePath());

            bos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToXmlStream(jasperPrint, bos);
            bos.flush();

            log.debug("xml {}", new String(bos.toByteArray()));

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

    protected void createReport(JasperReportModel model) throws Exception {
        ByteArrayOutputStream bos = null;
        FileOutputStream pdf = null;
        String pdfName = null;
        try {

            pdf = new FileOutputStream(pdfName = debugFileName(model.getDesignName(), ".pdf").getAbsolutePath());
            JasperReportProcessor.createReport(model, JasperFileFormat.PDF, pdf);
            pdf.flush();

            bos = new ByteArrayOutputStream();
            JasperReportProcessor.createReport(model, JasperFileFormat.XML, bos);
            bos.flush();

            log.debug("xml {}", new String(bos.toByteArray()));

            document = parseXML(bos.toByteArray());
            XPathFactory xpathFactory = XPathFactory.newInstance();
            xPath = xpathFactory.newXPath();

            NodeList nodes = evaluate("/jasperPrint/page/text/textContent");
            for (int i = 0; i < nodes.getLength(); i++) {
                textItems.add(nodes.item(i).getTextContent());
            }

        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(pdf);
        }

        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            String[] command = { "cmd.exe", "/c", "start", pdfName };
            Runtime.getRuntime().exec(command);
        }
    }

    protected boolean containsText(String expression) {
        return textItems.contains(expression);
    }

    protected NodeList evaluate(String expression) throws XPathExpressionException {
        return (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
    }

    private Document parseXML(byte[] xml) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setExpandEntityReferences(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(xml));
    }

}
