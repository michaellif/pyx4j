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
package com.propertyvista.admin.server.onboarding.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.onboarding.CancelPMCRequestIO;
import com.propertyvista.onboarding.ResponseIO;

public class CancelPMCRequestHandler extends AbstractRequestHandler<CancelPMCRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(CancelPMCRequestHandler.class);

    public CancelPMCRequestHandler() {
        super(CancelPMCRequestIO.class);
    }

    @Override
    public ResponseIO execute(CancelPMCRequestIO request) {
        log.info("User {} {} requested {} ", new Object[] { request.onboardingAccountId().getValue(), request.email().getValue(), "CancelPMC" });

        ResponseIO response = EntityFactory.create(ResponseIO.class);

        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        Pmc pmc = Persistence.service().retrieve(criteria);
        if (pmc == null) {
            log.debug("No Pmc for on-boarding accountId {}", request.onboardingAccountId().getValue());
            response.success().setValue(Boolean.FALSE);
            return response;
        } else {

            ServerSideFactory.create(PmcFacade.class).cancelPmc(pmc);
            Persistence.service().commit();
            CacheService.reset();

            ServerSideFactory.create(AuditFacade.class).info("PMC {0} Cancelled by {1} account {2}", pmc.namespace().getValue(), request.email().getValue(),
                    request.onboardingAccountId().getValue());

            response.success().setValue(Boolean.TRUE);
            return response;
        }
    }
}
