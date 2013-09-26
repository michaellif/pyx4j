/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.insurance;

import com.google.gwt.user.client.Command;

import com.propertyvista.domain.moveinwizardmockup.InsuranceDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepViewImpl;

public class InsuranceViewImpl extends WizardStepViewImpl<InsuranceDTO, InsurancePresenter> implements InsuranceView {

    public InsuranceViewImpl() {
        super(new InsuranceForm());
        setActionButtonVisible(false);
        ((InsuranceForm) getForm()).setOnPurchaseInsuranceConfirmedCommand(new Command() {
            @Override
            public void execute() {
                onPurchacheInsuranceConfirmed();
            }
        });

        ((InsuranceForm) getForm()).setOnExistingInsuranceConfirmedCommand(new Command() {
            @Override
            public void execute() {
                onExistingInsuranceConfirmed();
            }
        });
    }

    private void onPurchacheInsuranceConfirmed() {
        onAction();
    }

    private void onExistingInsuranceConfirmed() {
        onAction();
    }

}
