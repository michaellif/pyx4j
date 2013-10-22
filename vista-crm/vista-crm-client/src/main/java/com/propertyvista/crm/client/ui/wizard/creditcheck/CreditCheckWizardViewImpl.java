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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.AbstractWizard;

import com.propertyvista.crm.client.ui.components.LegalTermsDialog;
import com.propertyvista.crm.rpc.services.PmcTermsOfServiceService;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.dto.vista2pmc.CreditCheckSetupDTO;

public class CreditCheckWizardViewImpl extends AbstractWizard<CreditCheckSetupDTO> implements CreditCheckWizardView {

    private static final I18n i18n = I18n.get(CreditCheckWizardViewImpl.class);

    public CreditCheckWizardViewImpl() {
        super(i18n.tr("Credit Check Setup"));
        setForm(new CreditCheckWizardForm(this, new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(GWT.<PmcTermsOfServiceService> create(PmcTermsOfServiceService.class), i18n.tr("Terms of Service")).show();
            }
        }));
    }

    @Override
    public void setCreditCheckFees(AbstractEquifaxFee creditCheckFees) {
        ((CreditCheckWizardForm) getForm()).setPricingOptions(creditCheckFees);
    }
}