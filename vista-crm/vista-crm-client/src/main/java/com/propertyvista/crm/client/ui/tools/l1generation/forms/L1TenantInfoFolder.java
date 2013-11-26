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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.legal.l1.L1TenantInfo;

public class L1TenantInfoFolder extends VistaBoxFolder<L1TenantInfo> {

    public L1TenantInfoFolder() {
        super(L1TenantInfo.class);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof L1TenantInfo) {
            return new L1TenantInfoForm();
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<L1TenantInfo> createItemDecorator() {
        VistaBoxFolderItemDecorator<L1TenantInfo> dectorator = (VistaBoxFolderItemDecorator<L1TenantInfo>) super.createItemDecorator();
        dectorator.setCollapsible(false);
        return dectorator;
    }

    public static class L1TenantInfoForm extends CEntityForm<L1TenantInfo> {

        public L1TenantInfoForm() {
            super(L1TenantInfo.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().firstName())).build());
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().lastName())).build());
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().gender())).build());
            return panel;
        }

    }
}
