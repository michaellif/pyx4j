/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.domain.property.vendor.Maintenance;

public class MaintenanceEditor extends CForm<Maintenance> {

    private static final I18n i18n = I18n.get(MaintenanceEditor.class);

    public MaintenanceEditor() {
        super(Maintenance.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(proto().contract().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().contract(), new ContractEditor());

        formPanel.h1(i18n.tr("Schedule"));

        formPanel.append(Location.Left, proto().lastService()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().nextService()).decorate().componentWidth(120);

        validateMaintenanceDates();
        return formPanel;
    }

    private void validateMaintenanceDates() {
        get(proto().lastService()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().nextService()).addComponentValidator(new FutureDateValidator());
    }
}
