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

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementForm extends CrmEntityForm<DashboardMetadata> {

    private static final I18n i18n = I18n.get(DashboardManagementForm.class);

    public DashboardManagementForm(IForm<DashboardMetadata> view) {
        super(DashboardMetadata.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;

        content.setH1(++row, 0, 2, i18n.tr("General"));
        content.setWidget(++row, 0, inject(proto().type(), new FieldDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder(20).build()));
        content.setWidget(++row, 0, inject(proto().description(), new FieldDecoratorBuilder(40).build()));
        content.setWidget(++row, 0, inject(proto().isShared(), new FieldDecoratorBuilder(3).build()));
        content.setWidget(++row, 0,
                inject(proto().ownerUser().name(), new CLabel<String>(), new FieldDecoratorBuilder(15).customLabel(i18n.tr("Owner")).build()));

        selectTab(addTab(content));
        setTabBarVisible(false);
    }

    public void setNewDashboardMode(boolean isNewDashboard) {
        (get(proto().type())).setEditable(isNewDashboard);
        (get(proto().ownerUser().name())).setVisible(!isNewDashboard);
    }
}