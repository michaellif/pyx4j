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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionEditorForm extends CrmEntityForm<Concession> {

    public ConcessionEditorForm() {
        this(false);
    }

    public ConcessionEditorForm(boolean viewMode) {
        super(Concession.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().type()), 12).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().term()), 12).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().value()), 6).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().condition()), 8).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().status()), 8).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().approvedBy()), 20).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().effectiveDate()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().expirationDate()), 9).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().description()), 51).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }
}
