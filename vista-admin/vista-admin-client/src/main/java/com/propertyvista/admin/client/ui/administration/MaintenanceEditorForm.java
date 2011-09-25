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
package com.propertyvista.admin.client.ui.administration;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.forms.client.ui.CBooleanLabel;

import com.propertyvista.admin.client.ui.components.AdminEditorsComponentFactory;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.client.ui.decorations.AdminScrollPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;

public class MaintenanceEditorForm extends AdminEntityForm<SystemMaintenanceState> {

    public MaintenanceEditorForm() {
        super(SystemMaintenanceState.class, new AdminEditorsComponentFactory());
    }

    public MaintenanceEditorForm(IEditableComponentFactory factory) {
        super(SystemMaintenanceState.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().inEffect(), new CBooleanLabel()), 10);
        main.add(inject(proto().type()), 10);
        main.add(inject(proto().startDate()), 10);
        main.add(inject(proto().startTime()), 10);
        main.add(inject(proto().gracePeriod()), 10);
        main.add(inject(proto().duration()), 10);
        main.add(inject(proto().message()), 20);

        return new AdminScrollPanel(main);
    }
}