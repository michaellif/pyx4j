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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;
import com.propertyvista.shared.VistaSystemIdentification;

public class MaintenanceForm extends OperationsEntityForm<VistaSystemMaintenanceState> {

    private static final I18n i18n = I18n.get(MaintenanceForm.class);

    public MaintenanceForm(IForm<VistaSystemMaintenanceState> view) {
        super(VistaSystemMaintenanceState.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;
        content.setWidget(++row, 0, inject(proto().systemIdentification(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().startDate(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().inEffect(), new CBooleanLabel(), new FieldDecoratorBuilder().build()));
        content.setWidget(row, 1, inject(proto().startTime(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().type(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().gracePeriod(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().externalConnections(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().duration(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, 2, inject(proto().message(), new FieldDecoratorBuilder(true).build()));

        Collection<String> opt = new ArrayList<String>();
        for (VistaSystemIdentification i : EnumSet.allOf(VistaSystemIdentification.class)) {
            opt.add(i.name());
        }
        ((CComboBox<String>) get(proto().systemIdentification())).setOptions(opt);

        selectTab(addTab(content, i18n.tr("General")));

        TwoColumnFlexFormPanel tenantSureMaintenanceTab = new TwoColumnFlexFormPanel();
        row = -1;
        tenantSureMaintenanceTab.setWidget(++row, 0, 2, inject(proto().enableTenantSureMaintenance(), new FieldDecoratorBuilder(5, true).build()));
        tenantSureMaintenanceTab.setWidget(++row, 0, 2, inject(proto().enableFundsTransferMaintenance(), new FieldDecoratorBuilder(5, true).build()));
        tenantSureMaintenanceTab.setWidget(++row, 0, 2, inject(proto().enableCreditCardMaintenance(), new FieldDecoratorBuilder(5, true).build()));
        tenantSureMaintenanceTab
                .setWidget(++row, 0, 2, inject(proto().enableCreditCardConvenienceFeeMaintenance(), new FieldDecoratorBuilder(5, true).build()));
        tenantSureMaintenanceTab.setWidget(++row, 0, 2, inject(proto().enableInteracMaintenance(), new FieldDecoratorBuilder(5, true).build()));
        tenantSureMaintenanceTab.setWidget(++row, 0, 2, inject(proto().enableEquifaxMaintenance(), new FieldDecoratorBuilder(5, true).build()));

        addTab(tenantSureMaintenanceTab, i18n.tr("Vista Interfaces"));

    }
}