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
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementForm extends CrmEntityForm<DashboardMetadata> {

    private static final I18n i18n = I18n.get(DashboardManagementForm.class);

    public DashboardManagementForm(IForm<DashboardMetadata> view) {
        super(DashboardMetadata.class, view);

        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(240);
        formPanel.append(Location.Right, proto().isShared()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().ownerUser().name(), new CLabel<String>()).decorate().componentWidth(180).customLabel(i18n.tr("Owner"));
        formPanel.append(Location.Dual, proto().description()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    public void setNewDashboardMode(boolean isNewDashboard) {
        (get(proto().type())).setEditable(isNewDashboard);
        (get(proto().ownerUser().name())).setVisible(!isNewDashboard);
    }
}