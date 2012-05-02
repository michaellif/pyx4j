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
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentEditorForm extends CrmEntityForm<LeaseAdjustment> {

    public LeaseAdjustmentEditorForm() {
        this(false);
    }

    public LeaseAdjustmentEditorForm(boolean viewMode) {
        super(LeaseAdjustment.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().receivedDate()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().updated()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createdBy()), 10).build());

        row = 0;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().targetDate()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().tax()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reason()), 25).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().executionType()), 10).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().createdWhen()), 10).build());
        return new CrmScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().id()).setVisible(!getValue().id().isNull());
    }
}