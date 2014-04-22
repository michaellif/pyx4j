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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class TenantsFolder extends PortalBoxFolder<LeaseTermTenant> {

    private static final I18n i18n = I18n.get(TenantsFolder.class);

    public TenantsFolder() {
        super(LeaseTermTenant.class, i18n.tr("Tenant"), false);
    }

    @Override
    protected CForm<LeaseTermTenant> createItemForm(IObject<?> member) {
        return new TenantForm();
    }

    class TenantForm extends CForm<LeaseTermTenant> {

        public TenantForm() {
            super(LeaseTermTenant.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0,
                    inject(proto().leaseParticipant().customer().person().name(), new CEntityLabel<Name>(), new FieldDecoratorBuilder().build()));
            mainPanel.setWidget(++row, 0, inject(proto().role(), new CEnumLabel(), new FieldDecoratorBuilder().build()));
            mainPanel.setWidget(++row, 0,
                    inject(proto().leaseParticipant().customer().person().email(), new CLabel<String>(), new FieldDecoratorBuilder().build()));

            return mainPanel;
        }
    }
}
