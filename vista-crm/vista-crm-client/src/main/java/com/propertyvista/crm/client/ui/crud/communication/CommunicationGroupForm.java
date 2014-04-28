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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
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

    private final IsWidget mainTab;

    private final CrmRoleFolder roleFolder;

    public CommunicationGroupForm(IForm<CommunicationGroup> view) {
        super(CommunicationGroup.class, view);

        roleFolder = new CrmRoleFolder(this);
        mainTab = createInfoTab();
        selectTab(addTab(mainTab, i18n.tr("Communication Settings")));

    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().isPredefined(), new CBooleanLabel()).decorate();
        formPanel.h1(i18n.tr("CRM User Roles"));
        formPanel.append(Location.Full, proto().roles(), roleFolder);
        formPanel.h1(i18n.tr("Contact Associated With"));
        formPanel.h3(i18n.tr("Buildings"));
        formPanel.append(Location.Full, proto().buildings(), new BuildingFolder(this.getParentView(), true));
        formPanel.h3(i18n.tr("Portfolios"));
        formPanel.append(Location.Full, proto().portfolios(), new PortfolioFolder(this.getParentView(), true));
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        CommunicationGroup se = getValue();
        if (se == null) {
            return;
        }
        setEditable(!se.isPredefined().getValue(false));
    }
}
