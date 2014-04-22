/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.property.vendor.Contract;

public class ContractEditor extends CForm<Contract> {

    public ContractEditor() {
        super(Contract.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, inject(proto().contractID(), new FieldDecoratorBuilder(20).build()));
        main.setWidget(++row, 0, inject(proto().contractor(), new FieldDecoratorBuilder(20).build()));
        main.setWidget(++row, 0, inject(proto().cost(), new FieldDecoratorBuilder(10).build()));

// TODO : design representation for:
//      main.setWidget(++row, 0, decorate(inject(proto.document()), 50);

        row = -1;
        main.setWidget(++row, 1, inject(proto().start(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 1, inject(proto().end(), new FieldDecoratorBuilder(9).build()));

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        new StartEndDateValidation(get(proto().start()), get(proto().end()));
        get(proto().start()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().end()))); //connects validation of both fields
        get(proto().end()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().start())));

    }
}
