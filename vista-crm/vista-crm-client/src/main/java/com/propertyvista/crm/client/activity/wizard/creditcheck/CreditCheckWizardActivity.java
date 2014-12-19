/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 */
package com.propertyvista.crm.client.activity.wizard.creditcheck;

import com.google.gwt.core.client.GWT;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmWizardActivity;
import com.propertyvista.crm.client.ui.wizard.creditcheck.CreditCheckWizardView;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckWizardService;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.dto.vista2pmc.CreditCheckSetupDTO;

public class CreditCheckWizardActivity extends CrmWizardActivity<CreditCheckSetupDTO> implements CreditCheckWizardView.Presenter {

    public CreditCheckWizardActivity(CrudAppPlace place) {
        super(CreditCheckSetupDTO.class, place, CrmSite.getViewFactory().getView(CreditCheckWizardView.class),
                GWT.<CreditCheckWizardService> create(CreditCheckWizardService.class));
    }

    @Override
    protected void populateView(final CreditCheckSetupDTO result) {
        ((CreditCheckWizardService) getService()).obtatinFee(new DefaultAsyncCallback<AbstractEquifaxFee>() {
            @Override
            public void onSuccess(AbstractEquifaxFee creditCheckFees) {
                CreditCheckWizardActivity.super.populateView(result);
                ((CreditCheckWizardView) CreditCheckWizardActivity.this.getView()).setCreditCheckFees(creditCheckFees);
            }
        });

    }
}
