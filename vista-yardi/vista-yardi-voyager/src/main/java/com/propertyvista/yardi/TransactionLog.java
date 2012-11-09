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
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.log4j.LoggerConfig;

public class TransactionLog {

    private final static Logger log = LoggerFactory.getLogger(TransactionLog.class);

    public static void log(Long transactionId, String contextName, String context) {
        log(transactionId, contextName, context, "log");
    }

    public static void log(Long transactionId, String contextName, String context, String fileExt) {
        if (transactionId == null) {
            return;
        }
        FileWriter writer = null;
        try {
            File dir;
            if (LoggerConfig.getContextName() != null) {
                dir = new File("logs", LoggerConfig.getContextName());
            } else {
                dir = new File("logs");
            }
            dir = new File(dir, "yardi-transactions");
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
            writer = new FileWriter(out);
            writer.write(context);
        } catch (Throwable t) {
            log.error("failed to create transaction log", t);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
