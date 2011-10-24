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

import com.propertyvista.common.client.ui.components.CLicense;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorEditorForm extends MechlBaseEditorForm<ElevatorDTO> {

    public ElevatorEditorForm() {
        super(ElevatorDTO.class, new CrmEditorsComponentFactory());
    }

    public ElevatorEditorForm(IEditableComponentFactory factory) {
        super(ElevatorDTO.class, factory);
    }

    @Override
    protected Widget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, decorate(inject(proto().type()), 15));
        main.setWidget(++row, 0, decorate(inject(proto().make()), 15));
        main.setWidget(++row, 0, decorate(inject(proto().model()), 15));

        main.setHeader(++row, 0, 2, i18n.tr("License"));
        main.setWidget(++row, 0, inject(proto().license(), new CLicense()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setHeader(++row, 0, 2, "");
        main.setWidget(++row, 0, decorate(inject(proto().notes()), 57));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        main.setWidget(++row, 1, decorate(inject(proto().build()), 9));
        main.setWidget(++row, 1, decorate(inject(proto().description()), 20));
        main.setWidget(++row, 1, decorate(inject(proto().isForMoveInOut()), 5));

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().build()));
    }
}