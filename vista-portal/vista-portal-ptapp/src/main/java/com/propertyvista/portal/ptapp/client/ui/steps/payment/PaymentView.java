/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.payment;

import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepPresenter;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepView;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView.PaymentPresenter;
import com.propertyvista.portal.rpc.ptapp.dto.PaymentInformationDTO;

public interface PaymentView extends WizardStepView<PaymentInformationDTO, PaymentPresenter> {

    public interface PaymentPresenter extends WizardStepPresenter<PaymentInformationDTO> {

        public void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp);
    }
}
