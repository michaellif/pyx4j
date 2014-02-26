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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.common.BuildingFolder;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioForm extends CrmEntityForm<Portfolio> {

    private static final I18n i18n = I18n.get(PortfolioForm.class);

    public PortfolioForm(IForm<Portfolio> view) {
        super(Portfolio.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().name()), 20, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());

        content.setH1(++row, 0, 2, i18n.tr("Assigned Buildings"));
        content.setWidget(++row, 0, 2, inject(proto().buildings(), new BuildingFolder(getParentView(), isEditable())));

        setTabBarVisible(false);
        selectTab(addTab(content));
    }
}
