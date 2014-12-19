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
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyPercentCombo;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductDeposit;

public class ProductDepositEditor extends CForm<ProductDeposit> {

    public ProductDepositEditor() {
        super(ProductDeposit.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        CEntityComboBox<ARCode> chargeCodeSelector;

        formPanel.append(Location.Left, proto().enabled()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().depositType()).decorate();
        formPanel.append(Location.Left, proto().chargeCode(), chargeCodeSelector = new CEntityComboBox<ARCode>(ARCode.class)).decorate();

        formPanel.append(Location.Right, proto().valueType()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().value(), new CMoneyPercentCombo()).decorate().componentWidth(100);

        formPanel.append(Location.Dual, proto().description()).decorate();

        // tweaks:
        chargeCodeSelector.addCriterion(PropertyCriterion.in(chargeCodeSelector.proto().type(), ARCode.Type.deposits()));

        get(proto().valueType()).addValueChangeHandler(new ValueChangeHandler<ValueType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ValueType> event) {
                ((CMoneyPercentCombo) get(proto().value())).setAmountType(event.getValue());
            }
        });

        get(proto().enabled()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setStateEnabled(event.getValue());
            }
        });

        return formPanel;
    }

    private void setStateEnabled(boolean enabled) {
        ProductDepositEditor.this.setEnabled(enabled);

        get(proto().enabled()).inheritEnabled(false);
        get(proto().enabled()).setEnabled(true);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setStateEnabled(getValue().enabled().getValue(false));
        // sync value type
        if (getValue() != null) {
            ((CMoneyPercentCombo) get(proto().value())).setAmountType(getValue().valueType().getValue());
        }
    }
}