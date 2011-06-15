/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.rdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.server.config.VistaNamespaceResolver;
import com.propertyvista.server.config.VistaServerSideConfiguration;

public class VistaDBClear {

    private static final Logger log = LoggerFactory.getLogger(VistaDBPreload.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        log.info("Remove All data");
        SchedulerHelper.dbReset();
        VistaServerSideConfiguration conf = new VistaServerSideConfiguration();
        ServerSideConfiguration.setInstance(conf);
        NamespaceManager.setNamespace(VistaNamespaceResolver.demoNamespace);
        log.info(conf.getDataPreloaders().delete());
        log.info("Total time: " + TimeUtils.secSince(start));
    }

}
