/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.pyx4j.essentials.j2se.util.FileIOUtils;
import com.pyx4j.essentials.server.dev.NumberInFile;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.config.VistaDeployment;

public class TransactionLog {

    private final static Logger log = LoggerFactory.getLogger(TransactionLog.class);

    private static NumberInFile transactionsCountId = new NumberInFile(logsDir());

    public static long getNextNumber() {
        return transactionsCountId.getNextNumber();
    }

    private static File logsDir() {
        File dir;
        if (LoggerConfig.getContextName() != null) {
            dir = new File("logs", LoggerConfig.getContextName());
        } else {
            dir = new File("logs");
        }
        if (VistaDeployment.isVistaStaging()) {
            return new File(dir, "yardi-transactions-staging");
        } else {
            return new File(dir, "yardi-transactions");
        }

    }

    private static String prettyXmlFormat(String input) {
        try {

            InputSource src = new InputSource(new StringReader(input));
            Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", true);

            // this by default uses UTF-16 to convert document to String with corresponding encoding in the xml declaration
            // <?xml version="1.0" encoding="UTF-16"?>
            return writer.writeToString(document);
        } catch (Throwable e) {
            log.warn("unable to format xml", e);
            return input;
        }
    }

    public static String log(Long transactionId, String contextName, String context, String fileExt) {
        if (transactionId == null) {
            return null;
        }
        try {
            File dir = logsDir();
            long transactionGroup = transactionId / 1000;
            NumberFormat gnf = new DecimalFormat("0000");
            dir = new File(dir, gnf.format(transactionGroup) + "000-" + gnf.format(transactionGroup) + "999");
            FileUtils.forceMkdir(dir);

            NumberFormat nf = new DecimalFormat("000000");
            StringBuffer fname = new StringBuffer(nf.format(transactionId));
            fname.append('-').append(contextName);

            File out = new File(dir, fname.toString() + "." + fileExt);
            int repeat = 0;
            while (out.exists()) {
                out = new File(dir, fname.toString() + "-" + (++repeat) + "." + fileExt);
            }
            FileIOUtils.writeToFile(out, prettyXmlFormat(context), StandardCharsets.UTF_16);
            return out.getAbsolutePath();
        } catch (Throwable t) {
            log.error("failed to create transaction log", t);
            return null;
        }
    }
}
