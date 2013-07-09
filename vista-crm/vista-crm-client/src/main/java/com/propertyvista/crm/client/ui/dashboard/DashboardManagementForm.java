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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementForm extends CrmEntityForm<DashboardMetadata> {

    private static final I18n i18n = I18n.get(DashboardManagementForm.class);

    public DashboardManagementForm(IForm<DashboardMetadata> view) {
        super(DashboardMetadata.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type()), 15).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name()), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 40).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().isShared()), 3).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().ownerUser().name()), 15).customLabel(i18n.tr("Owner")).build());
        (get(proto().ownerUser().name())).setViewable(true);
        selectTab(addTab(content));
    }

    public void setNewDashboardMode(boolean isNewDashboard) {
        (get(proto().type())).setViewable(!isNewDashboard);
        (get(proto().ownerUser().name())).setVisible(!isNewDashboard);
    }
}