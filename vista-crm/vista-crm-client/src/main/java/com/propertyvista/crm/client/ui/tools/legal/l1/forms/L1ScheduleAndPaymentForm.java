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
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;

import com.propertyvista.domain.legal.l1.L1ScheduleAndPayment;

public class L1ScheduleAndPaymentForm extends CEntityForm<L1ScheduleAndPayment> {

    private static final I18n i18n = I18n.get(L1ScheduleAndPayment.class);

    public L1ScheduleAndPaymentForm() {
        super(L1ScheduleAndPayment.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 2, i18n.tr("Application Fee"));
        panel.setWidget(++row, 0, 2, inject(proto().paymentInfo().paymentMethod(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().paymentInfo().creditCardNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().paymentInfo().expiryDate(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().paymentInfo().cardholdersName(), new FormDecoratorBuilder().build()));

        panel.setH1(++row, 0, 2, i18n.tr("Information Required to Schedule the Hearing"));
        panel.setWidget(
                ++row,
                0,
                2,
                inject(proto().appplicationSchedule().applicationPackageDeliveryMethodToLandlord(),
                        new FormDecoratorBuilder().customLabel(i18n.tr("How do you want the Board to give you the application package?")).build()));
        panel.setWidget(++row, 0, 2,
                inject(proto().appplicationSchedule().officeName(), new FormDecoratorBuilder().customLabel(i18n.tr("Which Office?")).build()));
        panel.setWidget(++row, 0, 2, inject(proto().appplicationSchedule().pickupDate(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().appplicationSchedule().fax(), new FormDecoratorBuilder().build()));

        panel.setWidget(
                ++row,
                0,
                2,
                inject(proto().appplicationSchedule().isSameDayDeliveryToTenant(),
                        new FormDecoratorBuilder().customLabel(
                                i18n.tr("Will you give the application package to the tenant(s) on the date you receive the package from the Board?")).build()));
        panel.setWidget(
                ++row,
                0,
                2,
                inject(proto().appplicationSchedule().toTenantDeliveryDate(),
                        new FormDecoratorBuilder().customLabel(i18n.tr("On what date will you give the package  to tenant(s)?")).build()));
        panel.setWidget(
                ++row,
                0,
                2,
                inject(proto().appplicationSchedule().applicationPackageDeliveryMethodToTenant(),
                        new FormDecoratorBuilder().customLabel(i18n.tr("How will you give the application package to the tenant(s)?")).build()));

        panel.setH1(++row, 0, 2, i18n.tr("Interpretation Services Required"));
        panel.setWidget(++row, 0, 2, inject(proto().languageServices(), new FormDecoratorBuilder().customLabel("").useLabelSemicolon(false).build()));
        return panel;
    }
}
