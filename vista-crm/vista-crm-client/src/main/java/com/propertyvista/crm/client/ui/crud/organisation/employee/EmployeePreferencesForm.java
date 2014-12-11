/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.CrmUserDeliveryPreferencesDTO;

public class EmployeePreferencesForm extends CrmEntityForm<CrmUserDeliveryPreferencesDTO> {

    private static final I18n i18n = I18n.get(EmployeePreferencesForm.class);

    public EmployeePreferencesForm(IPrimeFormView<CrmUserDeliveryPreferencesDTO, ?> view) {
        super(CrmUserDeliveryPreferencesDTO.class, view);
        selectTab(addTab(createGeneralForm(), i18n.tr("Account Preferences")));
        setTabBarVisible(false);
    }

    private IsWidget createGeneralForm() {

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Communication Preferences"));
        formPanel.br();

        formPanel.h3(i18n.tr("Informational"));
        formPanel.append(Location.Left, proto().informationalDelivery()).decorate().customLabel(i18n.tr("Delivery Frequency"));
        formPanel.br();
        formPanel.h3(i18n.tr("Promotional"));
        formPanel.append(Location.Left, proto().promotionalDelivery()).decorate().customLabel(i18n.tr("Delivery Frequency"));

        return formPanel;
    }
}
