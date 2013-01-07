/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Date;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.admin.domain.legal.VistaTerms.Target;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.OnlinePaymentWizardService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.dto.OnlinePaymentSetupDTO;
import com.propertyvista.server.jobs.TaskRunner;

public class OnlinePaymentWizardServiceImpl implements OnlinePaymentWizardService {

    @Override
    public void create(AsyncCallback<OnlinePaymentSetupDTO> callback) {
        OnlinePaymentSetupDTO onlinePaymentSetup = EntityFactory.create(OnlinePaymentSetupDTO.class);

        // init property accounts
        onlinePaymentSetup.propertyAccounts().add(onlinePaymentSetup.propertyAccounts().$());

        // init default company legal name
        Pmc pmc = VistaDeployment.getCurrentPmc();
        onlinePaymentSetup.businessInformation().companyName().setValue(pmc.name().getValue());

        // init terms
        onlinePaymentSetup.caledonAgreement().set(retrieveCaledonTerms());
        onlinePaymentSetup.paymentPadAgreement().set(retrievePaymentPadTerms());

        // init signatures
        Date date = new Date();
        String ipAddress = Context.getRequestRemoteAddr();

        onlinePaymentSetup.caledonAgreementSignature().ipAddress().setValue(ipAddress);
        onlinePaymentSetup.caledonAgreementSignature().timestamp().setValue(date);
        onlinePaymentSetup.paymentPadAgreementSignature().ipAddress().setValue(ipAddress);
        onlinePaymentSetup.paymentPadAgreementSignature().timestamp().setValue(date);

        callback.onSuccess(onlinePaymentSetup);
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void finish(AsyncCallback<VoidSerializable> callback, OnlinePaymentSetupDTO editableEntity) {
        callback.onSuccess(null);
    }

    @Override
    public void obtainPaymentFees(AsyncCallback<AbstractPaymentFees> callback) {
        callback.onSuccess(EntityFactory.create(AbstractPaymentFees.class));
    }

    private LegalTermsContent retrieveCaledonTerms() {
        LegalTermsContent content = retrieveTerms(Target.PmcCaledonTemplate);
        content.localizedCaption().setValue("Merchant Processing Application Agreement & Pre-Authorized Debit Agreement");
        return content;
    }

    private LegalTermsContent retrievePaymentPadTerms() {
        LegalTermsContent content = retrieveTerms(Target.PmcPaymentPad);
        content.localizedCaption().setValue("Pre-Authorized Debit Agreement and Interac Online");
        return content;
    }

    private LegalTermsContent retrieveTerms(final VistaTerms.Target target) {
        String contentText = TaskRunner.runInAdminNamespace(new Callable<String>() {
            @Override
            public String call() throws Exception {
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                VistaTerms terms = Persistence.service().retrieve(criteria);
                return terms.version().document().get(0).content().getValue();
            }
        });

        LegalTermsContent content = EntityFactory.create(LegalTermsContent.class);
        content.content().setValue(contentText);
        return content;
    }
}
