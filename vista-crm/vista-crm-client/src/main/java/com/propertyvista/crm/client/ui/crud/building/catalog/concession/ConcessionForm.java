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

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionForm extends CrmEntityForm<Concession> {

    private static final I18n i18n = I18n.get(ConcessionForm.class);

    private final SimplePanel valueHolder = new SimplePanel();

    public ConcessionForm(IForm<Concession> view) {
        super(Concession.class, view);

        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Left, proto().version().type()).decorate().componentWidth(120);
        formPanel.append(Location.Left, valueHolder);
        formPanel.append(Location.Left, proto().version().term()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().version().condition()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().version().mixable()).decorate().componentWidth(80);

        formPanel.append(Location.Right, proto().version().effectiveDate()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().version().expirationDate()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().updated(), new CDateLabel()).decorate().componentWidth(90);

        formPanel.append(Location.Dual, proto().version().description()).decorate();

        get(proto().version().type()).addValueChangeHandler(new ValueChangeHandler<Concession.Type>() {
            @Override
            public void onValueChange(ValueChangeEvent<Concession.Type> event) {
                bindValueEditor(event.getValue(), false);
            }
        });

        selectTab(addTab(formPanel, i18n.tr("Concession")));
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
        CField<BigDecimal, ?> comp = null;
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
            comp.setDecorator(new FieldDecorator.Builder<>().componentWidth("100px").build());
            valueHolder.setWidget(inject(proto().version().value(), comp));

            if (repopulatevalue && valueType != Concession.Type.free) {
                get(proto().version().value()).populate(getValue().version().value().getValue(BigDecimal.ZERO));
            }
        }
    }
}
