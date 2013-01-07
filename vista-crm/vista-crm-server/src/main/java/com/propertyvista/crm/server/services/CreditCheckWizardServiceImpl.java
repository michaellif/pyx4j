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

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.CreditCheckWizardService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
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
    public void finish(AsyncCallback<VoidSerializable> callback, final CreditCheckSetupDTO dto) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        final AbstractEquifaxFee fee = ServerSideFactory.create(Vista2PmcFacade.class).getEquifaxFee();

        TaskRunner.runInAdminNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PmcPaymentMethod paymentMethod = ServerSideFactory.create(PaymentMethodFacade.class).persistPmcPaymentMethod(dto.creditCardInfo(), pmc);
                Persistence.service().retrieveMember(pmc.equifaxInfo());
                pmc.equifaxInfo().paymentMethod().set(paymentMethod);
                pmc.equifaxInfo().approved().setValue(false);
                pmc.equifaxInfo().reportType().setValue(dto.creditPricingOption().getValue());

                switch (dto.creditPricingOption().getValue()) {
                case FullCreditReport:
                    pmc.equifaxInfo().equifaxSignUpFee().setValue(fee.fullCreditReportSetUpFee().getValue());
                    pmc.equifaxInfo().equifaxPerApplicantCreditCheckFee().setValue(fee.fullCreditReportPerApplicantFee().getValue());
                    break;
                case RecomendationReport:
                    pmc.equifaxInfo().equifaxSignUpFee().setValue(fee.recommendationReportSetUpFee().getValue());
                    pmc.equifaxInfo().equifaxPerApplicantCreditCheckFee().setValue(fee.recommendationReportPerApplicantFee().getValue());
                    break;
                default:
                    throw new IllegalArgumentException();
                }

                Persistence.service().persist(pmc.equifaxInfo());
                return null;
            }
        });

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void obtatinFee(AsyncCallback<AbstractEquifaxFee> callback) {
        callback.onSuccess(ServerSideFactory.create(Vista2PmcFacade.class).getEquifaxFee());
    }

}
