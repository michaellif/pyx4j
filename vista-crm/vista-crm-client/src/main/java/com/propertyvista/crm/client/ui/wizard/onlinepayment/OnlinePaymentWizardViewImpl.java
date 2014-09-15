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
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.AbstractWizard;

import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;

public class OnlinePaymentWizardViewImpl extends AbstractWizard<OnlinePaymentSetupDTO> implements OnlinePaymentWizardView {

    private static final I18n i18n = I18n.get(OnlinePaymentWizardViewImpl.class);

    public OnlinePaymentWizardViewImpl() {
        super(i18n.tr("Online Payment Setup"));
        setForm(new OnlinePaymentWizardForm(this, new Command() {
            @Override
            public void execute() {
                // TODO open terms 
                Window.alert("TODO: here be ToS");
            }
        }));
    }

    @Override
    public void setPaymentFees(AbstractPaymentFees paymentFees) {
        ((OnlinePaymentWizardForm) getForm()).setPaymentFees(paymentFees);
    }

}
