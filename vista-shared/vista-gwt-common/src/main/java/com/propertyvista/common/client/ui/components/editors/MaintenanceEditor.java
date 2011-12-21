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
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.property.vendor.Maintenance;

public class MaintenanceEditor extends CEntityDecoratableEditor<Maintenance> {

    private static final I18n i18n = I18n.get(MaintenanceEditor.class);

    public MaintenanceEditor() {
        super(Maintenance.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, proto().contract().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().contract(), new ContractEditor()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Schedule"));
        ++row;
        main.setWidget(row, 0, new DecoratorBuilder(inject(proto().lastService()), 9).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().nextService()), 9).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }
}
