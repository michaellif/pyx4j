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
package com.propertyvista.crm.client.ui.crud.building.mech;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.editors.LicenseEditor;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.BoilerDTO;

public class BoilerEditorForm extends MechlBaseEditorForm<BoilerDTO> {

    public BoilerEditorForm() {
        super(BoilerDTO.class, new CrmEditorsComponentFactory());
    }

    public BoilerEditorForm(IEditableComponentFactory factory) {
        super(BoilerDTO.class, factory);
    }

    @Override
    protected Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().make()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().model()), 15).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().build()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().description()), 20).build());
        main.getFlexCellFormatter().setRowSpan(row, 1, 3);

        row += 2;
        main.setH1(++row, 0, 2, proto().license().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().license(), new LicenseEditor()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, "");
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notes()), 57).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().build()));
    }
}