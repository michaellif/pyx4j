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

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionForm extends CrmEntityForm<Concession> {

    private final SimplePanel valueHolder = new SimplePanel();

    public ConcessionForm(IForm<Concession> view) {
        super(Concession.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;
        content.setWidget(++row, 0, inject(proto().version().type(), new FormDecoratorBuilder(12).build()));
        content.setWidget(++row, 0, valueHolder);
        content.setWidget(++row, 0, inject(proto().version().term(), new FormDecoratorBuilder(12).build()));
        content.setWidget(++row, 0, inject(proto().version().condition(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().version().mixable(), new FormDecoratorBuilder(5).build()));

        content.setWidget(++row, 0, inject(proto().version().description(), new FormDecoratorBuilder(60).build()));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        content.setWidget(++row, 1, inject(proto().version().effectiveDate(), new FormDecoratorBuilder(9).build()));
        content.setWidget(++row, 1, inject(proto().version().expirationDate(), new FormDecoratorBuilder(9).build()));
        content.setWidget(++row, 1, inject(proto().updated(), new FormDecoratorBuilder(9).build()));

        // tweak:
        get(proto().updated()).setViewable(true);

        get(proto().version().type()).addValueChangeHandler(new ValueChangeHandler<Concession.Type>() {
            @Override
            public void onValueChange(ValueChangeEvent<Concession.Type> event) {
                bindValueEditor(event.getValue(), false);
            }
        });

        selectTab(addTab(content));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        if (getValue() != null) {
            bindValueEditor(getValue().version().type().getValue(), true);
        }
    }

    private void bindValueEditor(Concession.Type valueType, boolean repopulatevalue) {
        CComponent<?> comp = null;
        if (valueType != null) {
            switch (valueType) {
            case monetaryOff:
            case promotionalItem:
                comp = new CMoneyField();
                break;
            case percentageOff:
                comp = new CPercentageField();
                break;
            case free:
                comp = new CMoneyField();
                comp.setEnabled(false);
                break;
            }
        }

        unbind(proto().version().value());

        if (comp != null) {
            valueHolder.setWidget(inject(proto().version().value(), comp, new FormDecoratorBuilder(6).build()));

            if (repopulatevalue && valueType != Concession.Type.free) {
                get(proto().version().value()).populate(getValue().version().value().getValue(BigDecimal.ZERO));
            }
        }
    }
}
