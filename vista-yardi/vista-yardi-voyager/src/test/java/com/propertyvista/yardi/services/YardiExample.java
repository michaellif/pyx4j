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
package com.propertyvista.yardi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.server.config.DevYardiCredentials;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.bean.Properties;

public class YardiExample {

    private final static Logger log = LoggerFactory.getLogger(YardiExample.class);

    public static void main(String[] args) {
        YardiClient c = ServerSideFactory.create(YardiClient.class);
        c.setPmcYardiCredential(DevYardiCredentials.getTestPmcYardiCredential());
        try {
            Properties properties = YardiResidentTransactionsService.getInstance()
                    .getPropertyConfigurations(c, DevYardiCredentials.getTestPmcYardiCredential());
            System.out.println("+++++++++++++" + properties);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }
}
