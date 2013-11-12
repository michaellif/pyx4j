/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.prospect.ui.steps.PaymentStepView.PaymentStepPresenter;
import com.propertyvista.portal.rpc.portal.web.dto.application.PaymentStepDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizardStep;

public class PaymentStep extends CPortalEntityWizardStep<PaymentStepDTO> {

    private static final I18n i18n = I18n.get(PaymentStep.class);

    private PaymentStepPresenter presenter;

    public PaymentStep(PaymentStepView view) {
        super(PaymentStepDTO.class, view, i18n.tr("Payment"), i18n.tr("Submit"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        return mainPanel;
    }

}
