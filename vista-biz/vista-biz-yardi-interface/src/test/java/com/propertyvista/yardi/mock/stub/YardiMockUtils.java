/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 27, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.stub;

import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.yardi.TransactionLog;

public class YardiMockUtils {

    private final static Logger log = LoggerFactory.getLogger(YardiMockUtils.class);

    public static <T> T dumpXml(String contextName, T data) {
        try {
            String name = TransactionLog.log(TransactionLog.getNextNumber(),
                    contextName + "_" + new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(SystemDateManager.getDate()), MarshallUtil.marshall(data), "xml");
            log.debug("log file created {}", name);
        } catch (JAXBException e) {
            log.error("writing data dump error", e);
        }
        return data;
    }

}
