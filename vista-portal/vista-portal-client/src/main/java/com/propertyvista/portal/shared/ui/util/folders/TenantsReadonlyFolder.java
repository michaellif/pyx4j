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
package com.propertyvista.portal.shared.ui.util.folders;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class TenantsReadonlyFolder extends PortalBoxFolder<LeaseTermTenant> {

    private static final I18n i18n = I18n.get(TenantsReadonlyFolder.class);

    public TenantsReadonlyFolder() {
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
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().name(), new CEntityLabel<Name>()).decorate();
            formPanel.append(Location.Left, proto().role(), new CEnumLabel()).decorate();
            formPanel.append(Location.Left, proto().relationship(), new CEnumLabel()).decorate();
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().email(), new CLabel<String>()).decorate();

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().relationship()).setVisible(!getValue().relationship().isNull());
        }
    }
}
