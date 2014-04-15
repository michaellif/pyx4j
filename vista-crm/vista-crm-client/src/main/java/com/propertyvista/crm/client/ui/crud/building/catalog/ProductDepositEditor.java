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
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        CEntityComboBox<ARCode> chargeCodeSelector;

        int row = -1;
        content.setWidget(++row, 0, inject(proto().enabled(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().depositType(), new FormDecoratorBuilder().build()));
        content.setWidget(++row, 0,
                inject(proto().chargeCode(), chargeCodeSelector = new CEntityComboBox<ARCode>(ARCode.class), new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, 2, inject(proto().description(), new FormDecoratorBuilder(true).build()));

        row = 0;
        content.setWidget(++row, 1, inject(proto().valueType(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 1, valueHolder);

        // tweaks:
        chargeCodeSelector.addCriterion(PropertyCriterion.in(chargeCodeSelector.proto().type(), ARCode.Type.deposits()));

        get(proto().valueType()).addValueChangeHandler(new ValueChangeHandler<ValueType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ValueType> event) {
                bindValueEditor(event.getValue(), false);
            }
        });

        get(proto().enabled()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setStateEnabled(event.getValue());
            }
        });

        return content;
    }

    private void setStateEnabled(boolean enabled) {
        ProductDepositEditor.this.setEnabled(enabled);

        get(proto().enabled()).inheritEnabled(false);
        get(proto().enabled()).setEnabled(true);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        bindValueEditor(getValue().valueType().getValue(), true);

        setStateEnabled(getValue().enabled().getValue(false));
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
            valueHolder.setWidget(inject(proto().value(), comp, new FormDecoratorBuilder(6).build()));

            if (repopulatevalue) {
                get(proto().value()).populate(getValue().value().getValue(BigDecimal.ZERO));
            }
        }
    }
}