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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.CreditCheckWizardService;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.dto.CreditCheckSetupDTO;

public class CreditCheckWizardServiceImpl implements CreditCheckWizardService {

    @Override
    public void create(AsyncCallback<CreditCheckSetupDTO> callback) {
        CreditCheckSetupDTO creditCheck = EntityFactory.create(CreditCheckSetupDTO.class);
        Pmc pmc = VistaDeployment.getCurrentPmc();
        creditCheck.businessInformation().companyName().setValue(pmc.name().getValue());
        callback.onSuccess(creditCheck);
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void finish(AsyncCallback<VoidSerializable> callback, CreditCheckSetupDTO editableEntity) {
        System.out.println("++++++++save");
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
