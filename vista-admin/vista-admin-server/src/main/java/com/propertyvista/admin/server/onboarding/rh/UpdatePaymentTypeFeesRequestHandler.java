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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UpdatePaymentTypeFeesRequestIO;

public class UpdatePaymentTypeFeesRequestHandler extends AbstractRequestHandler<UpdatePaymentTypeFeesRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(UpdatePaymentTypeFeesRequestHandler.class);

    public UpdatePaymentTypeFeesRequestHandler() {
        super(UpdatePaymentTypeFeesRequestIO.class);
    }

    @Override
    public ResponseIO execute(UpdatePaymentTypeFeesRequestIO request) {
        log.info("User {} requested {} ", new Object[] { request.onboardingAccountId().getValue(), "UpdatePaymentTypeFees" });

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        EntityQueryCriteria<Pmc> pmcCr = EntityQueryCriteria.create(Pmc.class);
        pmcCr.add(PropertyCriterion.eq(pmcCr.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        Pmc pmc = Persistence.service().retrieve(pmcCr);

        if (pmc == null) {
            log.debug("No Pmc for onboarding accountid {}", request.onboardingAccountId().getValue());
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        pmc.paymentTypeInfo().ccPaymentAvailable().setValue(request.ccPaymentAvailable().getValue());
        pmc.paymentTypeInfo().ccFee().setValue(request.ccFee().getValue());

        pmc.paymentTypeInfo().eCheckPaymentAvailable().setValue(request.echeckPaymentAvailable().getValue());
        pmc.paymentTypeInfo().eChequeFee().setValue(request.echeckFee().getValue());

        pmc.paymentTypeInfo().eftFee().setValue(request.etfFee().getValue());
        pmc.paymentTypeInfo().eftPaymentAvailable().setValue(request.etfPaymentAvailable().getValue());

        pmc.paymentTypeInfo().interacCaledonFee().setValue(request.interacCaledonFee().getValue());
        pmc.paymentTypeInfo().interacCaledonPaymentAvailable().setValue(request.interacCaledonPaymentAvailable().getValue());

        pmc.paymentTypeInfo().interacVisaFee().setValue(request.interacVisaFee().getValue());
        pmc.paymentTypeInfo().interacVisaPaymentAvailable().setValue(request.interacVisaPaymentAvailable().getValue());

        Persistence.service().persist(pmc);
        Persistence.service().commit();

        return response;
    }
}
