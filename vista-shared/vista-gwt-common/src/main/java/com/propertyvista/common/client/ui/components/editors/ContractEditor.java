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

import java.util.Date;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.property.vendor.Contract;

public class ContractEditor extends CEntityDecoratableEditor<Contract> {

    private static final I18n i18n = I18n.get(ContractEditor.class);

    public ContractEditor() {
        super(Contract.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contractID()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().contractor()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cost()), 10).build());

// TODO : design representation for:
//      main.setWidget(++row, 0, decorate(inject(proto.document()), 50);

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().start()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().end()), 9).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        getValidations();

        return main;
    }

    private void getValidations() {

        get(proto().start()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().end()))); //connects validation of both fields
        get(proto().end()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().start())));

        get(proto().start()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }
                IPrimitive<LogicalDate> end = getValue().end();
                if (end.isNull()) {
                    return null;
                }
                return (value != null) && value.before(end.getValue()) ? null : new ValidationFailure(i18n
                        .tr("Contract Expiry Date should be after its Start Date"));
            }

        });

        get(proto().end()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                if (getValue() == null || getValue().isEmpty()) {
                    return null;
                }
                IPrimitive<LogicalDate> start = getValue().start();
                if (start.isNull()) {
                    return null;
                }
                return (value != null) && value.after(start.getValue()) ? null : new ValidationFailure(i18n
                        .tr("Contract Expiry Date should be after its Start Date"));
            }

        });

    }
}
