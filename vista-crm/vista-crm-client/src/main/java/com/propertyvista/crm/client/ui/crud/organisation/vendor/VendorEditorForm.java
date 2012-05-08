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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.CompanyPhoneFolder;
import com.propertyvista.common.client.ui.components.folders.EmailFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.vendor.Vendor;

public class VendorEditorForm extends CrmEntityForm<Vendor> {

    private static final I18n i18n = I18n.get(VendorEditorForm.class);

    public VendorEditorForm() {
        this(false);
    }

    public VendorEditorForm(boolean viewMode) {
        super(Vendor.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().website()), 35).build());
        main.setH1(++row, 0, 2, i18n.tr("Phone Numbers"));
        main.setWidget(++row, 0, inject(proto().phones(), new CompanyPhoneFolder(isEditable())));
        main.setH1(++row, 0, 2, i18n.tr("Emails"));
        main.setWidget(++row, 0, inject(proto().emails(), new EmailFolder(isEditable())));
        return new ScrollPanel(main);
    }
}
