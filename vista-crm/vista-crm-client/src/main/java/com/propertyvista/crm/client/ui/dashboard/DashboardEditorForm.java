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
package com.propertyvista.crm.client.ui.dashboard;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardEditorForm extends CrmEntityForm<DashboardMetadata> {

    public DashboardEditorForm() {
        super(DashboardMetadata.class, new CrmEditorsComponentFactory());
    }

    public DashboardEditorForm(IEditableComponentFactory factory) {
        super(DashboardMetadata.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 40).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isFavorite()), 3).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isShared()), 3).build());

        return main;
    }
}