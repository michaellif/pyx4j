/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.onboarding.CheckAvailabilityRequestIO;
import com.propertyvista.onboarding.ResponseIO;

public class CheckAvailabilityRequestHandler extends AbstractRequestHandler<CheckAvailabilityRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(CheckAvailabilityRequestHandler.class);

    public CheckAvailabilityRequestHandler() {
        super(CheckAvailabilityRequestIO.class);
    }

    @Override
    public ResponseIO execute(CheckAvailabilityRequestIO request) {
        log.info("User {} requested {} for DNS name {}", new Object[] { request.onboardingAccountId().getValue(), "CheckAvailability",
                request.dnsName().getValue() });

        String dnsName = request.dnsName().getValue().toLowerCase(Locale.ENGLISH);
        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(PmcNameValidator.canCreatePmcName(dnsName, null));

        return response;
    }

}
