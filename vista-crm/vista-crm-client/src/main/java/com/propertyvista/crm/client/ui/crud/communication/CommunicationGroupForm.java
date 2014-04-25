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
package com.propertyvista.crm.client.ui.crud.communication;

import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.common.BuildingFolder;
import com.propertyvista.crm.client.ui.crud.organisation.common.PortfolioFolder;
import com.propertyvista.crm.client.ui.crud.organisation.employee.CrmRoleFolder;
import com.propertyvista.domain.communication.CommunicationGroup;

public class CommunicationGroupForm extends CrmEntityForm<CommunicationGroup> {

    private static final I18n i18n = I18n.get(CommunicationGroupForm.class);

    private final TwoColumnFlexFormPanel mainTab;

    private final CrmRoleFolder roleFolder;

    public CommunicationGroupForm(IForm<CommunicationGroup> view) {
        super(CommunicationGroup.class, view);

        roleFolder = new CrmRoleFolder(this);
        mainTab = createInfoTab();
        selectTab(addTab(mainTab, i18n.tr("Communication Settings")));

    }

    private TwoColumnFlexFormPanel createInfoTab() {
        int row = -1;
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        panel.setWidget(++row, 0, 2, inject(proto().name(), new FieldDecoratorBuilder(20).build()));
        panel.setWidget(++row, 0, 2, inject(proto().isPredefined(), new CBooleanLabel(), new FieldDecoratorBuilder(20).build()));
        panel.setH1(++row, 0, 2, i18n.tr("CRM User Roles"));
        panel.setWidget(++row, 0, 2, inject(proto().roles(), roleFolder));
        panel.setH1(++row, 0, 2, i18n.tr("Contact Associated With"));
        panel.setH3(++row, 0, 2, i18n.tr("Buildings"));
        panel.setWidget(++row, 0, 2, inject(proto().buildings(), new BuildingFolder(this.getParentView(), true)));
        panel.setH3(++row, 0, 2, i18n.tr("Portfolios"));
        panel.setWidget(++row, 0, 2, inject(proto().portfolios(), new PortfolioFolder(this.getParentView(), true)));
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        CommunicationGroup se = getValue();
        if (se == null) {
            return;
        }

        setEditable(!se.isPredefined().getValue(false));

        mainTab.setTitle(i18n.tr(se.getStringView()));
    }
}
