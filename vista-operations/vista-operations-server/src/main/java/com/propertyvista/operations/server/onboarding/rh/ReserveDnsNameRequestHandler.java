/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.onboarding.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.operations.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.onboarding.ReserveDnsNameRequestIO;
import com.propertyvista.onboarding.ResponseIO;

public class ReserveDnsNameRequestHandler extends AbstractRequestHandler<ReserveDnsNameRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(ReserveDnsNameRequestHandler.class);

    public ReserveDnsNameRequestHandler() {
        super(ReserveDnsNameRequestIO.class);
    }

    @Override
    public ResponseIO execute(ReserveDnsNameRequestIO request) {
        log.info("User {} requested {} for DNS name {}", new Object[] { request.onboardingAccountId().getValue(), "ReserveDnsName",
                request.dnsName().getValue() });

        ResponseIO response = EntityFactory.create(ResponseIO.class);

        if (ServerSideFactory.create(PmcFacade.class).reservedDnsName(request.dnsName().getValue(), request.onboardingAccountId().getValue())) {
            response.success().setValue(Boolean.TRUE);
            Persistence.service().commit();
        } else {
            response.success().setValue(Boolean.FALSE);
        }

        return response;

    }
}
