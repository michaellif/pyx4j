/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.inquiry;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.folders.PhoneFolder;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.InquiryDTO;

public class InquiryEditorForm extends CrmEntityForm<InquiryDTO> {

    public InquiryEditorForm() {
        super(InquiryDTO.class, new CrmEditorsComponentFactory());
    }

    public InquiryEditorForm(IEditableComponentFactory factory) {
        super(InquiryDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, decorate(inject(proto().name()), 15));
        main.setWidget(++row, 0, decorate(inject(proto().email()), 15));
        main.setWidget(++row, 0, decorate(inject(proto().description()), 57));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setHeader(++row, 0, 2, proto().phones().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().phones(), new PhoneFolder(isEditable())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        main.setWidget(++row, 1, decorate(inject(proto().building()), 15));
        main.setWidget(++row, 1, decorate(inject(proto().unit()), 15));

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }
}