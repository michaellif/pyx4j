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

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.employee.CrmRoleFolder;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;

public class CommunicationGroupForm extends CrmEntityForm<MessageCategory> {

    private static final I18n i18n = I18n.get(CommunicationGroupForm.class);

    private final IsWidget mainTab;

    public CommunicationGroupForm(IForm<MessageCategory> view) {
        super(MessageCategory.class, view);

        mainTab = createInfoTab();
        setTabBarVisible(false);
        selectTab(addTab(mainTab, i18n.tr("Message Group Properties")));

    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().topic()).decorate();
        formPanel.append(Location.Left, proto().category(), new CLabel<MessageGroupCategory>()).decorate();
        formPanel.h1(i18n.tr("Message Group Dispatchers"));
        formPanel.append(Location.Left, proto().dispatchers(), new EmployeeFolder(this, new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return (getValue() != null ? getValue().getPrimaryKey() : null);
            }
        }));
        formPanel.h1(i18n.tr("User Roles allowed to see group messages"));
        formPanel.append(Location.Left, proto().roles(), new CrmRoleFolder(this));
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        MessageCategory se = getValue();
        if (se == null) {
            return;
        }
        //setEditable(!se.isPredefined().getValue(false));
    }
}
