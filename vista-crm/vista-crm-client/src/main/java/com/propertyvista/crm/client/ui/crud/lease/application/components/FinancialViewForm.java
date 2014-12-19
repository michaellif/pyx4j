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
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalAssetFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalIncomeFolder;
import com.propertyvista.dto.TenantFinancialDTO;

public class FinancialViewForm extends CForm<TenantFinancialDTO> {

    static I18n i18n = I18n.get(FinancialViewForm.class);

    public FinancialViewForm() {
        super(TenantFinancialDTO.class, new VistaEditorsComponentFactory());

        setEditable(false);
        setViewable(true);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().person().name(), new NameEditor(i18n.tr("Person")));

        formPanel.h1(proto().incomes().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().incomes(), new PersonalIncomeFolder(isEditable()));

        formPanel.h1(proto().assets().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().assets(), new PersonalAssetFolder(isEditable()));

        return formPanel;
    }
}
