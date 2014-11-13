/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.availablereport;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.crm.client.ui.components.CrmRoleFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.reports.AvailableCrmReport;

public class AvailableCrmReportForm extends CrmEntityForm<AvailableCrmReport> {

    private static final I18n i18n = I18n.get(AvailableCrmReportForm.class);

    public AvailableCrmReportForm(IFormView<AvailableCrmReport, ?> view) {
        super(AvailableCrmReport.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().reportType()).decorate();
        formPanel.append(Location.Left, proto().roles(), new CrmRoleFolder(this));

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("Available Crm Report")));
    }

}
