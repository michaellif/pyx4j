/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;
import com.propertyvista.shared.VistaSystemIdentification;

public class MaintenanceForm extends OperationsEntityForm<VistaSystemMaintenanceState> {

    private static final I18n i18n = I18n.get(MaintenanceForm.class);

    public MaintenanceForm(IPrimeFormView<VistaSystemMaintenanceState, ?> view) {
        super(VistaSystemMaintenanceState.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().systemIdentification()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().startDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().inEffect(), new CBooleanLabel()).decorate();
        formPanel.append(Location.Right, proto().startTime()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().gracePeriod()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().externalConnections()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().duration()).decorate().componentWidth(120);
        formPanel.append(Location.Dual, proto().message()).decorate();

        Collection<String> opt = new ArrayList<String>();
        for (VistaSystemIdentification i : EnumSet.allOf(VistaSystemIdentification.class)) {
            opt.add(i.name());
        }
        ((CComboBox<String>) get(proto().systemIdentification())).setOptions(opt);

        selectTab(addTab(formPanel, i18n.tr("General")));

        FormPanel tenantSureMaintenanceTab = new FormPanel(this);

        tenantSureMaintenanceTab.append(Location.Dual, proto().enableTenantSureMaintenance()).decorate().componentWidth(60);
        tenantSureMaintenanceTab.append(Location.Dual, proto().enableFundsTransferMaintenance()).decorate().componentWidth(60);
        tenantSureMaintenanceTab.append(Location.Dual, proto().enableCreditCardMaintenance()).decorate().componentWidth(60);
        tenantSureMaintenanceTab.append(Location.Dual, proto().enableCreditCardConvenienceFeeMaintenance()).decorate().componentWidth(60);
        tenantSureMaintenanceTab.append(Location.Dual, proto().enableInteracMaintenance()).decorate().componentWidth(60);
        tenantSureMaintenanceTab.append(Location.Dual, proto().enableEquifaxMaintenance()).decorate().componentWidth(60);

        addTab(tenantSureMaintenanceTab, i18n.tr("Vista Interfaces"));

    }
}