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
package com.propertyvista.crm.client.ui.crud.building.catalog.concession;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionEditorForm extends CrmEntityForm<Concession> {

    public ConcessionEditorForm() {
        super(Concession.class, new CrmEditorsComponentFactory());
    }

    public ConcessionEditorForm(IEditableComponentFactory factory) {
        super(Concession.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, decorate(inject(proto().type()), 12));
        main.setWidget(++row, 0, decorate(inject(proto().term()), 12));
        main.setWidget(++row, 0, decorate(inject(proto().value()), 6));
        main.setWidget(++row, 0, decorate(inject(proto().condition()), 8));

        row = -1;
        main.setWidget(++row, 1, decorate(inject(proto().status()), 8));
        main.setWidget(++row, 1, decorate(inject(proto().approvedBy()), 20));
        main.setWidget(++row, 1, decorate(inject(proto().effectiveDate()), 8.2));
        main.setWidget(++row, 1, decorate(inject(proto().expirationDate()), 8.2));

        main.setWidget(++row, 0, decorate(inject(proto().description()), 50));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }
}
