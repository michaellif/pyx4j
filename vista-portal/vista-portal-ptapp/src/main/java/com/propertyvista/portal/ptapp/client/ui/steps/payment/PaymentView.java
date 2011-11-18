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

import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepPresenter;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepView;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView.PaymentPresenter;

public interface PaymentView extends WizardStepView<PaymentInformation, PaymentPresenter> {

    public interface PaymentPresenter extends WizardStepPresenter<PaymentInformation> {
    }

}
