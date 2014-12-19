/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.legal.l1.L1ReasonForApplication;

public class L1ReasonForApplicationForm extends CForm<L1ReasonForApplication> {

    private static final I18n i18n = I18n.get(L1ReasonForApplication.class);

    public L1ReasonForApplicationForm() {
        super(L1ReasonForApplication.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().applyingToCollectCharges()).decorate();
        formPanel.append(Location.Left, proto().applyingToCollectNsf()).decorate();
        formPanel.append(Location.Left, proto().isTenatStillInPossesionOfTheUnit()).decorate()
                .customLabel(i18n.tr("Is the tenant still in possession of the rental unit on the date this application is filed with the Board?"))
                .componentWidth("100px");
        formPanel.append(Location.Left, proto().rentPaymentPeriod()).decorate().componentWidth("100px");
        formPanel.append(Location.Left, proto().otherRentPaymentPeriodDescription()).decorate().componentWidth("100px");
        formPanel.append(Location.Left, new HTML("&nbsp;"));

        formPanel.h2(i18n.tr("Deposit"));
        formPanel.append(Location.Left, proto().amountOfRentOnDeposit()).decorate().customLabel(i18n.tr("The amount of rent currently on deposit"))
                .labelWidth("25em");
        formPanel.append(Location.Left, proto().dateOfDepositCollection()).decorate().customLabel(i18n.tr("The date the rent deposit was collected"))
                .labelWidth("25em");
        formPanel.h3(i18n.tr("The last period for which interest on the rent deposit was paid:"));
        formPanel.append(Location.Left, proto().lastPeriodInterestPaidFrom()).decorate().customLabel(i18n.tr("From")).componentWidth("150px");
        formPanel.append(Location.Left, proto().lastPeriodInterestPaidTo()).decorate().customLabel(i18n.tr("To")).componentWidth("150px");
        return formPanel;
    }
}
