/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-27
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.TenantCharge;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;

public class ChargesViewFormBase {

    final CEntityForm<?> masterForm;

    @SuppressWarnings("rawtypes")
    final ValueChangeHandler valueChangeHandler;

    public ChargesViewFormBase(CEntityForm<?> masterForm) {
        this.masterForm = masterForm;
        this.valueChangeHandler = null;
    }

    @SuppressWarnings("rawtypes")
    public ChargesViewFormBase(CEntityForm<?> masterForm, ValueChangeHandler valueChangeHandler) {
        this.masterForm = masterForm;
        this.valueChangeHandler = valueChangeHandler;
    }

    public void createContent(FlowPanel main, Charges member) {

        main.add(createHeader(member.monthlyCharges()));
        main.add(masterForm.create(member.monthlyCharges().charges(), masterForm));

        main.add(createHeader2(member.monthlyCharges().upgradeCharges()));
        main.add(masterForm.create(member.monthlyCharges().upgradeCharges(), masterForm));

        main.add(createTotal(member.monthlyCharges().total()));

        main.add(createHeader(member.proRatedCharges()));
        main.add(masterForm.create(member.proRatedCharges().charges(), masterForm));
        main.add(createTotal(member.proRatedCharges().total()));

        main.add(createHeader(member.applicationCharges()));
        main.add(masterForm.create(member.applicationCharges().charges(), masterForm));
        main.add(createTotal(member.applicationCharges().total()));

        main.add(createHeader(member.paymentSplitCharges()));
        main.add(masterForm.create(member.paymentSplitCharges().charges(), masterForm));
        main.add(createTotal(member.paymentSplitCharges().total()));
    }

    private Widget createHeader(IObject<?> member) {

        return new ViewHeaderDecorator(new HTML("<h4>" + member.getMeta().getCaption() + "</h4>"));
    }

    private Widget createHeader2(IObject<?> member) {

        HTML h = new HTML("<h5>" + member.getMeta().getCaption() + "</h5>");
        h.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        h.getElement().getStyle().setMarginLeft(1, Unit.EM);
        return h;
    }

    private Widget createTotal(Money member) {
        FlowPanel totalRow = new FlowPanel();

        Widget sp = new ViewLineSeparator(0, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
        sp.getElement().getStyle().setPadding(0, Unit.EM);
        sp.getElement().getStyle().setProperty("border", "1px dotted black");
        totalRow.add(sp);

        HTML total = new HTML("<b>" + member.getMeta().getCaption() + "</b>");
        totalRow.add(DecorationUtils.inline(total, "60%", null));
        totalRow.add(DecorationUtils.inline(masterForm.create(member, masterForm), "10%", "right"));
        totalRow.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        return totalRow;
    }

    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.getValueClass().equals(ChargeLine.class)) {
            return new ChargeLineFolder(masterForm);
        } else if (member.getValueClass().equals(ChargeLineSelectable.class)) {
            return new ChargeLineSelectableFolder(masterForm, valueChangeHandler);
        } else if (member.getValueClass().equals(TenantCharge.class)) {
            return new ChargeSplitListFolder(masterForm, valueChangeHandler);
        } else {
            return null;
        }
    }

}