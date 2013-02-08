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
package com.propertyvista.crm.server.services.vista2pmc;

import java.util.Date;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.admin.domain.legal.VistaTerms.Target;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.vista2pmc.OnlinePaymentWizardService;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.vista2pmc.AgreementDTO;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;
import com.propertyvista.server.jobs.TaskRunner;

public class OnlinePaymentWizardServiceImpl implements OnlinePaymentWizardService {

    @Override
    public void create(AsyncCallback<OnlinePaymentSetupDTO> callback) {
        OnlinePaymentSetupDTO onlinePaymentSetup = EntityFactory.create(OnlinePaymentSetupDTO.class);

        // init property accounts
        onlinePaymentSetup.propertyAccounts().add(onlinePaymentSetup.propertyAccounts().$());

        // init default company legal name        
        onlinePaymentSetup.businessInformation().companyName().setValue(VistaDeployment.getCurrentPmc().name().getValue());

        initTerms(onlinePaymentSetup.caledonAgreement(), retrieveTerms(Target.PmcCaledonTemplate));
        initTerms(onlinePaymentSetup.caledonSoleProprietorshipAgreement(), retrieveTerms(Target.PmcCaldedonSolePropetorshipSection));
        initTerms(onlinePaymentSetup.paymentPadAgreement(), retrieveTerms(Target.PmcPaymentPad));

        callback.onSuccess(onlinePaymentSetup);
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void finish(AsyncCallback<VoidSerializable> callback, OnlinePaymentSetupDTO editableEntity) {
        callback.onSuccess(null);

        // TODO get current user name and email, pass it to CommunicationFacade
        String userName = "";
        String userEmail = "";
        ServerSideFactory.create(CommunicationFacade.class).sendOnlinePaymentSetupCompletedEmail(userName, userEmail);
    }

    @Override
    public void obtainPaymentFees(AsyncCallback<AbstractPaymentFees> callback) {
        callback.onSuccess(EntityFactory.create(AbstractPaymentFees.class));
    }

    private String retrieveTerms(final VistaTerms.Target target) {
        String termsText = TaskRunner.runInAdminNamespace(new Callable<String>() {
            @Override
            public String call() throws Exception {
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                VistaTerms terms = Persistence.service().retrieve(criteria);
                return terms.version().document().get(0).content().getValue();
            }
        });

        return termsText;
    }

    private void initTerms(AgreementDTO agreement, String termsContent) {
        agreement.terms().setValue(termsContent);
        agreement.agreementSignature().timestamp().setValue(new Date());
        agreement.agreementSignature().ipAddress().setValue(Context.getRequestRemoteAddr());
    }
}
