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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.OnlinePaymentWizardService;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.OnlinePaymentSetupDTO;

public class OnlinePaymentWizardServiceImpl implements OnlinePaymentWizardService {

    @Override
    public void create(AsyncCallback<OnlinePaymentSetupDTO> callback) {
        OnlinePaymentSetupDTO onlinePaymentSetup = EntityFactory.create(OnlinePaymentSetupDTO.class);
        onlinePaymentSetup.propertyAccounts().add(onlinePaymentSetup.propertyAccounts().$());
        Pmc pmc = VistaDeployment.getCurrentPmc();
        onlinePaymentSetup.businessInformation().companyName().setValue(pmc.name().getValue());

        // TODO retrieve terms
        StringBuilder contentBuilder = new StringBuilder(); // create content that has multiple lines to check scrolling of the viewer
        for (int i = 0; i < 100; ++i) {
            contentBuilder.append("Content ");
            for (int j = 0; j < 100; ++j) {
                contentBuilder.append("content ");
            }
            contentBuilder.append("content.<br/>");
        }
        onlinePaymentSetup.caledonAgreement().localizedCaption().setValue("Merchant Processing Application Agreement & Pre-Authorized Debit Agreement");

        onlinePaymentSetup.caledonAgreement().content().setValue(contentBuilder.toString());

        onlinePaymentSetup.paymentPadAgreement().localizedCaption().setValue("Pre-Authorized Debit Agreement and Interac Online");
        onlinePaymentSetup.paymentPadAgreement().content().setValue(contentBuilder.toString());

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

}
