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
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.server.config.VistaServerSideConfiguration;

public class VistaDBDropForeignKeys {

    private static final Logger log = LoggerFactory.getLogger(VistaDBDropForeignKeys.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        log.info("Remove All ForeignKeys");
        VistaServerSideConfiguration conf = new VistaServerSideConfiguration();
        ServerSideConfiguration.setInstance(conf);
        Persistence.service().startBackgroundProcessTransaction();
        try {
            RDBUtils.dropAllForeignKeys();
        } finally {
            Persistence.service().endTransaction();
        }
        log.info("Total time: " + TimeUtils.secSince(start));
    }

}
