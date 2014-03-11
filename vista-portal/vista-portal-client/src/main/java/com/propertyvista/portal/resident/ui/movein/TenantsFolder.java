/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class TenantsFolder extends PortalBoxFolder<LeaseTermTenant> {

    private static final I18n i18n = I18n.get(TenantsFolder.class);

    public TenantsFolder() {
        super(LeaseTermTenant.class, i18n.tr("Tenant"));
        setOrderable(false);
        setAddable(false);
        setRemovable(false);
    }

    @Override
    public IFolderItemDecorator<LeaseTermTenant> createItemDecorator() {
        BoxFolderItemDecorator<LeaseTermTenant> decor = (BoxFolderItemDecorator<LeaseTermTenant>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LeaseTermTenant) {
            return new TenantForm();
        } else {
            return super.create(member);
        }
    }

    class TenantForm extends CEntityForm<LeaseTermTenant> {

        public TenantForm() {
            super(LeaseTermTenant.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(
                    inject(proto().leaseParticipant().customer().person().name(), new CEntityLabel<Name>())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().role(), new CEnumLabel())).build());
            mainPanel.setWidget(++row, 0,
                    new FormWidgetDecoratorBuilder(inject(proto().leaseParticipant().customer().person().email(), new CLabel<String>())).build());

            return mainPanel;
        }
    }
}
