/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.shared.IObject;

@Singleton
public class ChargesViewForm extends BaseEntityForm<Charges> {

    @SuppressWarnings("rawtypes")
    private ValueChangeHandler valueChangeHandler;

    public ChargesViewForm() {
        super(Charges.class);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void createContent() {
        // TODO move to constructor
        valueChangeHandler = new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                ChargesSharedCalculation.calculateCharges(getValue());
                setValue(getValue());
            }
        };

        FlowPanel main = new FlowPanel();

        main.add(createHeader(proto().monthlyCharges()));
        main.add(create(proto().monthlyCharges().charges(), this));
        // TODO add totals...

        main.add(createHeader2(proto().monthlyCharges().upgradeCharges()));
        main.add(create(proto().monthlyCharges().upgradeCharges(), this));
        // TODO add totals...

        main.add(createHeader(proto().proRatedCharges()));
        main.add(create(proto().proRatedCharges().charges(), this));
        // TODO add totals...

        main.add(createHeader(proto().applicationCharges()));
        main.add(create(proto().applicationCharges().charges(), this));
        // TODO add totals...

        main.add(createHeader(proto().paymentSplitCharges()));
        main.add(create(proto().paymentSplitCharges().charges(), this));
        // TODO add totals...

        setWidget(main);
    }

    private Widget createHeader(IObject<?> member) {

        return new ViewHeaderDecorator(new HTML("<h4>" + member.getMeta().getCaption() + "</h4>"));
    }

    private Widget createHeader2(IObject<?> member) {

        return new HTML("<h5>" + member.getMeta().getCaption() + "</h5>");
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.getValueClass().equals(ChargeLine.class)) {
            return new ChargeLineFolder(this, (member == proto().monthlyCharges().upgradeCharges()) ? valueChangeHandler : null);
        } else if (member.getValueClass().equals(TenantCharge.class)) {
            return new TenantChargeListFolder(this, valueChangeHandler);
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

}
