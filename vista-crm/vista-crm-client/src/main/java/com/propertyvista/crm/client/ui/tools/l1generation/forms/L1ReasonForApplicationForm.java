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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation.forms;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.l1.L1ReasonForApplication;

public class L1ReasonForApplicationForm extends CEntityForm<L1ReasonForApplication> {

    private static final I18n i18n = I18n.get(L1ReasonForApplication.class);

    public L1ReasonForApplicationForm() {
        super(L1ReasonForApplication.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = 0;
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().applyingToCollectCharges())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().applyingToCollectNsf())).build());
        panel.setWidget(
                ++row,
                0,
                1,
                new FormDecoratorBuilder(inject(proto().isTenatStillInPossesionOfTheUnit()))
                        .customLabel(i18n.tr("Is the tenant still in possession of the rental unit on the date this application is filed with the Board?"))
                        .componentWidth("100px").build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().rentPaymentPeriod())).componentWidth("100px").build());
        panel.setWidget(row, 1, 1, new FormDecoratorBuilder(inject(proto().otherRentPaymentPeriodDescription())).componentWidth("100px").contentWidth("100px")
                .build());
        panel.setWidget(++row, 0, 1, new HTML("&nbsp;"));

        panel.setH2(++row, 0, 2, i18n.tr("Deposit"));
        panel.setWidget(
                ++row,
                0,
                1,
                new FormDecoratorBuilder(inject(proto().amountOfRentOnDeposit())).customLabel(i18n.tr("The amount of rent currently on deposit"))
                        .labelWidth("25em").build());
        panel.setWidget(
                ++row,
                0,
                1,
                new FormDecoratorBuilder(inject(proto().dateOfDepositCollection())).customLabel(i18n.tr("The date the rent deposit was collected"))
                        .labelWidth("25em").build());
        panel.setH3(++row, 0, 2, i18n.tr("The last period for which interest on the rent deposit was paid:"));
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().lastPeriodInterestPaidFrom())).customLabel(i18n.tr("From"))
                .componentWidth("150px").build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().lastPeriodInterestPaidTo())).customLabel(i18n.tr("To")).componentWidth("150px")
                .build());
        return panel;
    }
}
