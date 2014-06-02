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

import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CMoneyPercentCombo;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Concession.Type;

public class ConcessionForm extends CrmEntityForm<Concession> {

    private static final I18n i18n = I18n.get(ConcessionForm.class);

    public ConcessionForm(IForm<Concession> view) {
        super(Concession.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().version().type()).decorate().componentWidth(120);
        final CMoneyPercentCombo moneyPct = new CMoneyPercentCombo();
        formPanel.append(Location.Left, proto().version().value(), moneyPct).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().version().term()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().version().condition()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().version().mixable()).decorate().componentWidth(80);

        formPanel.append(Location.Right, proto().version().effectiveDate()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().version().expirationDate()).decorate().componentWidth(90);
        formPanel.append(Location.Right, proto().updated(), new CDateLabel()).decorate().componentWidth(90);

        formPanel.append(Location.Dual, proto().version().description()).decorate();

        get(proto().version().type()).addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                Type type = get(proto().version().type()).getValue();
                moneyPct.setAmountType(Type.percentageOff.equals(type) ? ValueType.Percentage : ValueType.Monetary);
                moneyPct.setEnabled(!Type.free.equals(type));
            }
        });

        selectTab(addTab(formPanel, i18n.tr("Concession")));
        setTabBarVisible(false);
    }
}
