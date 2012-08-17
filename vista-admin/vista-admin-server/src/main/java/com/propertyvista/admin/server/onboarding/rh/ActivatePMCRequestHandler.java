/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.deferred.DeferredProcessRegistry;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.biz.system.PmcActivationDeferredProcess;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.onboarding.ActivatePMCRequestIO;
import com.propertyvista.onboarding.ResponseIO;

public class ActivatePMCRequestHandler extends AbstractRequestHandler<ActivatePMCRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(ActivatePMCRequestHandler.class);

    public ActivatePMCRequestHandler() {
        super(ActivatePMCRequestIO.class);
    }

    @Override
    public ResponseIO execute(ActivatePMCRequestIO request) {
        log.info("User {} requested {} for PMC activation {}", new Object[] { request.onboardingAccountId().getValue(), "ActivatePmc" });

        ResponseIO response = EntityFactory.create(ResponseIO.class);

        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        Pmc pmc = Persistence.service().retrieve(criteria);

        if (pmc == null) {
            log.debug("No Pmc for onboarding accountid {}", request.onboardingAccountId().getValue());
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        switch (pmc.status().getValue()) {
        case Created:
            DeferredProcessRegistry.fork(new PmcActivationDeferredProcess(pmc), ThreadPoolNames.IMPORTS);
            response.success().setValue(Boolean.TRUE);
            break;
        case Active:
            response.success().setValue(Boolean.TRUE);
            break;
        default:
            response.success().setValue(Boolean.FALSE);
        }
        return response;
    }

}
