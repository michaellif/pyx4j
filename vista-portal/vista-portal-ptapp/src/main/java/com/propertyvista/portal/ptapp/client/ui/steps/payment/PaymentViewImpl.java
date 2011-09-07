/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.payment;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.propertyvista.portal.domain.ptapp.PaymentInfo;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView.PaymentPresenter;

public class PaymentViewImpl extends WizardStepViewImpl<PaymentInfo, PaymentPresenter> implements PaymentView {

    private static I18n i18n = I18nFactory.getI18n(PaymentViewImpl.class);

    public PaymentViewImpl() {
        super(new PaymentViewForm());
    }

    @Override
    protected String actionName() {
        return i18n.tr("Pay and Continue");
    }

}
