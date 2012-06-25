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
package com.propertyvista.crm.client.ui.crud.organisation.vendor;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.CompanyPhoneFolder;
import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.vendor.Vendor;

public class VendorForm extends CrmEntityForm<Vendor> {

    private static final I18n i18n = I18n.get(VendorForm.class);

    public VendorForm() {
        this(false);
    }

    public VendorForm(boolean viewMode) {
        super(Vendor.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().website()), 35).build());
        content.setH1(++row, 0, 2, i18n.tr("Phone Numbers"));
        content.setWidget(++row, 0, inject(proto().phones(), new CompanyPhoneFolder(isEditable())));
        content.setH1(++row, 0, 2, i18n.tr("Emails"));
        content.setWidget(++row, 0, inject(proto().emails(), new EmailFolder(isEditable())));

        selectTab(addTab(content, i18n.tr("General")));
    }
}
