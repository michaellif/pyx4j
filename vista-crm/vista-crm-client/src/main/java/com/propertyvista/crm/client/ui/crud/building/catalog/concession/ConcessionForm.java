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
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.concession;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CMoneyPercentCombo;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Concession.Type;

public class ConcessionForm extends CrmEntityForm<Concession> {

    private static final I18n i18n = I18n.get(ConcessionForm.class);

    private final CMoneyPercentCombo moneyPct = new CMoneyPercentCombo();

    public ConcessionForm(IPrimeFormView<Concession, ?> view) {
        super(Concession.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().version().type()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().version().value(), moneyPct).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().version().term()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().version().condition()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().version().mixable()).decorate().componentWidth(80);

        formPanel.append(Location.Right, proto().version().effectiveDate()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().version().expirationDate()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().updated(), new CDateLabel()).decorate().componentWidth(90);

        formPanel.append(Location.Dual, proto().version().description()).decorate();

        get(proto().version().type()).addValueChangeHandler(new ValueChangeHandler<Type>() {
            @Override
            public void onValueChange(ValueChangeEvent<Type> event) {
                syncAmountType(event.getValue());
            }
        });

        selectTab(addTab(formPanel, i18n.tr("Concession")));
        setTabBarVisible(false);
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue() != null) {
            syncAmountType(getValue().version().type().getValue());
        }
    }

    private void syncAmountType(Type type) {
        moneyPct.setAmountType(Type.percentageOff.equals(type) ? ValueType.Percentage : ValueType.Monetary);
        moneyPct.setEnabled(!Type.free.equals(type));
    }
}
