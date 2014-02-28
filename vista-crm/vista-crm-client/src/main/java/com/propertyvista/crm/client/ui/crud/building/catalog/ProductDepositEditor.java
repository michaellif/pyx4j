/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 28, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductDeposit;
import com.propertyvista.domain.financial.offering.ProductDeposit.ValueType;

public class ProductDepositEditor extends CEntityForm<ProductDeposit> {

    private final SimplePanel valueHolder = new SimplePanel();

    public ProductDepositEditor() {
        super(ProductDeposit.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        CEntityComboBox<ARCode> chargeCodeSelector;

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().depositType())).build());
        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().chargeCode(), chargeCodeSelector = new CEntityComboBox<ARCode>(ARCode.class))).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());

        row = -1;
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().valueType()), 10).build());
        content.setWidget(++row, 1, valueHolder);

        // tweaks:
        chargeCodeSelector.addCriterion(PropertyCriterion.in(chargeCodeSelector.proto().type(), ARCode.Type.deposits()));

        get(proto().valueType()).addValueChangeHandler(new ValueChangeHandler<ValueType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ValueType> event) {
                bindValueEditor(event.getValue(), false);
            }
        });

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        bindValueEditor(getValue().valueType().getValue(), true);
    }

    private void bindValueEditor(ValueType valueType, boolean repopulatevalue) {
        if (valueType == null)
            return; // New item

        CComponent<?> comp = null;
        switch (valueType) {
        case Monetary:
            comp = new CMoneyField();
            break;
        case Percentage:
            comp = new CPercentageField();
            break;
        }

        unbind(proto().value());

        if (comp != null) {
            valueHolder.setWidget(new FormDecoratorBuilder(inject(proto().value(), comp), 6).build());

            if (repopulatevalue) {
                get(proto().value()).populate(getValue().value().getValue(BigDecimal.ZERO));
            }
        }
    }
}