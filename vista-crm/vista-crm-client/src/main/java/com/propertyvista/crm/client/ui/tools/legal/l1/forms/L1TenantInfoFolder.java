/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.legal.l1.L1TenantInfo;

public class L1TenantInfoFolder extends VistaBoxFolder<L1TenantInfo> {

    public L1TenantInfoFolder() {
        super(L1TenantInfo.class);
        setOrderable(false);
    }

    @Override
    protected CForm<L1TenantInfo> createItemForm(IObject<?> member) {
        return new L1TenantInfoForm();
    }

    @Override
    public VistaBoxFolderItemDecorator<L1TenantInfo> createItemDecorator() {
        VistaBoxFolderItemDecorator<L1TenantInfo> dectorator = super.createItemDecorator();
        dectorator.setCollapsible(false);
        return dectorator;
    }

    public static class L1TenantInfoForm extends CForm<L1TenantInfo> {

        public L1TenantInfoForm() {
            super(L1TenantInfo.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().firstName()).decorate();
            formPanel.append(Location.Left, proto().lastName()).decorate();
            formPanel.append(Location.Left, proto().gender()).decorate();
            return formPanel;
        }

    }
}
