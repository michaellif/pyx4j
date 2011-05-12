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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.ws.operations.Ping;
import com.yardi.ws.operations.PingResponse;

public class Example {

    private final static Logger log = LoggerFactory.getLogger(Example.class);

    public static void main(String[] args) {
        YardiClient c = new YardiClient();

        try {
            Ping ping = new Ping();
            PingResponse pr = c.getResidentTransactionsService().ping(ping);
            log.info("result [{}]", pr.getPingResult());
        } catch (Throwable e) {
            log.error("error", e);
        }
    }
}
