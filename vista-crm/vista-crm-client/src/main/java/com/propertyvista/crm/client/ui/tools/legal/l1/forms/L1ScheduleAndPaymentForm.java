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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.legal.l1.L1ScheduleAndPayment;

public class L1ScheduleAndPaymentForm extends CForm<L1ScheduleAndPayment> {

    private static final I18n i18n = I18n.get(L1ScheduleAndPayment.class);

    public L1ScheduleAndPaymentForm() {
        super(L1ScheduleAndPayment.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Application Fee"));
        formPanel.append(Location.Dual, proto().paymentInfo().paymentMethod()).decorate();
        formPanel.append(Location.Dual, proto().paymentInfo().creditCardNumber()).decorate();
        formPanel.append(Location.Dual, proto().paymentInfo().expiryDate()).decorate();
        formPanel.append(Location.Dual, proto().paymentInfo().cardholdersName()).decorate();

        formPanel.h1(i18n.tr("Information Required to Schedule the Hearing"));
        formPanel.append(Location.Dual, proto().appplicationSchedule().applicationPackageDeliveryMethodToLandlord()).decorate();
        formPanel.append(Location.Dual, proto().appplicationSchedule().officeName()).decorate().customLabel(i18n.tr("Which Office?"));
        formPanel.append(Location.Dual, proto().appplicationSchedule().pickupDate()).decorate();
        formPanel.append(Location.Dual, proto().appplicationSchedule().fax()).decorate();

        formPanel.append(Location.Dual, proto().appplicationSchedule().isSameDayDeliveryToTenant()).decorate()
                .customLabel(i18n.tr("Will you give the application package to the tenant(s) on the date you receive the package from the Board?"));

        formPanel.append(Location.Dual, proto().appplicationSchedule().toTenantDeliveryDate()).decorate()
                .customLabel(i18n.tr("On what date will you give the package  to tenant(s)?"));

        formPanel.append(Location.Dual, proto().appplicationSchedule().applicationPackageDeliveryMethodToTenant()).decorate()
                .customLabel(i18n.tr("How will you give the application package to the tenant(s)?"));

        formPanel.h1(i18n.tr("Interpretation Services Required"));
        formPanel.append(Location.Dual, proto().languageServices()).decorate().customLabel("").useLabelSemicolon(false);
        return formPanel;
    }
}
