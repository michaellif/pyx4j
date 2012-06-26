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
package com.propertyvista.admin.client.ui.crud.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.shared.VistaSystemIdentification;

public class MaintenanceForm extends AdminEntityForm<SystemMaintenanceState> {

    private static final I18n i18n = I18n.get(MaintenanceForm.class);

    public MaintenanceForm() {
        this(false);
    }

    public MaintenanceForm(boolean viewMode) {
        super(SystemMaintenanceState.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().systemIdentification()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().inEffect()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().externalConnections()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().startDate()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().startTime()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().gracePeriod()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().duration()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().message()), 20).build());

        get(proto().inEffect()).setViewable(true);

        Collection<String> opt = new ArrayList<String>();
        for (VistaSystemIdentification i : EnumSet.allOf(VistaSystemIdentification.class)) {
            opt.add(i.name());
        }
        ((CComboBox<String>) get(proto().systemIdentification())).setOptions(opt);

        selectTab(addTab(content, i18n.tr("General")));
    }
}