/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.payment.caledon.CaledonRequest;
import com.propertyvista.payment.caledon.CaledonResponse;

public class CardServiceSimulationProcessor {

    private static final Logger log = LoggerFactory.getLogger(CardServiceSimulationProcessor.class);

    public static CaledonResponse execute(CaledonRequest caledonRequest) {
        CaledonResponse caledonResponse = new CaledonResponse();

        caledonResponse.code = "1106";
        caledonResponse.text = "Simulator";

        return caledonResponse;
    }
}
