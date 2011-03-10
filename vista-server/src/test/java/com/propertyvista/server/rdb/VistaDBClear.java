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

import com.propertyvista.server.config.VistaServerSideConfiguration;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;

public class VistaDBClear {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println("Remove All data");
        VistaServerSideConfiguration conf = new VistaServerSideConfiguration();
        ServerSideConfiguration.setInstance(conf);
        System.out.println(conf.getDataPreloaders().delete());
        System.out.println("Total time: " + TimeUtils.secSince(start));
    }

}
