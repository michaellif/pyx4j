/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.prospect.dto.TenantDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class TenantsReadonlyFolder extends PortalBoxFolder<TenantDTO> {

    private static final I18n i18n = I18n.get(TenantsReadonlyFolder.class);

    public TenantsReadonlyFolder() {
        super(TenantDTO.class, i18n.tr("Co-Applicant"), false);
        setViewable(true);
    }

    @Override
    public IFolderItemDecorator<TenantDTO> createItemDecorator() {
        BoxFolderItemDecorator<TenantDTO> decor = (BoxFolderItemDecorator<TenantDTO>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof TenantDTO) {
            return new TenantForm();
        }
        return super.create(member);
    }

    class TenantForm extends CEntityForm<TenantDTO> {

        public TenantForm() {
            super(TenantDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().role())).build());

            return mainPanel;
        }
    }
}