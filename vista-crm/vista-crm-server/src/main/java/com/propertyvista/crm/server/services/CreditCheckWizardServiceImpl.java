/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 14, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.CreditCheckWizardService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.dto.CreditCheckSetupDTO;
import com.propertyvista.server.jobs.TaskRunner;

public class CreditCheckWizardServiceImpl implements CreditCheckWizardService {

    @Override
    public void create(AsyncCallback<CreditCheckSetupDTO> callback) {
        CreditCheckSetupDTO creditCheck = EntityFactory.create(CreditCheckSetupDTO.class);
        Pmc pmc = VistaDeployment.getCurrentPmc();
        creditCheck.businessInformation().companyName().setValue(pmc.name().getValue());
        callback.onSuccess(creditCheck);
    }

    @Override
    public void finish(AsyncCallback<VoidSerializable> callback, final CreditCheckSetupDTO entity) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        TaskRunner.runInAdminNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                ServerSideFactory.create(PaymentMethodFacade.class).persistPmcPaymentMethod(entity.creditCardInfo(), pmc);
                return null;
            }
        });

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void obtatinFee(AsyncCallback<AbstractEquifaxFee> callback) {
        AbstractEquifaxFee p = EntityFactory.create(AbstractEquifaxFee.class);
        p.recommendationReportPerApplicantFee().setValue(new BigDecimal("19.99"));
        p.recommendationReportSetUpFee().setValue(BigDecimal.ZERO);
        p.fullCreditReportPerApplicantFee().setValue(new BigDecimal("19.99"));
        p.fullCreditReportSetUpFee().setValue(new BigDecimal("150.00"));

        callback.onSuccess(p);

    }

}
