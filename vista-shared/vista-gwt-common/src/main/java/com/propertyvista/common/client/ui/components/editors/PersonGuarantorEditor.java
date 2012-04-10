/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import java.util.Date;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.PersonGuarantor;
import com.propertyvista.domain.util.ValidationUtils;

public class PersonGuarantorEditor extends CEntityDecoratableEditor<PersonGuarantor> {

    protected I18n i18n = I18n.get(PersonGuarantorEditor.class);

    private final boolean twoColumns;

    public PersonGuarantorEditor() {
        this(true);
    }

    public PersonGuarantorEditor(boolean twoColumns) {
        super(PersonGuarantor.class);
        this.twoColumns = twoColumns;
    }

    protected boolean isTwoColumns() {
        return twoColumns;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        int row1 = row;

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().guarantor().customer().person().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().guarantor().customer().person().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().guarantor().customer().person().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().guarantor().customer().person().name().lastName()), 20).build());
        } else {
            main.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().guarantor().customer().person().name(), new CEntityLabel<Name>()), 25)
                            .customLabel(i18n.tr("Guarantor")).build());
            get(proto().guarantor().customer().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().guarantor().customer().person().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().guarantor().customer().person().birthDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 9).build());

        int col = 1;
        if (!isEditable() || !isTwoColumns()) {
            row1 = row;
            col = 0;
        }
        main.setWidget(++row1, col, new DecoratorBuilder(inject(proto().guarantor().customer().person().homePhone()), 15).build());
        main.setWidget(++row1, col, new DecoratorBuilder(inject(proto().guarantor().customer().person().mobilePhone()), 15).build());
        main.setWidget(++row1, col, new DecoratorBuilder(inject(proto().guarantor().customer().person().workPhone()), 15).build());
        main.setWidget(++row1, col, new DecoratorBuilder(inject(proto().guarantor().customer().person().email()), 15).build());

        return main;
    }

    @Override
    public void addValidations() {

        get(proto().guarantor().customer().person().email()).setMandatory(true);

        get(proto().guarantor().customer().person().birthDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                return ValidationUtils.isOlderThen18(value) ? null : new ValidationFailure(i18n.tr("Guarantor Should Be At Least 18 Years Old"));
            }

        });
    }
}