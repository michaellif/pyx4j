/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.portfolio;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.common.BuildingFolder;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioForm extends CrmEntityForm<Portfolio> {

    private static final I18n i18n = I18n.get(PortfolioForm.class);

    public PortfolioForm(IPrimeFormView<Portfolio, ?> view) {
        super(Portfolio.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Information"));
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().description()).decorate();

        formPanel.h1(i18n.tr("Assigned Buildings"));
        formPanel.append(Location.Dual, proto().buildings(), new BuildingFolder(isEditable()));

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}
